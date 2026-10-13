/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.subscription

import com.sudoplatform.sudoprivacyinteraction.types.VirtualPresence

/**
 * Manages subscribers for the virtual presence update subscription.
 */
internal class VirtualPresenceSubscriptionManager<T> : SubscriptionManager<T, VirtualPresenceSubscriber>() {
    internal fun virtualPresenceUpdated(virtualPresence: VirtualPresence) {
        val subscribersToNotify: ArrayList<VirtualPresenceSubscriber>
        synchronized(this) {
            subscribersToNotify = ArrayList(this.getSubscribers().values)
        }
        for (subscriber in subscribersToNotify) {
            subscriber.virtualPresenceUpdated(virtualPresence)
        }
    }
}
