/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.amplifyframework.api.graphql.GraphQLResponse
import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.data.DataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.ProviderTypeEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.RelationshipProviderEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.VirtualPresenceStateEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.inputs.ScanOptionsEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.AuthCodeInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ConnectVirtualPresenceInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ListVirtualPresencesInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.RescanVirtualPresenceInput
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.check
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ConnectVirtualPresenceInput as ConnectVirtualPresenceInputGraphQL

/**
 * Test the correct operation of [GraphQLVirtualPresenceService]
 * using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class GraphQLVirtualPresenceServiceTest : BaseTests() {
    override val mockApiClient by before {
        mock<ApiClient>()
    }

    private val instanceUnderTest by before {
        GraphQLVirtualPresenceService(
            mockApiClient,
            mockLogger,
        )
    }

    private val input by before {
        ConnectVirtualPresenceInput(
            authCode =
                AuthCodeInput(
                    authCode = mockAuthCode,
                    redirectUri = mockRedirectUri,
                ),
            relationshipProvider = RelationshipProviderEntity.GMAIL_PROVIDER,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockApiClient)
    }

    @Test
    fun `connect() should return virtual presence entity when successful`() =
        runTest {
            val mutationResponse = DataFactory.connectVirtualPresenceMutationResponse()
            mockApiClient.stub {
                onBlocking {
                    connectVirtualPresenceMutation(any<ConnectVirtualPresenceInputGraphQL>())
                } doReturn mutationResponse
            }

            val result = instanceUnderTest.connect(input)

            result shouldNotBe null
            result.id shouldBe mockVirtualPresenceId
            result.owner shouldBe mockOwner
            result.identifier shouldBe mockIdentifier
            result.providerType shouldBe ProviderTypeEntity.EMAIL
            result.state shouldBe VirtualPresenceStateEntity.CONNECTED

            verify(mockApiClient).connectVirtualPresenceMutation(
                check { graphQLInput ->
                    graphQLInput.authCode.getOrNull()?.authCode shouldBe mockAuthCode
                    graphQLInput.authCode
                        .getOrNull()
                        ?.redirectUri
                        ?.getOrNull() shouldBe mockRedirectUri
                    graphQLInput.relationshipProvider.getOrNull() shouldNotBe null
                },
            )
        }

    @Test
    fun `connect() should throw FailedException when result is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    connectVirtualPresenceMutation(any())
                } doReturn GraphQLResponse(null, null)
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.FailedException> {
                instanceUnderTest.connect(input)
            }

            verify(mockApiClient).connectVirtualPresenceMutation(any())
        }

    @Test
    fun `connect() should throw InvalidAuthCodeException when response has invalid auth code error`() =
        runTest {
            val testError =
                GraphQLResponse.Error(
                    "Test generated error",
                    emptyList(),
                    emptyList(),
                    mapOf("errorType" to "sudoplatform.privacy-interaction.InvalidAuthCode"),
                )
            mockApiClient.stub {
                onBlocking {
                    connectVirtualPresenceMutation(any())
                } doReturn GraphQLResponse(null, listOf(testError))
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.InvalidAuthCodeException> {
                instanceUnderTest.connect(input)
            }

            verify(mockApiClient).connectVirtualPresenceMutation(any())
        }

    @Test
    fun `connect() should throw VirtualPresenceNotFoundException when response has not found error`() =
        runTest {
            val testError =
                GraphQLResponse.Error(
                    "Test generated error",
                    emptyList(),
                    emptyList(),
                    mapOf("errorType" to "sudoplatform.privacy-interaction.VirtualPresenceNotFound"),
                )
            mockApiClient.stub {
                onBlocking {
                    connectVirtualPresenceMutation(any())
                } doReturn GraphQLResponse(null, listOf(testError))
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.VirtualPresenceNotFoundException> {
                instanceUnderTest.connect(input)
            }

            verify(mockApiClient).connectVirtualPresenceMutation(any())
        }

    @Test
    fun `connect() should throw AuthenticationException when not authorized`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    connectVirtualPresenceMutation(any())
                } doThrow NotAuthorizedException("not authorized")
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.AuthenticationException> {
                instanceUnderTest.connect(input)
            }

            verify(mockApiClient).connectVirtualPresenceMutation(any())
        }

    @Test
    fun `connect() should throw UnknownException for unexpected errors`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    connectVirtualPresenceMutation(any())
                } doThrow RuntimeException("unexpected")
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.UnknownException> {
                instanceUnderTest.connect(input)
            }

            verify(mockApiClient).connectVirtualPresenceMutation(any())
        }

    @Test
    fun `disconnect() should return virtual presence entity when successful`() =
        runTest {
            val mutationResponse = DataFactory.disconnectVirtualPresenceMutationResponse()
            mockApiClient.stub {
                onBlocking {
                    disconnectVirtualPresenceMutation(any())
                } doReturn mutationResponse
            }

            val result = instanceUnderTest.disconnect(mockVirtualPresenceId)

            result shouldNotBe null
            result.id shouldBe mockVirtualPresenceId
            result.owner shouldBe mockOwner
            result.identifier shouldBe mockIdentifier
            result.providerType shouldBe ProviderTypeEntity.EMAIL
            result.state shouldBe VirtualPresenceStateEntity.CONNECTED

            verify(mockApiClient).disconnectVirtualPresenceMutation(eq(mockVirtualPresenceId))
        }

    @Test
    fun `disconnect() should throw FailedException when result is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    disconnectVirtualPresenceMutation(any())
                } doReturn GraphQLResponse(null, null)
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.FailedException> {
                instanceUnderTest.disconnect(mockVirtualPresenceId)
            }

            verify(mockApiClient).disconnectVirtualPresenceMutation(any())
        }

    @Test
    fun `disconnect() should throw VirtualPresenceNotFoundException when response has not found error`() =
        runTest {
            val testError =
                GraphQLResponse.Error(
                    "Test generated error",
                    emptyList(),
                    emptyList(),
                    mapOf("errorType" to "sudoplatform.privacy-interaction.VirtualPresenceNotFound"),
                )
            mockApiClient.stub {
                onBlocking {
                    disconnectVirtualPresenceMutation(any())
                } doReturn GraphQLResponse(null, listOf(testError))
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.VirtualPresenceNotFoundException> {
                instanceUnderTest.disconnect(mockVirtualPresenceId)
            }

            verify(mockApiClient).disconnectVirtualPresenceMutation(any())
        }

    @Test
    fun `disconnect() should throw AuthenticationException when not authorized`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    disconnectVirtualPresenceMutation(any())
                } doThrow NotAuthorizedException("not authorized")
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.AuthenticationException> {
                instanceUnderTest.disconnect(mockVirtualPresenceId)
            }

            verify(mockApiClient).disconnectVirtualPresenceMutation(any())
        }

    @Test
    fun `disconnect() should throw UnknownException for unexpected errors`() =
        runTest {
            mockApiClient.stub {
                onBlocking {
                    disconnectVirtualPresenceMutation(any())
                } doThrow RuntimeException("unexpected")
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.UnknownException> {
                instanceUnderTest.disconnect(mockVirtualPresenceId)
            }

            verify(mockApiClient).disconnectVirtualPresenceMutation(any())
        }

    @Test
    fun `list() should return list output when successful`() =
        runTest {
            val queryResponse =
                DataFactory.listVirtualPresencesQueryResponse(
                    virtualPresences =
                        listOf(
                            DataFactory.getVirtualPresenceFragment(id = "vp1"),
                            DataFactory.getVirtualPresenceFragment(id = "vp2"),
                        ),
                    nextToken = "mockNextToken",
                )
            mockApiClient.stub {
                onBlocking { listVirtualPresencesQuery(any()) } doReturn queryResponse
            }

            val result = instanceUnderTest.list(ListVirtualPresencesInput(limit = 10, nextToken = "inputToken"))

            result shouldNotBe null
            result.items.size shouldBe 2
            result.items[0].id shouldBe "vp1"
            result.items[1].id shouldBe "vp2"
            result.nextToken shouldBe "mockNextToken"

            verify(mockApiClient).listVirtualPresencesQuery(
                check { graphQLInput ->
                    graphQLInput.limit.getOrNull() shouldBe 10
                    graphQLInput.nextToken.getOrNull() shouldBe "inputToken"
                },
            )
        }

    @Test
    fun `list() should return empty list when no items`() =
        runTest {
            val queryResponse =
                DataFactory.listVirtualPresencesQueryResponse(virtualPresences = emptyList())
            mockApiClient.stub {
                onBlocking { listVirtualPresencesQuery(any()) } doReturn queryResponse
            }

            val result = instanceUnderTest.list(ListVirtualPresencesInput())

            result.items.size shouldBe 0
            result.nextToken shouldBe null

            verify(mockApiClient).listVirtualPresencesQuery(any())
        }

    @Test
    fun `list() should return empty list when result is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking { listVirtualPresencesQuery(any()) } doReturn GraphQLResponse(null, null)
            }

            val result = instanceUnderTest.list(ListVirtualPresencesInput())

            result.items.size shouldBe 0
            result.nextToken shouldBe null

            verify(mockApiClient).listVirtualPresencesQuery(any())
        }

    @Test
    fun `list() should throw AuthenticationException when not authorized`() =
        runTest {
            mockApiClient.stub {
                onBlocking { listVirtualPresencesQuery(any()) } doThrow NotAuthorizedException("not authorized")
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.AuthenticationException> {
                instanceUnderTest.list(ListVirtualPresencesInput())
            }

            verify(mockApiClient).listVirtualPresencesQuery(any())
        }

    @Test
    fun `rescan() should return virtual presence entity when successful`() =
        runTest {
            val mutationResponse = DataFactory.rescanVirtualPresenceMutationResponse()
            mockApiClient.stub {
                onBlocking { rescanVirtualPresenceMutation(any(), anyOrNull()) } doReturn mutationResponse
            }

            val options =
                ScanOptionsEntity(
                    maximumItemsProcessed = 50,
                    earliestScanDate = "2025-01-01",
                    latestScanDate = "2026-06-01",
                    excludeDomains = listOf("spam.com"),
                    excludeCategories = listOf("newsletters"),
                )

            val result =
                instanceUnderTest.rescan(
                    RescanVirtualPresenceInput(id = mockVirtualPresenceId, options = options),
                )

            result shouldNotBe null
            result.id shouldBe mockVirtualPresenceId

            verify(mockApiClient).rescanVirtualPresenceMutation(
                eq(mockVirtualPresenceId),
                check { graphQLOptions ->
                    graphQLOptions shouldNotBe null
                    graphQLOptions.maximumItemsProcessed.getOrNull() shouldBe 50
                    graphQLOptions.earliestScanDate.getOrNull() shouldBe "2025-01-01"
                    graphQLOptions.latestScanDate.getOrNull() shouldBe "2026-06-01"
                    graphQLOptions.excludeDomains.getOrNull() shouldBe listOf("spam.com")
                    graphQLOptions.excludeCategories.getOrNull() shouldBe listOf("newsletters")
                },
            )
        }

    @Test
    fun `rescan() should pass null options through`() =
        runTest {
            val mutationResponse = DataFactory.rescanVirtualPresenceMutationResponse()
            mockApiClient.stub {
                onBlocking { rescanVirtualPresenceMutation(any(), anyOrNull()) } doReturn mutationResponse
            }

            instanceUnderTest.rescan(RescanVirtualPresenceInput(id = mockVirtualPresenceId))

            verify(mockApiClient).rescanVirtualPresenceMutation(eq(mockVirtualPresenceId), eq(null))
        }

    @Test
    fun `rescan() should throw FailedException when result is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking { rescanVirtualPresenceMutation(any(), anyOrNull()) } doReturn GraphQLResponse(null, null)
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.FailedException> {
                instanceUnderTest.rescan(RescanVirtualPresenceInput(id = mockVirtualPresenceId))
            }

            verify(mockApiClient).rescanVirtualPresenceMutation(any(), anyOrNull())
        }

    @Test
    fun `rescan() should throw VirtualPresenceNotFoundException when response has not found error`() =
        runTest {
            val testError =
                GraphQLResponse.Error(
                    "Test generated error",
                    emptyList(),
                    emptyList(),
                    mapOf("errorType" to "sudoplatform.privacy-interaction.VirtualPresenceNotFound"),
                )
            mockApiClient.stub {
                onBlocking { rescanVirtualPresenceMutation(any(), anyOrNull()) } doReturn GraphQLResponse(null, listOf(testError))
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.VirtualPresenceNotFoundException> {
                instanceUnderTest.rescan(RescanVirtualPresenceInput(id = mockVirtualPresenceId))
            }

            verify(mockApiClient).rescanVirtualPresenceMutation(any(), anyOrNull())
        }
}
