/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.types.inputs

/**
 * Input object containing the properties required to list the actions available for a data holder
 * relationship.
 *
 * @property dataHolderId [String] The data holder relationship to list available actions for.
 * @property includeCompleted [Boolean] When `true`, actions the user has already completed are
 *  returned as well. By default completed actions are omitted.
 */
data class ListAvailableActionsInput(
    val dataHolderId: String,
    val includeCompleted: Boolean? = null,
)
