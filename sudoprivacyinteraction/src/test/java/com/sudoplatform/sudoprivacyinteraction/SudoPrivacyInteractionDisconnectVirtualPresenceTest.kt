/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.UseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence.DisconnectVirtualPresenceUseCase
import com.sudoplatform.sudoprivacyinteraction.types.ProviderType
import com.sudoplatform.sudoprivacyinteraction.types.VirtualPresenceState
import com.sudoplatform.sudouser.SignInGuard
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner
import java.util.Date

/**
 * Test the correct operation of [SudoPrivacyInteractionClient.disconnectVirtualPresence]
 * using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class SudoPrivacyInteractionDisconnectVirtualPresenceTest : BaseTests() {
    private val virtualPresenceEntity by before {
        EntityDataFactory.getVirtualPresenceEntity()
    }

    private val mockVirtualPresenceService by before {
        mock<VirtualPresenceService>()
    }

    private val mockUseCase by before {
        mock<DisconnectVirtualPresenceUseCase>().stub {
            onBlocking {
                execute(any())
            } doReturn virtualPresenceEntity
        }
    }

    private val mockUseCaseFactory by before {
        mock<UseCaseFactory>().stub {
            on { createDisconnectVirtualPresenceUseCase() } doReturn mockUseCase
        }
    }

    private val mockSignInGuard by before {
        mock<SignInGuard>()
    }

    private val client by before {
        DefaultSudoPrivacyInteractionClient(
            context = mockContext,
            apiClient = mockApiClient,
            sudoUserClient = mockUserClient,
            logger = mockLogger,
            virtualPresenceService = mockVirtualPresenceService,
            useCaseFactory = mockUseCaseFactory,
            signInGuard = mockSignInGuard,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(
            mockContext,
            mockUserClient,
            mockApiClient,
            mockUseCaseFactory,
            mockUseCase,
            mockSignInGuard,
        )
    }

    @Test
    fun `disconnectVirtualPresence() should return results when no error present`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.disconnectVirtualPresence(mockVirtualPresenceId)
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldNotBe null
            with(result) {
                id shouldBe mockVirtualPresenceId
                owner shouldBe mockOwner
                providerType shouldBe ProviderType.EMAIL
                identifier shouldBe mockIdentifier
                state shouldBe VirtualPresenceState.CONNECTED
                relationshipProvider shouldBe null
                lastScannedAt shouldBe Date(1L)
                lastScanFailureReason shouldBe null
                version shouldBe 1
                createdAt shouldBe Date(1L)
                updatedAt shouldBe Date(1L)
            }

            verify(mockUseCaseFactory).createDisconnectVirtualPresenceUseCase()
            verify(mockUseCase).execute(eq(mockVirtualPresenceId))
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `disconnectVirtualPresence() should throw when use case throws`() =
        runTest {
            mockUseCase.stub {
                onBlocking {
                    execute(any())
                } doThrow SudoPrivacyInteractionClient.VirtualPresenceException.VirtualPresenceNotFoundException()
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.VirtualPresenceNotFoundException> {
                        client.disconnectVirtualPresence(mockVirtualPresenceId)
                    }
                }
            deferredResult.start()
            deferredResult.await()

            verify(mockUseCaseFactory).createDisconnectVirtualPresenceUseCase()
            verify(mockUseCase).execute(any())
            verify(mockSignInGuard).ensureSignedIn()
        }
}
