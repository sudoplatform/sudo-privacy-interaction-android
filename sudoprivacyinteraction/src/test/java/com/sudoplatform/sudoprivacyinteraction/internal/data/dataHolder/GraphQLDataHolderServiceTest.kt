/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.dataHolder

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.amplifyframework.api.graphql.GraphQLResponse
import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.data.DataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderProtectionStateEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.ListDataHolderScanSummariesInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.ListDataHoldersInput
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
 * Test the correct operation of [GraphQLDataHolderService] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class GraphQLDataHolderServiceTest : BaseTests() {
    private val mockDataHolderId = "mockDataHolderId"

    override val mockApiClient by before {
        mock<ApiClient>()
    }

    private val instanceUnderTest by before {
        GraphQLDataHolderService(
            mockApiClient,
            mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockApiClient)
    }

    @Test
    fun `get() should return data holder entity when successful`() =
        runTest {
            val queryResponse = DataFactory.getDataHolderQueryResponse()
            mockApiClient.stub {
                onBlocking { getDataHolderQuery(any()) } doReturn queryResponse
            }

            val result = instanceUnderTest.get(mockDataHolderId)

            result shouldNotBe null
            result!!.id shouldBe mockDataHolderId
            result.owner shouldBe mockOwner
            result.virtualPresenceId shouldBe mockVirtualPresenceId
            result.domainName shouldBe "google.com"
            result.name shouldBe "Google"
            result.protectionState shouldBe DataHolderProtectionStateEntity.MONITORED
            result.complianceConcern shouldBe false

            verify(mockApiClient).getDataHolderQuery(eq(mockDataHolderId))
        }

    @Test
    fun `get() should return null when data holder not found`() =
        runTest {
            val queryResponse = DataFactory.getDataHolderQueryResponse(dataHolder = null)
            mockApiClient.stub {
                onBlocking { getDataHolderQuery(any()) } doReturn queryResponse
            }

            val result = instanceUnderTest.get(mockDataHolderId)

            result shouldBe null

            verify(mockApiClient).getDataHolderQuery(eq(mockDataHolderId))
        }

    @Test
    fun `get() should return null when result data is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getDataHolderQuery(any()) } doReturn GraphQLResponse(null, null)
            }

            val result = instanceUnderTest.get(mockDataHolderId)

            result shouldBe null

            verify(mockApiClient).getDataHolderQuery(any())
        }

    @Test
    fun `get() should throw DataHolderNotFoundException when response has not found error`() =
        runTest {
            val testError =
                GraphQLResponse.Error(
                    "Test generated error",
                    emptyList(),
                    emptyList(),
                    mapOf("errorType" to "sudoplatform.privacy-interaction.DataHolderNotFound"),
                )
            mockApiClient.stub {
                onBlocking { getDataHolderQuery(any()) } doReturn GraphQLResponse(null, listOf(testError))
            }

            shouldThrow<SudoPrivacyInteractionClient.DataHolderException.DataHolderNotFoundException> {
                instanceUnderTest.get(mockDataHolderId)
            }

            verify(mockApiClient).getDataHolderQuery(any())
        }

    @Test
    fun `get() should throw AuthenticationException when not authorized`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getDataHolderQuery(any()) } doThrow NotAuthorizedException("not authorized")
            }

            shouldThrow<SudoPrivacyInteractionClient.DataHolderException.AuthenticationException> {
                instanceUnderTest.get(mockDataHolderId)
            }

            verify(mockApiClient).getDataHolderQuery(any())
        }

    @Test
    fun `get() should throw UnknownException for unexpected errors`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getDataHolderQuery(any()) } doThrow RuntimeException("unexpected")
            }

            shouldThrow<SudoPrivacyInteractionClient.DataHolderException.UnknownException> {
                instanceUnderTest.get(mockDataHolderId)
            }

            verify(mockApiClient).getDataHolderQuery(any())
        }

    @Test
    fun `list() should return list output when successful`() =
        runTest {
            val queryResponse =
                DataFactory.listDataHoldersQueryResponse(
                    dataHolders =
                        listOf(
                            DataFactory.getDataHolderFragment(id = "dh1"),
                            DataFactory.getDataHolderFragment(id = "dh2"),
                        ),
                    nextToken = "mockNextToken",
                )
            mockApiClient.stub {
                onBlocking { listDataHoldersQuery(any(), anyOrNull(), anyOrNull()) } doReturn queryResponse
            }

            val result =
                instanceUnderTest.list(
                    ListDataHoldersInput(
                        virtualPresenceId = mockVirtualPresenceId,
                        limit = 10,
                        nextToken = "inputToken",
                    ),
                )

            result shouldNotBe null
            result.items.size shouldBe 2
            result.items[0].id shouldBe "dh1"
            result.items[1].id shouldBe "dh2"
            result.nextToken shouldBe "mockNextToken"

            verify(mockApiClient).listDataHoldersQuery(
                eq(mockVirtualPresenceId),
                eq(10),
                eq("inputToken"),
            )
        }

    @Test
    fun `list() should return empty list when no items`() =
        runTest {
            val queryResponse = DataFactory.listDataHoldersQueryResponse(dataHolders = emptyList())
            mockApiClient.stub {
                onBlocking { listDataHoldersQuery(any(), anyOrNull(), anyOrNull()) } doReturn queryResponse
            }

            val result = instanceUnderTest.list(ListDataHoldersInput(virtualPresenceId = mockVirtualPresenceId))

            result.items.size shouldBe 0
            result.nextToken shouldBe null

            verify(mockApiClient).listDataHoldersQuery(eq(mockVirtualPresenceId), anyOrNull(), anyOrNull())
        }

    @Test
    fun `list() should return empty list when result is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    listDataHoldersQuery(any(), anyOrNull(), anyOrNull())
                } doReturn GraphQLResponse(null, null)
            }

            val result = instanceUnderTest.list(ListDataHoldersInput(virtualPresenceId = mockVirtualPresenceId))

            result.items.size shouldBe 0
            result.nextToken shouldBe null

            verify(mockApiClient).listDataHoldersQuery(eq(mockVirtualPresenceId), anyOrNull(), anyOrNull())
        }

    @Test
    fun `list() should throw AuthenticationException when not authorized`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    listDataHoldersQuery(any(), anyOrNull(), anyOrNull())
                } doThrow NotAuthorizedException("not authorized")
            }

            shouldThrow<SudoPrivacyInteractionClient.DataHolderException.AuthenticationException> {
                instanceUnderTest.list(ListDataHoldersInput(virtualPresenceId = mockVirtualPresenceId))
            }

            verify(mockApiClient).listDataHoldersQuery(eq(mockVirtualPresenceId), anyOrNull(), anyOrNull())
        }

    @Test
    fun `listScanSummaries() should return list output and parse categoryBreakdown when successful`() =
        runTest {
            val queryResponse =
                DataFactory.listDataHolderScanSummariesQueryResponse(
                    scanSummaries = listOf(DataFactory.getDataHolderScanSummaryFragment()),
                    nextToken = "mockNextToken",
                )
            mockApiClient.stub {
                onBlocking {
                    listDataHolderScanSummariesQuery(any(), anyOrNull(), anyOrNull())
                } doReturn queryResponse
            }

            val result =
                instanceUnderTest.listScanSummaries(
                    ListDataHolderScanSummariesInput(
                        dataHolderId = mockDataHolderId,
                        limit = 5,
                        nextToken = "inputToken",
                    ),
                )

            result.items.size shouldBe 1
            val summary = result.items[0]
            summary.dataHolderId shouldBe mockDataHolderId
            summary.emailCount shouldBe 10
            summary.readRate shouldBe 0.5
            summary.categoryBreakdown shouldBe mapOf("promotions" to 3, "updates" to 2)
            summary.uncategorizedCount shouldBe 1
            result.nextToken shouldBe "mockNextToken"

            verify(mockApiClient).listDataHolderScanSummariesQuery(
                eq(mockDataHolderId),
                eq(5),
                eq("inputToken"),
            )
        }

    @Test
    fun `listScanSummaries() should default to empty map for blank categoryBreakdown`() =
        runTest {
            val queryResponse =
                DataFactory.listDataHolderScanSummariesQueryResponse(
                    scanSummaries =
                        listOf(DataFactory.getDataHolderScanSummaryFragment(categoryBreakdown = "")),
                )
            mockApiClient.stub {
                onBlocking {
                    listDataHolderScanSummariesQuery(any(), anyOrNull(), anyOrNull())
                } doReturn queryResponse
            }

            val result =
                instanceUnderTest.listScanSummaries(
                    ListDataHolderScanSummariesInput(dataHolderId = mockDataHolderId),
                )

            result.items[0].categoryBreakdown shouldBe emptyMap()

            verify(mockApiClient).listDataHolderScanSummariesQuery(eq(mockDataHolderId), anyOrNull(), anyOrNull())
        }

    @Test
    fun `listScanSummaries() should return empty list when result is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    listDataHolderScanSummariesQuery(any(), anyOrNull(), anyOrNull())
                } doReturn GraphQLResponse(null, null)
            }

            val result =
                instanceUnderTest.listScanSummaries(
                    ListDataHolderScanSummariesInput(dataHolderId = mockDataHolderId),
                )

            result.items.size shouldBe 0
            result.nextToken shouldBe null

            verify(mockApiClient).listDataHolderScanSummariesQuery(eq(mockDataHolderId), anyOrNull(), anyOrNull())
        }

    @Test
    fun `listScanSummaries() should throw AuthenticationException when not authorized`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    listDataHolderScanSummariesQuery(any(), anyOrNull(), anyOrNull())
                } doThrow NotAuthorizedException("not authorized")
            }

            shouldThrow<SudoPrivacyInteractionClient.DataHolderException.AuthenticationException> {
                instanceUnderTest.listScanSummaries(
                    ListDataHolderScanSummariesInput(dataHolderId = mockDataHolderId),
                )
            }

            verify(mockApiClient).listDataHolderScanSummariesQuery(eq(mockDataHolderId), anyOrNull(), anyOrNull())
        }
}
