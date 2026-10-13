/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import android.content.Context
import android.os.StrictMode
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.sudoplatform.sudoentitlements.SudoEntitlementsClient
import com.sudoplatform.sudoentitlementsadmin.SudoEntitlementsAdminClient
import com.sudoplatform.sudoentitlementsadmin.types.Entitlement
import com.sudoplatform.sudokeymanager.KeyManagerFactory
import com.sudoplatform.sudologging.AndroidUtilsLogDriver
import com.sudoplatform.sudologging.LogLevel
import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResult
import com.sudoplatform.sudoprivacyinteraction.types.AvailableAction
import com.sudoplatform.sudoprivacyinteraction.types.DataHolder
import com.sudoplatform.sudoprivacyinteraction.types.RelationshipProvider
import com.sudoplatform.sudoprivacyinteraction.types.VirtualPresence
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ConnectVirtualPresenceWithRefreshTokenInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListAnalysisResultsInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListAvailableActionsInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListDataHoldersInput
import com.sudoplatform.sudouser.SudoUserClient
import com.sudoplatform.sudouser.TESTAuthenticationProvider
import io.kotlintest.shouldBe
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.awaitility.Duration
import org.awaitility.kotlin.await
import org.awaitility.kotlin.has
import org.awaitility.kotlin.untilCallTo
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import timber.log.Timber
import java.util.UUID

/**
 * Base class for the [SudoPrivacyInteractionClient] integration tests.
 *
 * Handles the register / sign in / entitlement lifecycle once per test class and exposes shared
 * helpers to the concrete test suites.
 */
abstract class BaseIntegrationTest {
    protected val testProviderIdentity = "integration-test@example.com"
    protected val testRefreshToken = "test-refresh-token"
    protected val testAuthCode = "test-auth-code"

    companion object {
        val context: Context = ApplicationProvider.getApplicationContext()

        private const val VERBOSE = false
        private val logLevel = if (VERBOSE) LogLevel.VERBOSE else LogLevel.INFO
        val logger =
            com.sudoplatform.sudologging.Logger("pim-test", AndroidUtilsLogDriver(logLevel))

        @JvmStatic
        protected val entitlements =
            listOf(
                Entitlement("sudoplatform.privacy-interaction.serviceUserEntitled", "test", 1),
                Entitlement("sudoplatform.privacy-interaction.virtualPresenceMaxPerUser", "test", 5),
            )

        @JvmStatic
        protected val userClient by lazy {
            SudoUserClient
                .builder(context)
                .setNamespace("pim-client-test")
                .build()
        }

        @JvmStatic
        protected val entitlementsClient by lazy {
            SudoEntitlementsClient
                .builder()
                .setContext(context)
                .setSudoUserClient(userClient)
                .build()
        }

        @JvmStatic
        protected val entitlementsAdminClient by lazy {
            val adminApiKey = readArgument("ADMIN_API_KEY", "api.key")
            SudoEntitlementsAdminClient.builder(context, adminApiKey).build()
        }

        @JvmStatic
        protected val keyManager by lazy {
            KeyManagerFactory(context).createAndroidKeyManager("pim-client-test")
        }

        @JvmStatic
        protected lateinit var privacyInteractionClient: SudoPrivacyInteractionClient

        private fun readTextFile(fileName: String): String =
            context.assets.open(fileName).bufferedReader().use {
                it.readText().trim()
            }

        @JvmStatic
        protected fun readArgument(
            argumentName: String,
            fallbackFileName: String?,
        ): String {
            val argumentValue =
                InstrumentationRegistry.getArguments().getString(argumentName)?.trim()
            if (argumentValue != null) {
                return argumentValue
            }
            if (fallbackFileName != null) {
                return readTextFile(fallbackFileName)
            }
            throw IllegalArgumentException("$argumentName property not found")
        }

        private suspend fun register() {
            userClient.isRegistered() shouldBe false

            val privateKey = readArgument("REGISTER_KEY", "register_key.private")
            val keyId = readArgument("REGISTER_KEY_ID", "register_key.id")

            val authProvider =
                TESTAuthenticationProvider(
                    name = "pim-client-test",
                    privateKey = privateKey,
                    publicKey = null,
                    keyManager = keyManager,
                    keyId = keyId,
                )

            userClient.registerWithAuthenticationProvider(authProvider, "pim-client-test")
        }

        @JvmStatic
        protected suspend fun deregister() {
            userClient.deregister()
        }

        private suspend fun signIn() {
            userClient.signInWithKey()
        }

        private suspend fun registerAndSignIn() {
            userClient.isRegistered() shouldBe false
            register()
            userClient.isRegistered() shouldBe true
            signIn()
            userClient.isSignedIn() shouldBe true
        }

        @JvmStatic
        protected suspend fun registerSignInAndEntitle() {
            registerAndSignIn()
            val externalId = entitlementsClient.getExternalId()
            entitlementsAdminClient.applyEntitlementsToUser(externalId, entitlements)
            entitlementsClient.redeemEntitlements()
        }

        @BeforeClass
        @JvmStatic
        fun init() {
            Timber.plant(Timber.DebugTree())
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy
                    .Builder()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build(),
            )

            userClient.reset()

            privacyInteractionClient =
                SudoPrivacyInteractionClient
                    .builder()
                    .setContext(context)
                    .setSudoUserClient(userClient)
                    .setLogger(logger)
                    .build()

            runTest {
                registerSignInAndEntitle()
            }
        }

