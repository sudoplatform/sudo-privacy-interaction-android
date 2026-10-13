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
import com.sudoplatform.sudoprivacyinteraction.types.inputs.InitiateActionInput
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

            val confirmed = privacyInteractionClient.confirmActionOutcome(initiated.id)
            confirmed shouldNotBe null
            confirmed.id shouldBe initiated.id
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
