/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.subscription

import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResultUpdate

/**
 * Manages subscribers for the analysis result update subscription.
 */
internal class AnalysisResultSubscriptionManager<T> : SubscriptionManager<T, AnalysisResultSubscriber>() {
    internal fun analysisResultUpdated(update: AnalysisResultUpdate) {
        val subscribersToNotify: ArrayList<AnalysisResultSubscriber>
        synchronized(this) {
            subscribersToNotify = ArrayList(this.getSubscribers().values)
        }
        for (subscriber in subscribersToNotify) {
            subscriber.analysisResultUpdated(update)
        }
    }
}
