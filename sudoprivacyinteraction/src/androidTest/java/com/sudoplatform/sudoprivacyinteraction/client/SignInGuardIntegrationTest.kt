/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.client

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sudoplatform.sudoprivacyinteraction.BaseIntegrationTest
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListVirtualPresencesInput
import com.sudoplatform.sudouser.SudoPlatformSignInCallback
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for the sign-in guard / callback mechanism on [SudoPrivacyInteractionClient].
 *
 * Uses `listVirtualPresences` as a simple operation to exercise the guard since it requires no
 * additional setup. A dedicated client instance is built per test so the guard state is isolated
 * from the shared client set up by [BaseIntegrationTest].
 */
@RunWith(AndroidJUnit4::class)
class SignInGuardIntegrationTest : BaseIntegrationTest() {
    private lateinit var guardedClient: SudoPrivacyInteractionClient

    @Before
    fun setUp() {
        guardedClient =
            SudoPrivacyInteractionClient
                .builder()
                .setContext(context)
                .setSudoUserClient(userClient)
                .setLogger(logger)
                .build()
    }

    @After
    fun tearDown() {
        runBlocking<Unit> {
            // Ensure we are signed in again for subsequent test classes.
            if (userClient.isRegistered() && !userClient.isSignedIn()) {
                userClient.signInWithKey()
            }
        }
        guardedClient.reset()
    }

    @Test
    fun signedInWithNoCallbackShouldSucceed() =
        runBlocking<Unit> {
            userClient.isSignedIn() shouldBe true

            val result = guardedClient.listVirtualPresences(ListVirtualPresencesInput())
            result shouldNotBe null
        }

    @Test
    fun signedInWithCallbackSetShouldSucceedWithoutInvokingCallback() =
        runBlocking<Unit> {
            userClient.isSignedIn() shouldBe true

            var callbackInvoked = false
            guardedClient.setSignInCallback(
                object : SudoPlatformSignInCallback {
                    override suspend fun signIn() {
                        callbackInvoked = true
                    }
                },
            )

            val result = guardedClient.listVirtualPresences(ListVirtualPresencesInput())
            result shouldNotBe null
            callbackInvoked shouldBe false
        }

    @Test
    fun notSignedInWithNoCallbackShouldThrow() =
        runBlocking<Unit> {
            userClient.signOut()
            userClient.isSignedIn() shouldBe false

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException> {
                guardedClient.listVirtualPresences(ListVirtualPresencesInput())
            }
        }

    @Test
    fun notSignedInWithCallbackShouldInvokeCallbackAndSucceedAfterSignIn() =
        runBlocking<Unit> {
            userClient.signOut()
            userClient.isSignedIn() shouldBe false

            var callbackInvoked = false
            guardedClient.setSignInCallback(
                object : SudoPlatformSignInCallback {
                    override suspend fun signIn() {
                        callbackInvoked = true
                        // Sign back in when the callback is invoked.
                        userClient.signInWithKey()
                    }
                },
            )

            // The guard should detect we're not signed in, invoke the callback (which signs us
            // back in), then proceed with the operation.
            val result = guardedClient.listVirtualPresences(ListVirtualPresencesInput())
            result shouldNotBe null
            callbackInvoked shouldBe true
        }

    @Test
    fun callbackExceptionShouldPropagateToCallerWhenNotSignedIn() =
        runBlocking<Unit> {
            userClient.signOut()
            userClient.isSignedIn() shouldBe false

            guardedClient.setSignInCallback(
                object : SudoPlatformSignInCallback {
                    override suspend fun signIn(): Unit = throw IllegalStateException("Simulated sign-in failure")
                },
            )

            val exception =
                shouldThrow<IllegalStateException> {
                    guardedClient.listVirtualPresences(ListVirtualPresencesInput())
                }
            exception.message shouldBe "Simulated sign-in failure"
        }

    @Test
    fun clearingCallbackShouldStopEnforcement() =
        runBlocking<Unit> {
            userClient.signOut()
            userClient.isSignedIn() shouldBe false

            var callbackInvoked = false
            guardedClient.setSignInCallback(
                object : SudoPlatformSignInCallback {
                    override suspend fun signIn() {
                        callbackInvoked = true
                    }
                },
            )

            // Clear the callback — sign-in checking is disabled.
            guardedClient.setSignInCallback(null)

            // With the callback cleared the guard is a no-op; the operation proceeds and surfaces
            // the service's own not-signed-in error rather than invoking the callback.
            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException> {
                guardedClient.listVirtualPresences(ListVirtualPresencesInput())
            }
            callbackInvoked shouldBe false
        }
}
