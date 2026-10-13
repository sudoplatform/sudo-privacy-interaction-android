/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.client

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sudoplatform.sudoprivacyinteraction.BaseIntegrationTest
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.types.ListOutput
import com.sudoplatform.sudoprivacyinteraction.types.VirtualPresence
import com.sudoplatform.sudoprivacyinteraction.types.VirtualPresenceState
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ConnectVirtualPresenceWithAuthCodeInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ConnectVirtualPresenceWithRefreshTokenInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListVirtualPresencesInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.RescanVirtualPresenceInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ScanOptions
import io.kotlintest.matchers.numerics.shouldBeGreaterThanOrEqual
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.awaitility.Duration
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test the virtual presence lifecycle operations of the [SudoPrivacyInteractionClient].
 */
@RunWith(AndroidJUnit4::class)
class VirtualPresenceIntegrationTest : BaseIntegrationTest() {
    private val connectedIds = mutableSetOf<String>()

    @After
    fun teardown() =
        runBlocking {
            for (id in connectedIds) {
                try {
                    privacyInteractionClient.disconnectVirtualPresence(id)
                } catch (e: Throwable) {
                    logger.debug("Cleanup disconnect failed: $id $e")
                }
            }
            connectedIds.clear()
        }

    @Test
    fun connectsAndDisconnectsVirtualPresenceWithRefreshTokenSuccessfully() =
        runTest {
            assumeTrue(isTestProviderAvailable())
            val relationshipProvider = resolveRelationshipProvider()
            val providerIdentity = generateProviderIdentity()

            // Connect a virtual presence using a refresh token.
            val connectedVp =
                privacyInteractionClient.connectVirtualPresenceWithRefreshToken(
                    ConnectVirtualPresenceWithRefreshTokenInput(
                        refreshToken = testRefreshToken,
                        providerIdentity = providerIdentity,
                        relationshipProvider = relationshipProvider,
                    ),
                )
            connectedIds.add(connectedVp.id)

            connectedVp shouldNotBe null
            connectedVp.id shouldNotBe null
            connectedVp.owner shouldNotBe null
            connectedVp.identifier shouldBe providerIdentity
            connectedVp.state shouldNotBe null
            connectedVp.providerType shouldNotBe null
            connectedVp.version shouldBeGreaterThanOrEqual 1

            // Disconnect the virtual presence.
            val disconnectedVp = privacyInteractionClient.disconnectVirtualPresence(connectedVp.id)
            connectedIds.remove(disconnectedVp.id)

            disconnectedVp shouldNotBe null
            disconnectedVp.id shouldBe connectedVp.id
            disconnectedVp.identifier shouldBe providerIdentity
            disconnectedVp.state shouldBe VirtualPresenceState.INACTIVE
        }

    @Test
    fun connectsVirtualPresenceWithAuthCodeSuccessfully() =
        runTest {
            assumeTrue(isTestProviderAvailable())
            val relationshipProvider = resolveRelationshipProvider()
            val connectedVp =
                privacyInteractionClient.connectVirtualPresenceWithAuthCode(
                    ConnectVirtualPresenceWithAuthCodeInput(
                        authCode = testAuthCode,
                        relationshipProvider = relationshipProvider,
                    ),
                )
            connectedIds.add(connectedVp.id)

            connectedVp shouldNotBe null
            connectedVp.id shouldNotBe null
            connectedVp.owner shouldNotBe null
            connectedVp.state shouldNotBe null
            connectedVp.providerType shouldNotBe null
            connectedVp.version shouldBeGreaterThanOrEqual 1
        }

