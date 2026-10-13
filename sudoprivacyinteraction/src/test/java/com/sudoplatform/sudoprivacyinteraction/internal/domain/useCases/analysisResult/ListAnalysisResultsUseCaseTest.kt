/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.analysisResult

import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ListAnalysisResultsOutput
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
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
 * Test the correct operation of [ListAnalysisResultsUseCase] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class ListAnalysisResultsUseCaseTest : BaseTests() {
    private val mockAnalysisResultService by before {
        mock<AnalysisResultService>()
    }

    private val instanceUnderTest by before {
        ListAnalysisResultsUseCase(
            analysisResultService = mockAnalysisResultService,
            logger = mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockAnalysisResultService)
    }

    @Test
    fun `execute() should return the list output when successful`() =
        runTest {
            val output =
                ListAnalysisResultsOutput(
                    items = listOf(EntityDataFactory.getAnalysisResultEntity()),
                    nextToken = "mockNextToken",
                )
            mockAnalysisResultService.stub {
                onBlocking { list(any()) } doReturn output
            }

            val result =
                instanceUnderTest.execute(
                    ListAnalysisResultsUseCaseInput(
                        virtualPresenceId = mockVirtualPresenceId,
                        limit = 10,
                        nextToken = "inputToken",
                    ),
                )

            result shouldNotBe null
            result shouldBe output

            verify(mockAnalysisResultService).list(
                check { input ->
                    input.virtualPresenceId shouldBe mockVirtualPresenceId
                    input.limit shouldBe 10
                    input.nextToken shouldBe "inputToken"
                },
            )
        }

    @Test
    fun `execute() should pass through null optional fields`() =
        runTest {
            val output = ListAnalysisResultsOutput(items = emptyList())
            mockAnalysisResultService.stub {
                onBlocking { list(any()) } doReturn output
            }

            instanceUnderTest.execute(ListAnalysisResultsUseCaseInput(virtualPresenceId = mockVirtualPresenceId))

            verify(mockAnalysisResultService).list(
                check { input ->
                    input.virtualPresenceId shouldBe mockVirtualPresenceId
                    input.limit shouldBe null
                    input.nextToken shouldBe null
                },
            )
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockAnalysisResultService.stub {
                onBlocking {
                    list(any())
                } doThrow SudoPrivacyInteractionClient.AnalysisResultException.FailedException()
            }

            shouldThrow<SudoPrivacyInteractionClient.AnalysisResultException.FailedException> {
                instanceUnderTest.execute(ListAnalysisResultsUseCaseInput(virtualPresenceId = mockVirtualPresenceId))
            }

            verify(mockAnalysisResultService).list(any())
        }
}
