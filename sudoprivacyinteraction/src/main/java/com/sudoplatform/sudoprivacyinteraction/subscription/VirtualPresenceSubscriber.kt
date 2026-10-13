/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.subscription

import com.sudoplatform.sudoprivacyinteraction.types.VirtualPresence

/**
 * Subscriber for receiving notifications about changes to [VirtualPresence]s.
 */
interface VirtualPresenceSubscriber : Subscriber {
    /**
     * Notifies the subscriber of an updated [VirtualPresence].
     *
     * @param virtualPresence [VirtualPresence] The updated virtual presence.
     */
    fun virtualPresenceUpdated(virtualPresence: VirtualPresence)
}
