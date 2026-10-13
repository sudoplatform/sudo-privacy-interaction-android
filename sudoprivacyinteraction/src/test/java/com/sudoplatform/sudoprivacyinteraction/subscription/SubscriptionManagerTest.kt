/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.subscription

import com.amplifyframework.api.graphql.GraphQLOperation
import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence.transformers.VirtualPresenceTransformer
import io.kotlintest.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

/**
 * Test the correct operation of the [SubscriptionManager] (via the concrete
 * [VirtualPresenceSubscriptionManager]) using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class SubscriptionManagerTest : BaseTests() {
    private val instanceUnderTest by before {
        VirtualPresenceSubscriptionManager<Any>()
    }

    private val virtualPresence by before {
        VirtualPresenceTransformer.fromEntityToApi(EntityDataFactory.getVirtualPresenceEntity())
    }

    @Test
    fun `virtualPresenceUpdated() notifies all registered subscribers`() {
        val subscriber1 = mock<VirtualPresenceSubscriber>()
        val subscriber2 = mock<VirtualPresenceSubscriber>()
        instanceUnderTest.replaceSubscriber("sub-1", subscriber1)
        instanceUnderTest.replaceSubscriber("sub-2", subscriber2)

        instanceUnderTest.virtualPresenceUpdated(virtualPresence)

        verify(subscriber1).virtualPresenceUpdated(virtualPresence)
        verify(subscriber2).virtualPresenceUpdated(virtualPresence)
    }

    @Test
    fun `connectionStatusChanged() notifies subscribers of CONNECTED`() {
        val subscriber = mock<VirtualPresenceSubscriber>()
        instanceUnderTest.replaceSubscriber("sub-1", subscriber)

        instanceUnderTest.connectionStatusChanged(Subscriber.ConnectionState.CONNECTED)

        verify(subscriber).connectionStatusChanged(Subscriber.ConnectionState.CONNECTED)
    }

    @Test
    fun `removeSubscriber() cancels the watcher when the last subscriber is removed`() {
        val watcher = mock<GraphQLOperation<Any>>()
        val subscriber = mock<VirtualPresenceSubscriber>()
        instanceUnderTest.replaceSubscriber("sub-1", subscriber)
        instanceUnderTest.watcher = watcher

        instanceUnderTest.removeSubscriber("sub-1")

        verify(watcher).cancel()
        instanceUnderTest.watcher shouldBe null
    }

    @Test
    fun `removeSubscriber() does not cancel the watcher while other subscribers remain`() {
        val watcher = mock<GraphQLOperation<Any>>()
        instanceUnderTest.replaceSubscriber("sub-1", mock())
        instanceUnderTest.replaceSubscriber("sub-2", mock())
        instanceUnderTest.watcher = watcher

        instanceUnderTest.removeSubscriber("sub-1")

        verify(watcher, never()).cancel()
        (instanceUnderTest.watcher != null) shouldBe true
    }

    @Test
    fun `connectionStatusChanged(DISCONNECTED) notifies then clears subscribers and cancels watcher`() {
        val watcher = mock<GraphQLOperation<Any>>()
        val subscriber = mock<VirtualPresenceSubscriber>()
        instanceUnderTest.replaceSubscriber("sub-1", subscriber)
        instanceUnderTest.watcher = watcher

        instanceUnderTest.connectionStatusChanged(Subscriber.ConnectionState.DISCONNECTED)

        verify(subscriber).connectionStatusChanged(Subscriber.ConnectionState.DISCONNECTED)
        verify(watcher).cancel()
        instanceUnderTest.watcher shouldBe null

        // After a disconnect the subscriber was removed, so a subsequent update is not delivered.
        instanceUnderTest.virtualPresenceUpdated(virtualPresence)
        verify(subscriber, never()).virtualPresenceUpdated(virtualPresence)
    }

    @Test
    fun `removeAllSubscribers() cancels the watcher and clears subscribers`() {
        val watcher = mock<GraphQLOperation<Any>>()
        val subscriber = mock<VirtualPresenceSubscriber>()
        instanceUnderTest.replaceSubscriber("sub-1", subscriber)
        instanceUnderTest.watcher = watcher

        instanceUnderTest.removeAllSubscribers()

        verify(watcher).cancel()
        instanceUnderTest.watcher shouldBe null

        instanceUnderTest.virtualPresenceUpdated(virtualPresence)
        verify(subscriber, never()).virtualPresenceUpdated(virtualPresence)
    }
}