        @AfterClass
        @JvmStatic
        fun fini() =
            runTest {
                if (userClient.isRegistered()) {
                    deregister()
                }
                privacyInteractionClient.close()
                userClient.reset()
                Timber.uprootAll()
            }
    }

    /**
     * Generates a unique provider identity so that connected virtual presences do not collide
     * across test executions.
     */
    protected fun generateProviderIdentity(prefix: String = "integration-test"): String = "$prefix-${UUID.randomUUID()}@example.com"

    /**
     * Ids of virtual presences connected via [connectVirtualPresence]. Disconnected automatically
     * after each test by [disconnectConnectedVirtualPresences].
     */
    private val connectedVirtualPresenceIds = mutableSetOf<String>()

    /**
     * Connects a virtual presence using a refresh token and registers it for automatic cleanup
     * after the test.
     *
     * @param prefix [String] A prefix used to generate a unique provider identity.
     * @return The connected [VirtualPresence].
     */
    protected suspend fun connectVirtualPresence(prefix: String = "integration-test"): VirtualPresence {
        val relationshipProvider = resolveRelationshipProvider()
        val connectedVp =
            privacyInteractionClient.connectVirtualPresenceWithRefreshToken(
                ConnectVirtualPresenceWithRefreshTokenInput(
                    refreshToken = testRefreshToken,
                    providerIdentity = generateProviderIdentity(prefix),
                    relationshipProvider = relationshipProvider,
                ),
            )
        connectedVirtualPresenceIds.add(connectedVp.id)
        return connectedVp
    }

    /**
     * Disconnects any virtual presences connected via [connectVirtualPresence] during the test,
     * tolerating failures (e.g. a presence already disconnected by the test itself).
     */
    @After
    fun disconnectConnectedVirtualPresences() =
        runBlocking {
            for (id in connectedVirtualPresenceIds) {
                try {
                    privacyInteractionClient.disconnectVirtualPresence(id)
                } catch (e: Throwable) {
                    logger.debug("Cleanup disconnect failed: $id $e")
                }
            }
            connectedVirtualPresenceIds.clear()
        }

    /**
     * Resolves the relationship provider to use when connecting a virtual presence based on the
     * provider configuration returned by the service.
     *
     * Prefers the `TEST_PROVIDER` whenever it is available. If absent, fall back to the
     * provider keyed by the `name` parameter (i.e. `GMAIL_PROVIDER`).
     *
     * @throws IllegalStateException if the service returns no usable provider configuration.
     */
    protected suspend fun resolveRelationshipProvider(): RelationshipProvider {
        val configs = privacyInteractionClient.getProviderConfiguration()
        check(configs.isNotEmpty()) { "No provider configuration available from the service" }

        val hasTestProvider = configs.any { it.name.lowercase() == "default" }
        if (hasTestProvider) {
            return RelationshipProvider.TEST_PROVIDER
        }
        val isGoogle = configs.any { it.name.lowercase().contains("google") }
        return if (isGoogle) RelationshipProvider.GMAIL_PROVIDER else RelationshipProvider.TEST_PROVIDER
    }

    /**
     * Whether the test provider (`default` configuration) is available in the current environment.
     * Used to guard tests that can only run against the test provider.
     */
    protected suspend fun isTestProviderAvailable(): Boolean =
        privacyInteractionClient.getProviderConfiguration().any {
            it.name.lowercase() ==
                "default"
        }

    /**
     * Polls [condition] until it returns `true` or [timeout] elapses, accounting for the eventual
     * consistency of the service after a mutating operation.
     */
    protected fun waitUntil(
        timeout: Duration = Duration.ONE_MINUTE,
        pollInterval: Duration = Duration.FIVE_SECONDS,
        condition: suspend () -> Boolean,
    ) {
        await
            .atMost(timeout)
            .pollInterval(pollInterval)
            .untilCallTo {
                runBlocking { condition() }
            } has { this }
    }

    /**
     * Polls [condition] until it returns `true` (or [timeout] elapses) and then produces a value
     * via [value]. Unlike [waitUntil] this is best-effort: if the condition is never satisfied
     * within [timeout], the timeout is swallowed (and logged) and `null` is returned, allowing a
     * caller to skip assertions that depend on data the pipeline did not produce in time.
     */
    protected fun <T> awaitValueOrNull(
        timeout: Duration = Duration.ONE_MINUTE,
        pollInterval: Duration = Duration.FIVE_SECONDS,
        condition: suspend () -> Boolean,
        value: suspend () -> T?,
    ): T? =
        try {
            waitUntil(timeout = timeout, pollInterval = pollInterval, condition = condition)
            runBlocking { value() }
        } catch (e: Throwable) {
            logger.debug("awaitValueOrNull condition not satisfied within $timeout: $e")
            null
        }

    /**
     * Polls [list] until it returns a non-empty list (or [timeout] elapses) and returns its first
     * element, or `null` if the list never became non-empty within [timeout]. Best-effort: a
     * timeout is swallowed (and logged) so callers can skip assertions that depend on data the
     * pipeline did not produce in time.
     *
     * @param list [suspend] Produces the list to inspect on each poll.
     * @param timeout [Duration] Maximum time to wait. Defaults to one minute.
     * @param pollInterval [Duration] Interval between polls. Defaults to five seconds.
     * @return The first element of the list, or `null` if the list did not become non-empty.
     */
    protected fun <T> awaitFirstOrNull(
        timeout: Duration = Duration.ONE_MINUTE,
        pollInterval: Duration = Duration.FIVE_SECONDS,
        list: suspend () -> List<T>,
    ): T? =
        awaitValueOrNull(
            timeout = timeout,
            pollInterval = pollInterval,
            condition = { list().isNotEmpty() },
            value = { list().firstOrNull() },
        )

    protected suspend fun listDataHolders(virtualPresenceId: String): List<DataHolder> =
        privacyInteractionClient
            .listDataHolders(ListDataHoldersInput(virtualPresenceId = virtualPresenceId))
            .items

    protected suspend fun listAnalysisResults(virtualPresenceId: String): List<AnalysisResult> =
        privacyInteractionClient
            .listAnalysisResults(ListAnalysisResultsInput(virtualPresenceId = virtualPresenceId))
            .items

    protected suspend fun listAvailableActions(dataHolderId: String): List<AvailableAction> =
        privacyInteractionClient
            .listAvailableActions(ListAvailableActionsInput(dataHolderId = dataHolderId))
            .items

    /**
     * Waits for the discovery pipeline to produce at least one data holder for [virtualPresenceId]
     * and returns its id, or `null` if none was discovered within [timeout].
     */
    protected fun awaitFirstDataHolderId(
        virtualPresenceId: String,
        timeout: Duration = Duration.ONE_MINUTE,
    ): String? = awaitFirstOrNull(timeout = timeout) { listDataHolders(virtualPresenceId) }?.id

    /**
     * Asserts that this list contains [element], throwing an [AssertionError] otherwise.
     */
    protected infix fun <T> List<T>.shouldContain(element: T) {
        if (!contains(element)) {
            throw AssertionError("Expected list to contain '$element' but was $this")
        }
    }
}
