/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.subscription

import com.amplifyframework.api.graphql.GraphQLOperation

/**
 * Manages subscribers for a specific GraphQL subscription.
 */
internal open class SubscriptionManager<T, S : Subscriber> {
    /**
     * Map of subscribers keyed by subscription identifier.
     */
    private val subscribers: MutableMap<String, S> = mutableMapOf()

    /**
     * GraphQL subscription watcher. `null` when there is no active subscription.
     */
    internal var watcher: GraphQLOperation<T>? = null

    /**
     * Retrieves the map of subscribers.
     */
    protected fun getSubscribers(): MutableMap<String, S> = this.subscribers

    /**
     * Adds or replaces a subscriber with the specified identifier.
     *
     * @param id [String] Identifier of the subscriber.
     * @param subscriber [S] Subscriber to subscribe.
     */
    internal fun replaceSubscriber(
        id: String,
        subscriber: S,
    ) {
        synchronized(this) {
            subscribers[id] = subscriber
        }
    }

    /**
     * Removes the subscriber with the specified identifier. When the last subscriber is removed
     * the underlying watcher is cancelled.
     *
     * @param id [String] Identifier of the subscriber.
     */
    internal fun removeSubscriber(id: String) {
        synchronized(this) {
            subscribers.remove(id)

            if (subscribers.isEmpty()) {
                watcher?.cancel()
                watcher = null
            }
        }
    }

    /**
     * Removes all subscribers and cancels the underlying watcher.
     */
    internal fun removeAllSubscribers() {
        synchronized(this) {
            subscribers.clear()
            watcher?.cancel()
            watcher = null
        }
    }

    /**
     * Processes an AppSync subscription connection status change. Resets state on disconnect and
     * notifies all subscribers.
     *
     * @param state [Subscriber.ConnectionState] The connection state.
     */
    internal fun connectionStatusChanged(state: Subscriber.ConnectionState) {
        val subscribersToNotify: ArrayList<S>
        synchronized(this) {
            // Take a copy of the subscribers to notify inside the synchronized block but notify
            // outside the block to avoid deadlock.
            subscribersToNotify = ArrayList(subscribers.values)

            // If the subscription was disconnected then remove all subscribers.
            if (state == Subscriber.ConnectionState.DISCONNECTED) {
                subscribers.clear()
                watcher?.cancel()
                watcher = null
            }
        }

        // Notify subscribers.
        for (subscriber in subscribersToNotify) {
            subscriber.connectionStatusChanged(state)
        }
    }
}
