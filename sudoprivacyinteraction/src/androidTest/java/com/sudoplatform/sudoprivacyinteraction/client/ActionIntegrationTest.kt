/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.client

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sudoplatform.sudoprivacyinteraction.BaseIntegrationTest
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.subscription.ActionSubscriber
import com.sudoplatform.sudoprivacyinteraction.subscription.Subscriber
import com.sudoplatform.sudoprivacyinteraction.types.Action
import com.sudoplatform.sudoprivacyinteraction.types.ActionFulfilmentMethod
import com.sudoplatform.sudoprivacyinteraction.types.ActionIntent
import com.sudoplatform.sudoprivacyinteraction.types.ActionOrigin
import com.sudoplatform.sudoprivacyinteraction.types.ActionStatus
import com.sudoplatform.sudoprivacyinteraction.types.inputs.InitiateActionInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListActionsForDataHolderInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListAvailableActionsInput
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

/**
 * Test the actions-based operations of the [SudoPrivacyInteractionClient].
 */
@RunWith(AndroidJUnit4::class)
class ActionIntegrationTest : BaseIntegrationTest() {
    @After
    fun teardown() {
        privacyInteractionClient.unsubscribeAllFromActions()
    }

    @Test
    fun returnsAnEmptyListForADataHolderWithNoActions() =
        runBlocking<Unit> {
            val result =
                privacyInteractionClient.listAvailableActions(
                    ListAvailableActionsInput(dataHolderId = "non-existent-data-holder-id"),
                )

            result shouldNotBe null
            result.items shouldNotBe null
            result.items.size shouldBe 0
        }

    @Test
    fun listsAvailableActionsForADiscoveredDataHolderWhenPresent() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("action-list")

            val dataHolderId = awaitFirstDataHolderId(connectedVp.id) ?: return@runBlocking

            val result =
                privacyInteractionClient.listAvailableActions(
                    ListAvailableActionsInput(dataHolderId = dataHolderId),
                )

            result shouldNotBe null
            result.items shouldNotBe null

            for (action in result.items) {
                action.intent shouldNotBe null
                ActionIntent.entries shouldContain action.intent
                action.fulfilmentMethod shouldNotBe null
                ActionFulfilmentMethod.entries shouldContain action.fulfilmentMethod
                action.origin shouldNotBe null
                ActionOrigin.entries shouldContain action.origin
                // status is nullable (null when the user has never initiated this action); when
                // present it must be a recognised value.
                action.status?.let { ActionStatus.entries shouldContain it }
            }
        }

    @Test
    fun listsAvailableActionsIncludingCompletedWhenRequested() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("action-include-completed")

            val dataHolderId = awaitFirstDataHolderId(connectedVp.id) ?: return@runBlocking

            // Requesting includeCompleted = true must still return a well-formed list; it is a
            // superset of the default (completed-omitted) view.
            val result =
                privacyInteractionClient.listAvailableActions(
                    ListAvailableActionsInput(
                        dataHolderId = dataHolderId,
                        includeCompleted = true,
                    ),
                )

            result shouldNotBe null
            result.items shouldNotBe null

            for (action in result.items) {
                ActionIntent.entries shouldContain action.intent
                ActionFulfilmentMethod.entries shouldContain action.fulfilmentMethod
                ActionOrigin.entries shouldContain action.origin
                action.status?.let { ActionStatus.entries shouldContain it }
            }
        }

    @Test
    fun initiatesAndConfirmsAnActionWhenAnAvailableActionIsPresent() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("action-initiate")

            val dataHolderId = awaitFirstDataHolderId(connectedVp.id) ?: return@runBlocking

            val available = awaitFirstOrNull { listAvailableActions(dataHolderId) } ?: return@runBlocking

            val initiated =
                privacyInteractionClient.initiateAction(
                    InitiateActionInput(
                        dataHolderId = dataHolderId,
                        intent = available.intent,
                        fulfilmentMethod = available.fulfilmentMethod,
                    ),
                )

            initiated shouldNotBe null
            initiated.id shouldNotBe null
            initiated.intent shouldBe available.intent
            initiated.status shouldNotBe null

            val fetched = privacyInteractionClient.getAction(initiated.id)
            fetched shouldNotBe null
            fetched!!.id shouldBe initiated.id
            fetched.intent shouldBe initiated.intent

            val confirmed = privacyInteractionClient.confirmActionOutcome(initiated.id)
            confirmed shouldNotBe null
            confirmed.id shouldBe initiated.id
        }

    @Test
    fun returnsNullForNonExistentAction() =
        runBlocking<Unit> {
            val result = privacyInteractionClient.getAction("non-existent-action-id")

            result shouldBe null
        }

    @Test
    fun listActionsForDataHolderReturnsAnEmptyListForADataHolderWithNoActions() =
        runBlocking<Unit> {
            val result =
                privacyInteractionClient.listActionsForDataHolder(
                    ListActionsForDataHolderInput(dataHolderId = "non-existent-data-holder-id"),
                )

            result shouldNotBe null
            result.items shouldNotBe null
            result.items.size shouldBe 0
        }

    @Test
    fun listsActionHistoryAfterInitiatingAnAction() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("action-history")

            val dataHolderId = awaitFirstDataHolderId(connectedVp.id) ?: return@runBlocking

            val available = awaitFirstOrNull { listAvailableActions(dataHolderId) } ?: return@runBlocking

            val initiated =
                privacyInteractionClient.initiateAction(
                    InitiateActionInput(
                        dataHolderId = dataHolderId,
                        intent = available.intent,
                        fulfilmentMethod = available.fulfilmentMethod,
                    ),
                )

            // The persisted history must eventually contain the action we just initiated.
            val history =
                awaitValueOrNull(
                    condition = {
                        privacyInteractionClient
                            .listActionsForDataHolder(ListActionsForDataHolderInput(dataHolderId = dataHolderId))
                            .items
                            .any { it.id == initiated.id }
                    },
                    value = {
                        privacyInteractionClient.listActionsForDataHolder(
                            ListActionsForDataHolderInput(dataHolderId = dataHolderId),
                        )
                    },
                ) ?: return@runBlocking

            history shouldNotBe null
            history.items shouldNotBe null

            val found = history.items.first { it.id == initiated.id }
            found.intent shouldBe initiated.intent
            ActionIntent.entries shouldContain found.intent
            ActionFulfilmentMethod.entries shouldContain found.fulfilmentMethod
            ActionStatus.entries shouldContain found.status
        }

    @Test
    fun subscribesToActionsAndReceivesConnectedState() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val subscriptionId = UUID.randomUUID().toString()
            val connectionState = AtomicReference<Subscriber.ConnectionState>()

            privacyInteractionClient.subscribeToActions(
                subscriptionId,
                object : ActionSubscriber {
                    override fun actionUpdated(action: Action) {}

                    override fun connectionStatusChanged(state: Subscriber.ConnectionState) {
                        connectionState.set(state)
                    }
                },
            )

            waitUntil { connectionState.get() == Subscriber.ConnectionState.CONNECTED }
            connectionState.get() shouldBe Subscriber.ConnectionState.CONNECTED

            privacyInteractionClient.unsubscribeFromActions(subscriptionId)
        }
}
