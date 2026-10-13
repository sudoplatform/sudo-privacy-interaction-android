/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.subscription

import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResultUpdate

/**
 * Subscriber for receiving notifications about analysis result updates.
 */
interface AnalysisResultSubscriber : Subscriber {
    /**
     * Notifies the subscriber of an analysis result update.
     *
     * @param update [AnalysisResultUpdate] The analysis result update notification.
     */
    fun analysisResultUpdated(update: AnalysisResultUpdate)
}
