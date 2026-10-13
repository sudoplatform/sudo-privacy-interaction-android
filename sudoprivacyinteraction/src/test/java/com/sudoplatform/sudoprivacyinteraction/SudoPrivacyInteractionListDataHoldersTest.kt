/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.ListDataHoldersOutput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.UseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder.ListDataHoldersUseCase
import com.sudoplatform.sudoprivacyinteraction.types.DataHolderProtectionState
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListDataHoldersInput
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
 * Test the correct operation of [SudoPrivacyInteractionClient.listDataHolders]
 * using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class SudoPrivacyInteractionListDataHoldersTest : BaseTests() {
    private val useCaseOutput by before {
        ListDataHoldersOutput(
            items = listOf(EntityDataFactory.getDataHolderEntity()),
            nextToken = "mockNextToken",
        )
    }

    private val mockDataHolderService by before {
        mock<DataHolderService>()
    }

    private val mockUseCase by before {
        mock<ListDataHoldersUseCase>().stub {
            onBlocking {
                execute(any())
            } doReturn useCaseOutput
        }
    }

    private val mockUseCaseFactory by before {
        mock<UseCaseFactory>().stub {
            on { createListDataHoldersUseCase() } doReturn mockUseCase
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
            dataHolderService = mockDataHolderService,
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
    fun `listDataHolders() should return results when no error present`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.listDataHolders(
                        ListDataHoldersInput(
                            virtualPresenceId = mockVirtualPresenceId,
                            limit = 10,
                            nextToken = "inputToken",
                        ),
                    )
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldNotBe null
            result.items.size shouldBe 1
            result.items[0].id shouldBe "mockDataHolderId"
            result.items[0].virtualPresenceId shouldBe mockVirtualPresenceId
            result.items[0].protectionState shouldBe DataHolderProtectionState.MONITORED
            result.nextToken shouldBe "mockNextToken"

            verify(mockUseCaseFactory).createListDataHoldersUseCase()
            verify(mockUseCase).execute(
                check { useCaseInput ->
                    useCaseInput.virtualPresenceId shouldBe mockVirtualPresenceId
                    useCaseInput.limit shouldBe 10
                    useCaseInput.nextToken shouldBe "inputToken"
                },
            )
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `listDataHolders() should throw when use case throws`() =
        runTest {
            mockUseCase.stub {
                onBlocking {
                    execute(any())
                } doThrow SudoPrivacyInteractionClient.DataHolderException.FailedException()
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    shouldThrow<SudoPrivacyInteractionClient.DataHolderException.FailedException> {
                        client.listDataHolders(ListDataHoldersInput(virtualPresenceId = mockVirtualPresenceId))
                    }
                }
            deferredResult.start()
            deferredResult.await()

            verify(mockUseCaseFactory).createListDataHoldersUseCase()
            verify(mockUseCase).execute(any())
            verify(mockSignInGuard).ensureSignedIn()
        }
}
