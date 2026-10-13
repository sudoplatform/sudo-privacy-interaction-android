/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionFulfilmentMethodEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionIntentEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionStatusEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ListAvailableActionsOutput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.UseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action.ConfirmActionOutcomeUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action.InitiateActionUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action.ListAvailableActionsUseCase
import com.sudoplatform.sudoprivacyinteraction.subscription.ActionSubscriber
import com.sudoplatform.sudoprivacyinteraction.subscription.SubscriptionService
import com.sudoplatform.sudoprivacyinteraction.types.ActionFulfilmentMethod
import com.sudoplatform.sudoprivacyinteraction.types.ActionIntent
import com.sudoplatform.sudoprivacyinteraction.types.ActionStatus
import com.sudoplatform.sudoprivacyinteraction.types.inputs.InitiateActionInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListAvailableActionsInput
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
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner

/**
 * Test the correct operation of the [SudoPrivacyInteractionClient] action methods using mocks and
 * spies.
 */
@RunWith(RobolectricTestRunner::class)
class SudoPrivacyInteractionActionTest : BaseTests() {
    private val mockDataHolderId = "mockDataHolderId"
    private val mockActionId = "mockActionId"

    private val mockListUseCase by before {
        mock<ListAvailableActionsUseCase>().stub {
            onBlocking { execute(any()) } doReturn
                ListAvailableActionsOutput(
                    items = listOf(EntityDataFactory.getAvailableActionEntity()),
                    nextToken = "mockNextToken",
                )
        }
    }

    private val mockInitiateUseCase by before {
        mock<InitiateActionUseCase>().stub {
            onBlocking { execute(any()) } doReturn EntityDataFactory.getActionEntity()
        }
    }

    private val mockConfirmUseCase by before {
        mock<ConfirmActionOutcomeUseCase>().stub {
            onBlocking { execute(any()) } doReturn EntityDataFactory.getActionEntity()
        }
    }

    private val mockSubscriptionService by before { mock<SubscriptionService>() }

    private val mockSignInGuard by before { mock<SignInGuard>() }

    private val mockUseCaseFactory by before {
        mock<UseCaseFactory>().stub {
            on { createListAvailableActionsUseCase() } doReturn mockListUseCase
            on { createInitiateActionUseCase() } doReturn mockInitiateUseCase
            on { createConfirmActionOutcomeUseCase() } doReturn mockConfirmUseCase
        }
    }

    private val client by before {
        DefaultSudoPrivacyInteractionClient(
            context = mockContext,
            apiClient = mockApiClient,
            sudoUserClient = mockUserClient,
            logger = mockLogger,
            subscriptionService = mockSubscriptionService,
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
            mockListUseCase,
            mockInitiateUseCase,
            mockConfirmUseCase,
            mockSubscriptionService,
            mockSignInGuard,
        )
    }

    @Test
    fun `initiateAction() should map input and return the initiated action`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.initiateAction(
                        InitiateActionInput(
                            dataHolderId = mockDataHolderId,
                            intent = ActionIntent.DELETE_MY_DATA,
                            fulfilmentMethod = ActionFulfilmentMethod.ASSISTED,
                        ),
                    )
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldNotBe null
            result.id shouldBe mockActionId
            result.intent shouldBe ActionIntent.DELETE_MY_DATA
            result.assistedPayload shouldNotBe null

            verify(mockUseCaseFactory).createInitiateActionUseCase()
            verify(mockInitiateUseCase).execute(
                check { useCaseInput ->
                    useCaseInput.dataHolderId shouldBe mockDataHolderId
                    useCaseInput.intent shouldBe ActionIntentEntity.DELETE_MY_DATA
                    useCaseInput.fulfilmentMethod shouldBe ActionFulfilmentMethodEntity.ASSISTED
                },
            )
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `confirmActionOutcome() should return the updated action`() =
        runTest {
            mockConfirmUseCase.stub {
                onBlocking { execute(any()) } doReturn
                    EntityDataFactory.getActionEntity(status = ActionStatusEntity.COMPLETED_VERIFIED)
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.confirmActionOutcome(mockActionId)
                }
            deferredResult.start()
            val result = deferredResult.await()

            result.id shouldBe mockActionId
            result.status shouldBe ActionStatus.COMPLETED_VERIFIED

            verify(mockUseCaseFactory).createConfirmActionOutcomeUseCase()
            verify(mockConfirmUseCase).execute(eq(mockActionId))
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `listAvailableActions() should return results when no error present`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.listAvailableActions(ListAvailableActionsInput(dataHolderId = mockDataHolderId))
                }
            deferredResult.start()
            val result = deferredResult.await()

            result.items.size shouldBe 1
            result.items[0].intent shouldBe ActionIntent.STOP_CONTACT
            result.nextToken shouldBe "mockNextToken"

            verify(mockUseCaseFactory).createListAvailableActionsUseCase()
            verify(mockListUseCase).execute(
                check { it.dataHolderId shouldBe mockDataHolderId },
            )
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `listAvailableActions() should throw when use case throws`() =
        runTest {
            mockListUseCase.stub {
                onBlocking { execute(any()) } doThrow SudoPrivacyInteractionClient.ActionException.FailedException()
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    shouldThrow<SudoPrivacyInteractionClient.ActionException.FailedException> {
                        client.listAvailableActions(ListAvailableActionsInput(dataHolderId = mockDataHolderId))
                    }
                }
            deferredResult.start()
            deferredResult.await()

            verify(mockUseCaseFactory).createListAvailableActionsUseCase()
            verify(mockListUseCase).execute(any())
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `subscribeToActions() should delegate to the subscription service`() =
        runTest {
            val subscriber = mock<ActionSubscriber>()
            client.subscribeToActions("sub-id", subscriber)

            verify(mockSubscriptionService).subscribeToActions(eq("sub-id"), eq(subscriber))
        }

    @Test
    fun `unsubscribeFromActions() should delegate to the subscription service`() =
        runTest {
            client.unsubscribeFromActions("sub-id")

            verify(mockSubscriptionService).unsubscribeFromActions(eq("sub-id"))
        }

    @Test
    fun `unsubscribeAllFromActions() should delegate to the subscription service`() =
        runTest {
            client.unsubscribeAllFromActions()

            verify(mockSubscriptionService).unsubscribeAllFromActions()
        }
}
