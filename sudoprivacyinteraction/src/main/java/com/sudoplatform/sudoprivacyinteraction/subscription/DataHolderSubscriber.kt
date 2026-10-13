/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.subscription

import com.sudoplatform.sudoprivacyinteraction.types.DataHolder

/**
 * Subscriber for receiving notifications about batches of [DataHolder] updates emitted during
 * discovery.
 */
interface DataHolderSubscriber : Subscriber {
    /**
     * Notifies the subscriber of a batch of data holder updates.
     *
     * @param dataHolders [List] The batch of updated data holders.
     */
    fun dataHoldersUpdated(dataHolders: List<DataHolder>)
}
