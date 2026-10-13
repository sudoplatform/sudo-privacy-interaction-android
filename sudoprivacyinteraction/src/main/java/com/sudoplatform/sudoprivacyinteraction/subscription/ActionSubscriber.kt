/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.subscription

import com.sudoplatform.sudoprivacyinteraction.types.Action

/**
 * Subscriber for receiving notifications about [Action] status updates.
 */
interface ActionSubscriber : Subscriber {
    /**
     * Notifies the subscriber of an updated [Action].
     *
     * @param action [Action] The updated action.
     */
    fun actionUpdated(action: Action)
}
