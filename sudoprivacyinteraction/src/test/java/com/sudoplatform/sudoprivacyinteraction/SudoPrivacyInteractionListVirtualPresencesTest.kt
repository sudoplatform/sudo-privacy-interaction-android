/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ListVirtualPresencesOutput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.UseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence.ListVirtualPresencesUseCase
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListVirtualPresencesInput
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

/**
 * Test the correct operation of [SudoPrivacyInteractionClient.listVirtualPresences]
 * using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class SudoPrivacyInteractionListVirtualPresencesTest : BaseTests() {
    private val useCaseOutput by before {
        ListVirtualPresencesOutput(
            items = listOf(EntityDataFactory.getVirtualPresenceEntity()),
            nextToken = "mockNextToken",
        )
    }

    private val mockVirtualPresenceService by before {
        mock<VirtualPresenceService>()
    }

    private val mockUseCase by before {
        mock<ListVirtualPresencesUseCase>().stub {
            onBlocking {
                execute(any())
            } doReturn useCaseOutput
        }
    }

    private val mockUseCaseFactory by before {
        mock<UseCaseFactory>().stub {
            on { createListVirtualPresencesUseCase() } doReturn mockUseCase
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
    fun `listVirtualPresences() should return results when no error present`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.listVirtualPresences(ListVirtualPresencesInput(limit = 10, nextToken = "inputToken"))
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldNotBe null
            result.items.size shouldBe 1
            result.items[0].id shouldBe mockVirtualPresenceId
            result.nextToken shouldBe "mockNextToken"

            verify(mockUseCaseFactory).createListVirtualPresencesUseCase()
            verify(mockUseCase).execute(
                check { useCaseInput ->
                    useCaseInput.limit shouldBe 10
                    useCaseInput.nextToken shouldBe "inputToken"
                },
            )
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `listVirtualPresences() should throw when use case throws`() =
        runTest {
            mockUseCase.stub {
                onBlocking {
                    execute(any())
                } doThrow SudoPrivacyInteractionClient.VirtualPresenceException.FailedException()
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.FailedException> {
                        client.listVirtualPresences(ListVirtualPresencesInput())
                    }
                }
            deferredResult.start()
            deferredResult.await()

            verify(mockUseCaseFactory).createListVirtualPresencesUseCase()
            verify(mockUseCase).execute(any())
            verify(mockSignInGuard).ensureSignedIn()
        }
}
