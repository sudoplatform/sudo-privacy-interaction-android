/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.organizationAnalysis

import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisModeEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisService
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
 * Test the correct operation of [GetOrganizationAnalysisUseCase] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class GetOrganizationAnalysisUseCaseTest : BaseTests() {
    private val mockDomain = "netflix.com"

    private val mockOrganizationAnalysisService by before {
        mock<OrganizationAnalysisService>()
    }

    private val instanceUnderTest by before {
        GetOrganizationAnalysisUseCase(
            organizationAnalysisService = mockOrganizationAnalysisService,
            logger = mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockOrganizationAnalysisService)
    }

    @Test
    fun `execute() should return the entity and forward mode when successful`() =
        runTest {
            val entity = EntityDataFactory.getOrganizationAnalysisEntity()
            mockOrganizationAnalysisService.stub {
                onBlocking { getOrganizationAnalysis(any()) } doReturn entity
            }

            val result =
                instanceUnderTest.execute(
                    GetOrganizationAnalysisUseCaseInput(
                        domain = mockDomain,
                        mode = OrganizationAnalysisModeEntity.FETCH,
                    ),
                )

            result shouldNotBe null
            result shouldBe entity

            verify(mockOrganizationAnalysisService).getOrganizationAnalysis(
                check { input ->
                    input.domain shouldBe mockDomain
                    input.mode shouldBe OrganizationAnalysisModeEntity.FETCH
                },
            )
        }

    @Test
    fun `execute() should pass through null mode`() =
        runTest {
            mockOrganizationAnalysisService.stub {
                onBlocking { getOrganizationAnalysis(any()) } doReturn EntityDataFactory.getOrganizationAnalysisEntity()
            }

            instanceUnderTest.execute(GetOrganizationAnalysisUseCaseInput(domain = mockDomain))

            verify(mockOrganizationAnalysisService).getOrganizationAnalysis(
                check { input ->
                    input.domain shouldBe mockDomain
                    input.mode shouldBe null
                },
            )
        }

    @Test
    fun `execute() should return null when not found`() =
        runTest {
            mockOrganizationAnalysisService.stub {
                onBlocking { getOrganizationAnalysis(any()) } doReturn null
            }

            instanceUnderTest.execute(GetOrganizationAnalysisUseCaseInput(domain = mockDomain)) shouldBe null

            verify(mockOrganizationAnalysisService).getOrganizationAnalysis(any())
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockOrganizationAnalysisService.stub {
                onBlocking {
                    getOrganizationAnalysis(any())
                } doThrow SudoPrivacyInteractionClient.AnalysisResultException.FailedException()
            }

            shouldThrow<SudoPrivacyInteractionClient.AnalysisResultException.FailedException> {
                instanceUnderTest.execute(GetOrganizationAnalysisUseCaseInput(domain = mockDomain))
            }

            verify(mockOrganizationAnalysisService).getOrganizationAnalysis(any())
        }
}
