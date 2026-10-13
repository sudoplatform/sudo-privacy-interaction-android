/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.UseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.analysisResult.GetAnalysisResultUseCase
import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResultStatus
import com.sudoplatform.sudoprivacyinteraction.types.DataCategory
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

/**
 * Test the correct operation of [SudoPrivacyInteractionClient.getAnalysisResult]
 * using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class SudoPrivacyInteractionGetAnalysisResultTest : BaseTests() {
    private val mockAnalysisResultId = "mockAnalysisResultId"

    private val mockAnalysisResultService by before {
        mock<AnalysisResultService>()
    }

    private val mockUseCase by before {
        mock<GetAnalysisResultUseCase>().stub {
            onBlocking { execute(any()) } doReturn EntityDataFactory.getAnalysisResultEntity()
        }
    }

    private val mockUseCaseFactory by before {
        mock<UseCaseFactory>().stub {
            on { createGetAnalysisResultUseCase() } doReturn mockUseCase
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
    fun `getAnalysisResult() should return mapped result with data when no error present`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.getAnalysisResult(mockAnalysisResultId)
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldNotBe null
            result!!.id shouldBe mockAnalysisResultId
            result.status shouldBe AnalysisResultStatus.COMPLETE
            result.data shouldNotBe null
            result.data!!.categories[0].category shouldBe DataCategory.LOCATION
            result.data!!.privacyScore?.score shouldBe 82.5

            verify(mockUseCaseFactory).createGetAnalysisResultUseCase()
            verify(mockUseCase).execute(eq(mockAnalysisResultId))
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `getAnalysisResult() should return null when use case returns null`() =
        runTest {
            mockUseCase.stub {
                onBlocking { execute(any()) } doReturn null
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.getAnalysisResult(mockAnalysisResultId)
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldBe null

            verify(mockUseCaseFactory).createGetAnalysisResultUseCase()
            verify(mockUseCase).execute(eq(mockAnalysisResultId))
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `getAnalysisResult() should throw when use case throws`() =
        runTest {
            mockUseCase.stub {
                onBlocking {
                    execute(any())
                } doThrow SudoPrivacyInteractionClient.AnalysisResultException.FailedException()
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    shouldThrow<SudoPrivacyInteractionClient.AnalysisResultException.FailedException> {
                        client.getAnalysisResult(mockAnalysisResultId)
                    }
                }
            deferredResult.start()
            deferredResult.await()

            verify(mockUseCaseFactory).createGetAnalysisResultUseCase()
            verify(mockUseCase).execute(any())
            verify(mockSignInGuard).ensureSignedIn()
        }
}
