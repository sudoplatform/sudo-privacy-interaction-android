/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.UseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence.RescanVirtualPresenceUseCase
import com.sudoplatform.sudoprivacyinteraction.types.ProviderType
import com.sudoplatform.sudoprivacyinteraction.types.VirtualPresenceState
import com.sudoplatform.sudoprivacyinteraction.types.inputs.RescanVirtualPresenceInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ScanOptions
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
import org.mockito.kotlin.check
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner
import java.util.Date

/**
 * Test the correct operation of [SudoPrivacyInteractionClient.rescanVirtualPresence]
 * using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class SudoPrivacyInteractionRescanVirtualPresenceTest : BaseTests() {
    private val input by before {
        RescanVirtualPresenceInput(
            id = mockVirtualPresenceId,
            options =
                ScanOptions(
                    maximumItemsProcessed = 50,
                    earliestScanDate = "2025-01-01",
                    latestScanDate = "2026-06-01",
                    excludeDomains = listOf("spam.com"),
                    excludeCategories = listOf("newsletters"),
                ),
        )
    }

    private val virtualPresenceEntity by before {
        EntityDataFactory.getVirtualPresenceEntity()
    }

    private val mockVirtualPresenceService by before {
        mock<VirtualPresenceService>()
    }

    private val mockUseCase by before {
        mock<RescanVirtualPresenceUseCase>().stub {
            onBlocking {
                execute(any())
            } doReturn virtualPresenceEntity
        }
    }

    private val mockUseCaseFactory by before {
        mock<UseCaseFactory>().stub {
            on { createRescanVirtualPresenceUseCase() } doReturn mockUseCase
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
    fun `rescanVirtualPresence() should return results when no error present`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.rescanVirtualPresence(input)
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
                lastScannedAt shouldBe Date(1L)
                version shouldBe 1
            }

            verify(mockUseCaseFactory).createRescanVirtualPresenceUseCase()
            verify(mockUseCase).execute(
                check { useCaseInput ->
                    useCaseInput.id shouldBe mockVirtualPresenceId
                    useCaseInput.options shouldNotBe null
                    useCaseInput.options!!.maximumItemsProcessed shouldBe 50
                    useCaseInput.options.earliestScanDate shouldBe "2025-01-01"
                    useCaseInput.options.latestScanDate shouldBe "2026-06-01"
                    useCaseInput.options.excludeDomains shouldBe listOf("spam.com")
                    useCaseInput.options.excludeCategories shouldBe listOf("newsletters")
                },
            )
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `rescanVirtualPresence() should throw when use case throws`() =
        runTest {
            mockUseCase.stub {
                onBlocking {
                    execute(any())
                } doThrow SudoPrivacyInteractionClient.VirtualPresenceException.VirtualPresenceNotFoundException()
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.VirtualPresenceNotFoundException> {
                        client.rescanVirtualPresence(input)
                    }
                }
            deferredResult.start()
            deferredResult.await()

            verify(mockUseCaseFactory).createRescanVirtualPresenceUseCase()
            verify(mockUseCase).execute(any())
            verify(mockSignInGuard).ensureSignedIn()
        }
}
