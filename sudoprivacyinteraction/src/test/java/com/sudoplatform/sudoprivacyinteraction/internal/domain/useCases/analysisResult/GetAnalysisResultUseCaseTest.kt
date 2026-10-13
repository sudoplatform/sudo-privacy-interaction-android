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
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
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
 * Test the correct operation of [GetAnalysisResultUseCase] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class GetAnalysisResultUseCaseTest : BaseTests() {
    private val mockAnalysisResultId = "mockAnalysisResultId"

    private val mockAnalysisResultService by before {
        mock<AnalysisResultService>()
    }

    private val instanceUnderTest by before {
        GetAnalysisResultUseCase(
            analysisResultService = mockAnalysisResultService,
            logger = mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockAnalysisResultService)
    }

    @Test
    fun `execute() should return the analysis result entity when successful`() =
        runTest {
            val entity = EntityDataFactory.getAnalysisResultEntity()
            mockAnalysisResultService.stub {
                onBlocking { get(any()) } doReturn entity
            }

            val result = instanceUnderTest.execute(mockAnalysisResultId)

            result shouldNotBe null
            result shouldBe entity

            verify(mockAnalysisResultService).get(eq(mockAnalysisResultId))
        }

    @Test
    fun `execute() should return null when not found`() =
        runTest {
            mockAnalysisResultService.stub {
                onBlocking { get(any()) } doReturn null
            }

            instanceUnderTest.execute(mockAnalysisResultId) shouldBe null

            verify(mockAnalysisResultService).get(eq(mockAnalysisResultId))
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockAnalysisResultService.stub {
                onBlocking {
                    get(any())
                } doThrow SudoPrivacyInteractionClient.AnalysisResultException.FailedException()
            }

            shouldThrow<SudoPrivacyInteractionClient.AnalysisResultException.FailedException> {
                instanceUnderTest.execute(mockAnalysisResultId)
            }

            verify(mockAnalysisResultService).get(any())
        }
}
