/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ListActionsForDataHolderInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ListActionsForDataHolderOutput

/**
 * Input for [ListActionsForDataHolderUseCase].
 */
internal data class ListActionsForDataHolderUseCaseInput(
    val dataHolderId: String,
    val limit: Int? = null,
    val nextToken: String? = null,
)

/**
 * Application business logic for listing the persisted action history for a data holder
 * relationship.
 */
internal class ListActionsForDataHolderUseCase(
    private val actionService: ActionService,
    private val logger: Logger,
) {
    suspend fun execute(input: ListActionsForDataHolderUseCaseInput): ListActionsForDataHolderOutput {
        logger.debug(this::class.java.simpleName)
        return actionService.listForDataHolder(
            ListActionsForDataHolderInput(
                dataHolderId = input.dataHolderId,
                limit = input.limit,
                nextToken = input.nextToken,
            ),
        )
    }
}
