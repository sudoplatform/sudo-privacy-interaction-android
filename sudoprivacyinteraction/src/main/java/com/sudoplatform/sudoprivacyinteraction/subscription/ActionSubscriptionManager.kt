/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.subscription

import com.sudoplatform.sudoprivacyinteraction.types.Action

/**
 * Manages subscribers for the action status update subscription.
 */
internal class ActionSubscriptionManager<T> : SubscriptionManager<T, ActionSubscriber>() {
    internal fun actionUpdated(action: Action) {
        val subscribersToNotify: ArrayList<ActionSubscriber>
        synchronized(this) {
            subscribersToNotify = ArrayList(this.getSubscribers().values)
        }
        for (subscriber in subscribersToNotify) {
            subscriber.actionUpdated(action)
        }
    }
}