    @Test
    fun connectsMultipleVirtualPresencesSuccessfully() =
        runTest {
            assumeTrue(isTestProviderAvailable())
            val relationshipProvider = resolveRelationshipProvider()
            val providerIdentity1 = generateProviderIdentity("multi-1")
            val providerIdentity2 = generateProviderIdentity("multi-2")

            val connectedVp1 =
                privacyInteractionClient.connectVirtualPresenceWithRefreshToken(
                    ConnectVirtualPresenceWithRefreshTokenInput(
                        refreshToken = testRefreshToken,
                        providerIdentity = providerIdentity1,
                        relationshipProvider = relationshipProvider,
                    ),
                )
            connectedIds.add(connectedVp1.id)
            connectedVp1 shouldNotBe null
            connectedVp1.identifier shouldBe providerIdentity1

            val connectedVp2 =
                privacyInteractionClient.connectVirtualPresenceWithRefreshToken(
                    ConnectVirtualPresenceWithRefreshTokenInput(
                        refreshToken = testRefreshToken,
                        providerIdentity = providerIdentity2,
                        relationshipProvider = relationshipProvider,
                    ),
                )
            connectedIds.add(connectedVp2.id)
            connectedVp2 shouldNotBe null
            connectedVp2.identifier shouldBe providerIdentity2

            connectedVp1.id shouldNotBe connectedVp2.id
        }

    @Test
    fun throwsWhenDisconnectingNonExistingVirtualPresence() =
        runTest {
            shouldThrowVirtualPresenceException {
                privacyInteractionClient.disconnectVirtualPresence("non-existent-virtual-presence-id")
            }
        }

    @Test
    fun retrievesListOfVirtualPresencesRespectingLimitSuccessfully() =
        runBlocking {
            assumeTrue(isTestProviderAvailable())
            val relationshipProvider = resolveRelationshipProvider()

            val connectedVp1 =
                privacyInteractionClient.connectVirtualPresenceWithRefreshToken(
                    ConnectVirtualPresenceWithRefreshTokenInput(
                        refreshToken = testRefreshToken,
                        providerIdentity = generateProviderIdentity("limit-1"),
                        relationshipProvider = relationshipProvider,
                    ),
                )
            connectedIds.add(connectedVp1.id)

            val connectedVp2 =
                privacyInteractionClient.connectVirtualPresenceWithRefreshToken(
                    ConnectVirtualPresenceWithRefreshTokenInput(
                        refreshToken = testRefreshToken,
                        providerIdentity = generateProviderIdentity("limit-2"),
                        relationshipProvider = relationshipProvider,
                    ),
                )
            connectedIds.add(connectedVp2.id)

            // Wait until both connected virtual presences are listable.
            waitForVirtualPresenceCount(2)

            val result = privacyInteractionClient.listVirtualPresences(ListVirtualPresencesInput(limit = 1))
            result.items.size shouldBe 1
            result.nextToken shouldNotBe null
        }

    @Test
    fun retrievesListOfVirtualPresencesWithPaginationSuccessfully() =
        runBlocking {
            assumeTrue(isTestProviderAvailable())
            val relationshipProvider = resolveRelationshipProvider()

            val connectedVp1 =
                privacyInteractionClient.connectVirtualPresenceWithRefreshToken(
                    ConnectVirtualPresenceWithRefreshTokenInput(
                        refreshToken = testRefreshToken,
                        providerIdentity = generateProviderIdentity("page-1"),
                        relationshipProvider = relationshipProvider,
                    ),
                )
            connectedIds.add(connectedVp1.id)

            // Page through with a limit of 1, collecting all items across pages.
            waitForVirtualPresenceCount(1)
            val firstPage = privacyInteractionClient.listVirtualPresences(ListVirtualPresencesInput(limit = 1))
            firstPage.items.size shouldBe 1

            val connectedVp2 =
                privacyInteractionClient.connectVirtualPresenceWithRefreshToken(
                    ConnectVirtualPresenceWithRefreshTokenInput(
                        refreshToken = testRefreshToken,
                        providerIdentity = generateProviderIdentity("page-2"),
                        relationshipProvider = relationshipProvider,
                    ),
                )
            connectedIds.add(connectedVp2.id)

            waitForVirtualPresenceCount(2)
            val allItems = listAllVirtualPresences()
            val ids = allItems.map { it.id }
            ids shouldContainId connectedVp1.id
            ids shouldContainId connectedVp2.id
        }

