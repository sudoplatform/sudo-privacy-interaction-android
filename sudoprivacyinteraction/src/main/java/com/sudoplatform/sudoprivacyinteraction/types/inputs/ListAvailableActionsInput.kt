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
 */
data class ListAvailableActionsInput(
    val dataHolderId: String,
)
