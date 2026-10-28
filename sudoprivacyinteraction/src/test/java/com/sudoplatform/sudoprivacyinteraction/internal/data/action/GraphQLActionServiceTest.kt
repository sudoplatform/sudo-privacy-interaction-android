/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.action

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.amplifyframework.api.graphql.GraphQLResponse
import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.data.DataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionFulfilmentMethodEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionIntentEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionOriginEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionStatusEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.InitiateActionInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ListAvailableActionsInput
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
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ActionFulfilmentMethod as ActionFulfilmentMethodGraphQL
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ActionIntent as ActionIntentGraphQL

/**
 * Test the correct operation of [GraphQLActionService] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class GraphQLActionServiceTest : BaseTests() {
    private val mockDataHolderId = "mockDataHolderId"
    private val mockActionId = "mockActionId"

    override val mockApiClient by before {
        mock<ApiClient>()
    }

    private val instanceUnderTest by before {
        GraphQLActionService(mockApiClient, mockLogger)
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockApiClient)
    }

    @Test
    fun `initiate() should return action with assisted payload when successful`() =
        runTest {
            mockApiClient.stub {
                onBlocking { initiateActionMutation(any()) } doReturn DataFactory.initiateActionMutationResponse()
            }

            val result =
                instanceUnderTest.initiate(
                    InitiateActionInput(
                        dataHolderId = mockDataHolderId,
                        intent = ActionIntentEntity.DELETE_MY_DATA,
                        fulfilmentMethod = ActionFulfilmentMethodEntity.ASSISTED,
                    ),
                )

            result.id shouldBe mockActionId
            result.intent shouldBe ActionIntentEntity.DELETE_MY_DATA
            result.status shouldBe ActionStatusEntity.PENDING
            result.assistedPayload shouldNotBe null
            result.assistedPayload!!.toAddress shouldBe "privacy@netflix.com"
            result.assistedPayload.contact?.email shouldBe "contact@netflix.com"
            result.assistedPayload.emailTemplate?.body shouldBe "Hello, I would like to request deletion of my data."

            verify(mockApiClient).initiateActionMutation(
                check { graphQLInput ->
                    graphQLInput.dataHolderId shouldBe mockDataHolderId
                    graphQLInput.intent shouldBe ActionIntentGraphQL.DELETE_MY_DATA
                    graphQLInput.fulfilmentMethod shouldBe ActionFulfilmentMethodGraphQL.ASSISTED
                },
            )
        }

    @Test
    fun `initiate() should throw FailedException when result is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking { initiateActionMutation(any()) } doReturn GraphQLResponse(null, null)
            }

            shouldThrow<SudoPrivacyInteractionClient.ActionException.FailedException> {
                instanceUnderTest.initiate(
                    InitiateActionInput(
                        dataHolderId = mockDataHolderId,
                        intent = ActionIntentEntity.DELETE_MY_DATA,
                        fulfilmentMethod = ActionFulfilmentMethodEntity.ASSISTED,
                    ),
                )
            }

            verify(mockApiClient).initiateActionMutation(any())
        }

    @Test
    fun `initiate() should throw DataHolderNotFoundException when response has not found error`() =
        runTest {
            val testError =
                GraphQLResponse.Error(
                    "Test generated error",
                    emptyList(),
                    emptyList(),
                    mapOf("errorType" to "sudoplatform.privacy-interaction.DataHolderNotFound"),
                )
            mockApiClient.stub {
                onBlocking { initiateActionMutation(any()) } doReturn GraphQLResponse(null, listOf(testError))
            }

            shouldThrow<SudoPrivacyInteractionClient.ActionException.DataHolderNotFoundException> {
                instanceUnderTest.initiate(
                    InitiateActionInput(
                        dataHolderId = mockDataHolderId,
                        intent = ActionIntentEntity.DELETE_MY_DATA,
                        fulfilmentMethod = ActionFulfilmentMethodEntity.ASSISTED,
                    ),
                )
            }

            verify(mockApiClient).initiateActionMutation(any())
        }

    @Test
    fun `confirmOutcome() should return the updated action when successful`() =
        runTest {
            mockApiClient.stub {
                onBlocking { confirmActionOutcomeMutation(any()) } doReturn DataFactory.confirmActionOutcomeMutationResponse()
            }

            val result = instanceUnderTest.confirmOutcome(mockActionId)

            result.id shouldBe mockActionId
            result.status shouldBe ActionStatusEntity.COMPLETED_VERIFIED

            verify(mockApiClient).confirmActionOutcomeMutation(eq(mockActionId))
        }

    @Test
    fun `confirmOutcome() should throw ActionNotFoundException when response has not found error`() =
        runTest {
            val testError =
                GraphQLResponse.Error(
                    "Test generated error",
                    emptyList(),
                    emptyList(),
                    mapOf("errorType" to "sudoplatform.privacy-interaction.ActionNotFound"),
                )
            mockApiClient.stub {
                onBlocking { confirmActionOutcomeMutation(any()) } doReturn GraphQLResponse(null, listOf(testError))
            }

            shouldThrow<SudoPrivacyInteractionClient.ActionException.ActionNotFoundException> {
                instanceUnderTest.confirmOutcome(mockActionId)
            }

            verify(mockApiClient).confirmActionOutcomeMutation(any())
        }

    @Test
    fun `confirmOutcome() should throw FailedException when result is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking { confirmActionOutcomeMutation(any()) } doReturn GraphQLResponse(null, null)
            }

            shouldThrow<SudoPrivacyInteractionClient.ActionException.FailedException> {
                instanceUnderTest.confirmOutcome(mockActionId)
            }

            verify(mockApiClient).confirmActionOutcomeMutation(any())
        }

    @Test
    fun `confirmOutcome() should throw UnknownException for unexpected errors`() =
        runTest {
            mockApiClient.stub {
                onBlocking { confirmActionOutcomeMutation(any()) } doThrow RuntimeException("unexpected")
            }

            shouldThrow<SudoPrivacyInteractionClient.ActionException.UnknownException> {
                instanceUnderTest.confirmOutcome(mockActionId)
            }

            verify(mockApiClient).confirmActionOutcomeMutation(any())
        }

    @Test
    fun `listAvailable() should return available actions when successful`() =
        runTest {
            val queryResponse =
                DataFactory.listAvailableActionsQueryResponse(
                    availableActions =
                        listOf(
                            DataFactory.getAvailableActionFragment(),
                            DataFactory.getAvailableActionFragment(intent = ActionIntentGraphQL.ACCESS_MY_DATA),
                        ),
                    nextToken = "mockNextToken",
                )
            mockApiClient.stub {
                onBlocking { listAvailableActionsQuery(any()) } doReturn queryResponse
            }

            val result = instanceUnderTest.listAvailable(ListAvailableActionsInput(dataHolderId = mockDataHolderId))

            result.items.size shouldBe 2
            result.items[0].intent shouldBe ActionIntentEntity.STOP_CONTACT
            result.items[0].origin shouldBe ActionOriginEntity.DISCOVERY
            result.items[0].fulfilmentMethod shouldBe ActionFulfilmentMethodEntity.ASSISTED
            result.items[1].intent shouldBe ActionIntentEntity.ACCESS_MY_DATA
            result.nextToken shouldBe "mockNextToken"

            verify(mockApiClient).listAvailableActionsQuery(eq(mockDataHolderId))
        }

    @Test
    fun `listAvailable() should return empty list when result is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking { listAvailableActionsQuery(any()) } doReturn GraphQLResponse(null, null)
            }

            val result = instanceUnderTest.listAvailable(ListAvailableActionsInput(dataHolderId = mockDataHolderId))

            result.items.size shouldBe 0
            result.nextToken shouldBe null

            verify(mockApiClient).listAvailableActionsQuery(any())
        }

    @Test
    fun `listAvailable() should throw AuthenticationException when not authorized`() =
        runTest {
            mockApiClient.stub {
                onBlocking { listAvailableActionsQuery(any()) } doThrow NotAuthorizedException("not authorized")
            }

            shouldThrow<SudoPrivacyInteractionClient.ActionException.AuthenticationException> {
                instanceUnderTest.listAvailable(ListAvailableActionsInput(dataHolderId = mockDataHolderId))
            }

            verify(mockApiClient).listAvailableActionsQuery(any())
        }

    @Test
    fun `get() should return the action entity when successful`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getActionQuery(any()) } doReturn DataFactory.getActionQueryResponse()
            }

            val result = instanceUnderTest.get(mockActionId)

            result shouldNotBe null
            result!!.id shouldBe mockActionId

            verify(mockApiClient).getActionQuery(eq(mockActionId))
        }

    @Test
    fun `get() should return null when the action is not found`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getActionQuery(any()) } doReturn DataFactory.getActionQueryResponse(action = null)
            }

            instanceUnderTest.get(mockActionId) shouldBe null

            verify(mockApiClient).getActionQuery(eq(mockActionId))
        }

    @Test
    fun `get() should return null when result data is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getActionQuery(any()) } doReturn GraphQLResponse(null, null)
            }

            instanceUnderTest.get(mockActionId) shouldBe null

            verify(mockApiClient).getActionQuery(eq(mockActionId))
        }

    @Test
    fun `get() should throw ActionNotFoundException for not found error response`() =
        runTest {
            val testError =
                GraphQLResponse.Error(
                    "Test generated error",
                    emptyList(),
                    emptyList(),
                    mapOf("errorType" to "sudoplatform.privacy-interaction.ActionNotFound"),
                )
            mockApiClient.stub {
                onBlocking { getActionQuery(any()) } doReturn GraphQLResponse(null, listOf(testError))
            }

            shouldThrow<SudoPrivacyInteractionClient.ActionException.ActionNotFoundException> {
                instanceUnderTest.get(mockActionId)
            }

            verify(mockApiClient).getActionQuery(eq(mockActionId))
        }

    @Test
    fun `get() should throw AuthenticationException when not authorized`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getActionQuery(any()) } doThrow NotAuthorizedException("not authorized")
            }

            shouldThrow<SudoPrivacyInteractionClient.ActionException.AuthenticationException> {
                instanceUnderTest.get(mockActionId)
            }

            verify(mockApiClient).getActionQuery(eq(mockActionId))
        }
}