    @Test
    fun rescansAConnectedVirtualPresenceSuccessfully() =
        runBlocking {
            assumeTrue(isTestProviderAvailable())
            val relationshipProvider = resolveRelationshipProvider()
            val providerIdentity = generateProviderIdentity("rescan")

            val connectedVp =
                privacyInteractionClient.connectVirtualPresenceWithRefreshToken(
                    ConnectVirtualPresenceWithRefreshTokenInput(
                        refreshToken = testRefreshToken,
                        providerIdentity = providerIdentity,
                        relationshipProvider = relationshipProvider,
                    ),
                )
            connectedIds.add(connectedVp.id)

            val rescannedVp =
                privacyInteractionClient.rescanVirtualPresence(
                    RescanVirtualPresenceInput(id = connectedVp.id),
                )

            rescannedVp shouldNotBe null
            rescannedVp.id shouldBe connectedVp.id
            rescannedVp.identifier shouldBe providerIdentity
            rescannedVp.owner shouldBe connectedVp.owner
            rescannedVp.version shouldBeGreaterThanOrEqual connectedVp.version
        }

    @Test
    fun rescansAVirtualPresenceWithScanOptionsSuccessfully() =
        runBlocking {
            assumeTrue(isTestProviderAvailable())
            val relationshipProvider = resolveRelationshipProvider()
            val providerIdentity = generateProviderIdentity("rescan-options")

            val connectedVp =
                privacyInteractionClient.connectVirtualPresenceWithRefreshToken(
                    ConnectVirtualPresenceWithRefreshTokenInput(
                        refreshToken = testRefreshToken,
                        providerIdentity = providerIdentity,
                        relationshipProvider = relationshipProvider,
                    ),
                )
            connectedIds.add(connectedVp.id)

            val rescannedVp =
                privacyInteractionClient.rescanVirtualPresence(
                    RescanVirtualPresenceInput(
                        id = connectedVp.id,
                        options =
                            ScanOptions(
                                maximumItemsProcessed = 50,
                                earliestScanDate = "2025-01-01",
                                latestScanDate = "2026-06-01",
                                excludeDomains = listOf("spam.com"),
                                excludeCategories = listOf("newsletters"),
                            ),
                    ),
                )

            rescannedVp shouldNotBe null
            rescannedVp.id shouldBe connectedVp.id
            rescannedVp.identifier shouldBe providerIdentity
            rescannedVp.version shouldBeGreaterThanOrEqual connectedVp.version
        }

    private suspend fun listAllVirtualPresences(limit: Int? = null): List<VirtualPresence> {
        val items = mutableListOf<VirtualPresence>()
        var nextToken: String? = null
        do {
            val page: ListOutput<VirtualPresence> =
                privacyInteractionClient.listVirtualPresences(
                    ListVirtualPresencesInput(limit = limit, nextToken = nextToken),
                )
            items.addAll(page.items)
            nextToken = page.nextToken
        } while (nextToken != null)
        return items
    }

    /**
     * Waits until at least [count] virtual presences are listable, accounting for eventual
     * consistency after a connect operation.
     */
    private fun waitForVirtualPresenceCount(count: Int) =
        waitUntil(pollInterval = Duration.TWO_HUNDRED_MILLISECONDS) {
            listAllVirtualPresences().size >= count
        }

    private infix fun List<String>.shouldContainId(id: String) {
        if (!contains(id)) {
            throw AssertionError("Expected list to contain id '$id' but was $this")
        }
    }

    private inline fun shouldThrowVirtualPresenceException(block: () -> Unit) {
        try {
            block()
        } catch (e: SudoPrivacyInteractionClient.VirtualPresenceException) {
            return
        }
        throw AssertionError("Expected VirtualPresenceException to be thrown")
    }
}
