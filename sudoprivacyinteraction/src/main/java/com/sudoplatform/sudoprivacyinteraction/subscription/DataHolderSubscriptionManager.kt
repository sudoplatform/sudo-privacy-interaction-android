/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.subscription

import com.sudoplatform.sudoprivacyinteraction.types.DataHolder

/**
 * Manages subscribers for the data holder update subscription.
 */
internal class DataHolderSubscriptionManager<T> : SubscriptionManager<T, DataHolderSubscriber>() {
    internal fun dataHoldersUpdated(dataHolders: List<DataHolder>) {
        val subscribersToNotify: ArrayList<DataHolderSubscriber>
        synchronized(this) {
            subscribersToNotify = ArrayList(this.getSubscribers().values)
        }
        for (subscriber in subscribersToNotify) {
            subscriber.dataHoldersUpdated(dataHolders)
        }
    }
}
