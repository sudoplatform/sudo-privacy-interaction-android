/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.organizationAnalysis

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.amplifyframework.api.graphql.GraphQLResponse
import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.data.DataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultStatusEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.GetOrganizationAnalysisInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisModeEntity
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner
import com.sudoplatform.sudoprivacyinteraction.graphql.type.OrganizationAnalysisMode as OrganizationAnalysisModeGraphQL

/**
 * Test the correct operation of [GraphQLOrganizationAnalysisService] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class GraphQLOrganizationAnalysisServiceTest : BaseTests() {
    private val mockDomain = "netflix.com"

    override val mockApiClient by before {
        mock<ApiClient>()
    }

    private val instanceUnderTest by before {
        GraphQLOrganizationAnalysisService(mockApiClient, mockLogger)
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockApiClient)
    }

    @Test
    fun `getOrganizationAnalysis() should return entity and map mode when successful`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    getOrganizationAnalysisQuery(any(), anyOrNull())
                } doReturn DataFactory.getOrganizationAnalysisQueryResponse()
            }

            val result =
                instanceUnderTest.getOrganizationAnalysis(
                    GetOrganizationAnalysisInput(domain = mockDomain, mode = OrganizationAnalysisModeEntity.FETCH),
                )

            result shouldNotBe null
            result!!.id shouldBe mockDomain
            result.domain shouldBe mockDomain
            result.status shouldBe AnalysisResultStatusEntity.COMPLETE
            result.data shouldNotBe null
            result.data!!.privacySummary.sourceUrl shouldBe "https://example.com/privacy"

            verify(mockApiClient).getOrganizationAnalysisQuery(
                eq(mockDomain),
                eq(OrganizationAnalysisModeGraphQL.FETCH),
            )
        }

    @Test
    fun `getOrganizationAnalysis() should pass null mode through`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    getOrganizationAnalysisQuery(any(), anyOrNull())
                } doReturn DataFactory.getOrganizationAnalysisQueryResponse()
            }

            instanceUnderTest.getOrganizationAnalysis(GetOrganizationAnalysisInput(domain = mockDomain))

            verify(mockApiClient).getOrganizationAnalysisQuery(eq(mockDomain), eq(null))
        }

    @Test
    fun `getOrganizationAnalysis() should return null when not found`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    getOrganizationAnalysisQuery(any(), anyOrNull())
                } doReturn DataFactory.getOrganizationAnalysisQueryResponse(organizationAnalysis = null)
            }

            instanceUnderTest.getOrganizationAnalysis(GetOrganizationAnalysisInput(domain = mockDomain)) shouldBe null

            verify(mockApiClient).getOrganizationAnalysisQuery(eq(mockDomain), anyOrNull())
        }

    @Test
    fun `getOrganizationAnalysis() should return null when result data is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    getOrganizationAnalysisQuery(any(), anyOrNull())
                } doReturn GraphQLResponse(null, null)
            }

            instanceUnderTest.getOrganizationAnalysis(GetOrganizationAnalysisInput(domain = mockDomain)) shouldBe null

            verify(mockApiClient).getOrganizationAnalysisQuery(eq(mockDomain), anyOrNull())
        }

    @Test
    fun `getOrganizationAnalysis() should throw AuthenticationException when not authorized`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    getOrganizationAnalysisQuery(any(), anyOrNull())
                } doThrow NotAuthorizedException("not authorized")
            }

            shouldThrow<SudoPrivacyInteractionClient.AnalysisResultException.AuthenticationException> {
                instanceUnderTest.getOrganizationAnalysis(GetOrganizationAnalysisInput(domain = mockDomain))
            }

            verify(mockApiClient).getOrganizationAnalysisQuery(eq(mockDomain), anyOrNull())
        }
}
