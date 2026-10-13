/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.client

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sudoplatform.sudoprivacyinteraction.BaseIntegrationTest
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.subscription.AnalysisResultSubscriber
import com.sudoplatform.sudoprivacyinteraction.subscription.DataHolderSubscriber
import com.sudoplatform.sudoprivacyinteraction.subscription.Subscriber
import com.sudoplatform.sudoprivacyinteraction.subscription.VirtualPresenceSubscriber
import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResultUpdate
import com.sudoplatform.sudoprivacyinteraction.types.DataHolder
import com.sudoplatform.sudoprivacyinteraction.types.VirtualPresence
import io.kotlintest.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

/**
 * Test the subscription lifecycle operations of the [SudoPrivacyInteractionClient].
 */
@RunWith(AndroidJUnit4::class)
class SubscriberIntegrationTest : BaseIntegrationTest() {
    @After
    fun teardown() {
        privacyInteractionClient.unsubscribeAllFromVirtualPresence()
        privacyInteractionClient.unsubscribeAllFromDataHolders()
        privacyInteractionClient.unsubscribeAllFromAnalysisResult()
    }

    @Test
    fun subscribesToVirtualPresenceAndReceivesConnectedState() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val subscriptionId = UUID.randomUUID().toString()
            val connectionState = AtomicReference<Subscriber.ConnectionState>()

            privacyInteractionClient.subscribeToVirtualPresence(
                subscriptionId,
                object : VirtualPresenceSubscriber {
                    override fun virtualPresenceUpdated(virtualPresence: VirtualPresence) {}

                    override fun connectionStatusChanged(state: Subscriber.ConnectionState) {
                        connectionState.set(state)
                    }
                },
            )

            waitUntil { connectionState.get() == Subscriber.ConnectionState.CONNECTED }
            connectionState.get() shouldBe Subscriber.ConnectionState.CONNECTED

            privacyInteractionClient.unsubscribeFromVirtualPresence(subscriptionId)
        }

    @Test
    fun supportsMultipleVirtualPresenceSubscribersWithoutError() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val subscriptionId1 = UUID.randomUUID().toString()
            val subscriptionId2 = UUID.randomUUID().toString()
            val subscriber1Connected = AtomicReference(false)

            privacyInteractionClient.subscribeToVirtualPresence(
                subscriptionId1,
                object : VirtualPresenceSubscriber {
                    override fun virtualPresenceUpdated(virtualPresence: VirtualPresence) {}

                    override fun connectionStatusChanged(state: Subscriber.ConnectionState) {
                        if (state == Subscriber.ConnectionState.CONNECTED) {
                            subscriber1Connected.set(true)
                        }
                    }
                },
            )
            privacyInteractionClient.subscribeToVirtualPresence(
                subscriptionId2,
                object : VirtualPresenceSubscriber {
                    override fun virtualPresenceUpdated(virtualPresence: VirtualPresence) {}

                    override fun connectionStatusChanged(state: Subscriber.ConnectionState) {}
                },
            )

            waitUntil { subscriber1Connected.get() }
            subscriber1Connected.get() shouldBe true

            privacyInteractionClient.unsubscribeFromVirtualPresence(subscriptionId1)
            privacyInteractionClient.unsubscribeFromVirtualPresence(subscriptionId2)
        }

    @Test
    fun subscribesToDataHoldersAndReceivesConnectedState() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val subscriptionId = UUID.randomUUID().toString()
            val connectionState = AtomicReference<Subscriber.ConnectionState>()

            privacyInteractionClient.subscribeToDataHolders(
                subscriptionId,
                object : DataHolderSubscriber {
                    override fun dataHoldersUpdated(dataHolders: List<DataHolder>) {}

                    override fun connectionStatusChanged(state: Subscriber.ConnectionState) {
                        connectionState.set(state)
                    }
                },
            )

            waitUntil { connectionState.get() == Subscriber.ConnectionState.CONNECTED }
            connectionState.get() shouldBe Subscriber.ConnectionState.CONNECTED

            privacyInteractionClient.unsubscribeFromDataHolders(subscriptionId)
        }

    @Test
    fun doesNotNotifyDataHolderSubscriberAfterUnsubscribing() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val subscriptionId = UUID.randomUUID().toString()
            val updateCalled = AtomicReference(false)

            privacyInteractionClient.subscribeToDataHolders(
                subscriptionId,
                object : DataHolderSubscriber {
                    override fun dataHoldersUpdated(dataHolders: List<DataHolder>) {
                        updateCalled.set(true)
                    }

                    override fun connectionStatusChanged(state: Subscriber.ConnectionState) {}
                },
            )

            privacyInteractionClient.unsubscribeFromDataHolders(subscriptionId)

            connectVirtualPresence("dh-sub-test")

            updateCalled.get() shouldBe false
        }

    @Test
    fun subscribesToAnalysisResultAndReceivesConnectedState() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val subscriptionId = UUID.randomUUID().toString()
            val connectionState = AtomicReference<Subscriber.ConnectionState>()

            privacyInteractionClient.subscribeToAnalysisResult(
                subscriptionId,
                object : AnalysisResultSubscriber {
                    override fun analysisResultUpdated(update: AnalysisResultUpdate) {}

                    override fun connectionStatusChanged(state: Subscriber.ConnectionState) {
                        connectionState.set(state)
                    }
                },
            )

            waitUntil { connectionState.get() == Subscriber.ConnectionState.CONNECTED }
            connectionState.get() shouldBe Subscriber.ConnectionState.CONNECTED

            privacyInteractionClient.unsubscribeFromAnalysisResult(subscriptionId)
        }
}
