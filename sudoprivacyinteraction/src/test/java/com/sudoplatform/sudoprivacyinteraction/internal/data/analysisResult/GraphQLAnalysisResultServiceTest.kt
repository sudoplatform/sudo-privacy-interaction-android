/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.amplifyframework.api.graphql.GraphQLResponse
import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.data.DataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultStatusEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.DataCategoryEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ListAnalysisResultsInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.OrganizationCategoryEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ShareStyleEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.SignalValueEntity
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

/**
 * Test the correct operation of [GraphQLAnalysisResultService] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class GraphQLAnalysisResultServiceTest : BaseTests() {
    private val mockAnalysisResultId = "mockAnalysisResultId"

    override val mockApiClient by before {
        mock<ApiClient>()
    }

    private val instanceUnderTest by before {
        GraphQLAnalysisResultService(mockApiClient, mockLogger)
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockApiClient)
    }

    @Test
    fun `get() should return analysis result entity with full data when successful`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getAnalysisResultQuery(any()) } doReturn DataFactory.getAnalysisResultQueryResponse()
            }

            val result = instanceUnderTest.get(mockAnalysisResultId)

            result shouldNotBe null
            result!!.id shouldBe mockAnalysisResultId
            result.virtualPresenceId shouldBe mockVirtualPresenceId
            result.dataHolderIdentifier shouldBe "netflix.com"
            result.status shouldBe AnalysisResultStatusEntity.COMPLETE

            val data = result.data
            data shouldNotBe null
            data!!.privacyScore?.score shouldBe 82.5
            data.privacyScore
                ?.breakdown
                ?.get(0)
                ?.aspect shouldBe "dataSharing"
            data.privacySummary.sourceUrl shouldBe "https://example.com/privacy"
            data.categories.size shouldBe 1
            data.categories[0].category shouldBe DataCategoryEntity.LOCATION
            data.categories[0].sharedWithThirdParties shouldBe ShareStyleEntity.SHARED_WITH_CONSENT
            data.categories[0].requiredForService shouldBe SignalValueEntity.NO
            data.categories[0].requiredForLaw shouldBe SignalValueEntity.NO
            data.capabilities.supportsAccountCreation shouldBe SignalValueEntity.YES
            data.riskIndicators.maxRetentionDays shouldBe 365
            data.organizationIdentity?.primaryCategory shouldBe OrganizationCategoryEntity.ENTERTAINMENT
            data.attribution shouldBe listOf("source: privacy-policy")

            verify(mockApiClient).getAnalysisResultQuery(eq(mockAnalysisResultId))
        }

    @Test
    fun `get() should return entity with null data when data is absent`() =
        runTest {
            val fragment = DataFactory.getAnalysisResultFragment(data = null)
            mockApiClient.stub {
                onBlocking {
                    getAnalysisResultQuery(any())
                } doReturn DataFactory.getAnalysisResultQueryResponse(analysisResult = fragment)
            }

            val result = instanceUnderTest.get(mockAnalysisResultId)

            result shouldNotBe null
            result!!.data shouldBe null

            verify(mockApiClient).getAnalysisResultQuery(any())
        }

    @Test
    fun `get() should return null when analysis result not found`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    getAnalysisResultQuery(any())
                } doReturn DataFactory.getAnalysisResultQueryResponse(analysisResult = null)
            }

            val result = instanceUnderTest.get(mockAnalysisResultId)

            result shouldBe null

            verify(mockApiClient).getAnalysisResultQuery(any())
        }

    @Test
    fun `get() should return null when result data is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getAnalysisResultQuery(any()) } doReturn GraphQLResponse(null, null)
            }

            instanceUnderTest.get(mockAnalysisResultId) shouldBe null

            verify(mockApiClient).getAnalysisResultQuery(any())
        }

    @Test
    fun `get() should throw AnalysisResultNotFoundException when response has not found error`() =
        runTest {
            val testError =
                GraphQLResponse.Error(
                    "Test generated error",
                    emptyList(),
                    emptyList(),
                    mapOf("errorType" to "sudoplatform.privacy-interaction.AnalysisResultNotFound"),
                )
            mockApiClient.stub {
                onBlocking { getAnalysisResultQuery(any()) } doReturn GraphQLResponse(null, listOf(testError))
            }

            shouldThrow<SudoPrivacyInteractionClient.AnalysisResultException.AnalysisResultNotFoundException> {
                instanceUnderTest.get(mockAnalysisResultId)
            }

            verify(mockApiClient).getAnalysisResultQuery(any())
        }

    @Test
    fun `get() should throw AuthenticationException when not authorized`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getAnalysisResultQuery(any()) } doThrow NotAuthorizedException("not authorized")
            }

            shouldThrow<SudoPrivacyInteractionClient.AnalysisResultException.AuthenticationException> {
                instanceUnderTest.get(mockAnalysisResultId)
            }

            verify(mockApiClient).getAnalysisResultQuery(any())
        }

    @Test
    fun `get() should throw UnknownException for unexpected errors`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getAnalysisResultQuery(any()) } doThrow RuntimeException("unexpected")
            }

            shouldThrow<SudoPrivacyInteractionClient.AnalysisResultException.UnknownException> {
                instanceUnderTest.get(mockAnalysisResultId)
            }

            verify(mockApiClient).getAnalysisResultQuery(any())
        }

    @Test
    fun `list() should return list output when successful`() =
        runTest {
            val queryResponse =
                DataFactory.listAnalysisResultsQueryResponse(
                    analysisResults =
                        listOf(
                            DataFactory.getAnalysisResultFragment(id = "ar1"),
                            DataFactory.getAnalysisResultFragment(id = "ar2"),
                        ),
                    nextToken = "mockNextToken",
                )
            mockApiClient.stub {
                onBlocking { listAnalysisResultsQuery(any(), anyOrNull(), anyOrNull()) } doReturn queryResponse
            }

            val result =
                instanceUnderTest.list(
                    ListAnalysisResultsInput(
                        virtualPresenceId = mockVirtualPresenceId,
                        limit = 10,
                        nextToken = "inputToken",
                    ),
                )

            result.items.size shouldBe 2
            result.items[0].id shouldBe "ar1"
            result.items[1].id shouldBe "ar2"
            result.nextToken shouldBe "mockNextToken"

            verify(mockApiClient).listAnalysisResultsQuery(eq(mockVirtualPresenceId), eq(10), eq("inputToken"))
        }

    @Test
    fun `list() should return empty list when result is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    listAnalysisResultsQuery(any(), anyOrNull(), anyOrNull())
                } doReturn GraphQLResponse(null, null)
            }

            val result = instanceUnderTest.list(ListAnalysisResultsInput(virtualPresenceId = mockVirtualPresenceId))

            result.items.size shouldBe 0
            result.nextToken shouldBe null

            verify(mockApiClient).listAnalysisResultsQuery(eq(mockVirtualPresenceId), anyOrNull(), anyOrNull())
        }

    @Test
    fun `list() should throw AuthenticationException when not authorized`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    listAnalysisResultsQuery(any(), anyOrNull(), anyOrNull())
                } doThrow NotAuthorizedException("not authorized")
            }

            shouldThrow<SudoPrivacyInteractionClient.AnalysisResultException.AuthenticationException> {
                instanceUnderTest.list(ListAnalysisResultsInput(virtualPresenceId = mockVirtualPresenceId))
            }

            verify(mockApiClient).listAnalysisResultsQuery(eq(mockVirtualPresenceId), anyOrNull(), anyOrNull())
        }
}
