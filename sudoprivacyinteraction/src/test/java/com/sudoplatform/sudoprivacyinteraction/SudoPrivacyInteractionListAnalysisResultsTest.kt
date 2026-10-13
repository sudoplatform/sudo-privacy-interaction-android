/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ListAnalysisResultsOutput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.UseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.analysisResult.ListAnalysisResultsUseCase
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListAnalysisResultsInput
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
 * Test the correct operation of [SudoPrivacyInteractionClient.listAnalysisResults]
 * using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class SudoPrivacyInteractionListAnalysisResultsTest : BaseTests() {
    private val useCaseOutput by before {
        ListAnalysisResultsOutput(
            items = listOf(EntityDataFactory.getAnalysisResultEntity()),
            nextToken = "mockNextToken",
        )
    }

    private val mockAnalysisResultService by before {
        mock<AnalysisResultService>()
    }

    private val mockUseCase by before {
        mock<ListAnalysisResultsUseCase>().stub {
            onBlocking { execute(any()) } doReturn useCaseOutput
        }
    }

    private val mockUseCaseFactory by before {
        mock<UseCaseFactory>().stub {
            on { createListAnalysisResultsUseCase() } doReturn mockUseCase
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
            analysisResultService = mockAnalysisResultService,
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
    fun `listAnalysisResults() should return results when no error present`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.listAnalysisResults(
                        ListAnalysisResultsInput(
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
            result.items[0].id shouldBe "mockAnalysisResultId"
            result.items[0].virtualPresenceId shouldBe mockVirtualPresenceId
            result.nextToken shouldBe "mockNextToken"

            verify(mockUseCaseFactory).createListAnalysisResultsUseCase()
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
    fun `listAnalysisResults() should throw when use case throws`() =
        runTest {
            mockUseCase.stub {
                onBlocking {
                    execute(any())
                } doThrow SudoPrivacyInteractionClient.AnalysisResultException.FailedException()
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    shouldThrow<SudoPrivacyInteractionClient.AnalysisResultException.FailedException> {
                        client.listAnalysisResults(ListAnalysisResultsInput(virtualPresenceId = mockVirtualPresenceId))
                    }
                }
            deferredResult.start()
            deferredResult.await()

            verify(mockUseCaseFactory).createListAnalysisResultsUseCase()
            verify(mockUseCase).execute(any())
            verify(mockSignInGuard).ensureSignedIn()
        }
}
