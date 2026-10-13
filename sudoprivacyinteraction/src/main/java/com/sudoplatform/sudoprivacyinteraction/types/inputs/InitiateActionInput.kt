/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.types.inputs

import com.sudoplatform.sudoprivacyinteraction.types.ActionFulfilmentMethod
import com.sudoplatform.sudoprivacyinteraction.types.ActionIntent

/**
 * Input object containing the properties required to initiate an action against a data holder
 * relationship.
 *
 * @property dataHolderId [String] The data holder relationship to scope the action to.
 * @property intent [ActionIntent] The intent of the action to initiate.
 * @property fulfilmentMethod [ActionFulfilmentMethod] The fulfilment method the client was offered
 *  for this intent. Initiation fails if the server re-resolves a different method.
 */
data class InitiateActionInput(
    val dataHolderId: String,
    val intent: ActionIntent,
    val fulfilmentMethod: ActionFulfilmentMethod,
)
