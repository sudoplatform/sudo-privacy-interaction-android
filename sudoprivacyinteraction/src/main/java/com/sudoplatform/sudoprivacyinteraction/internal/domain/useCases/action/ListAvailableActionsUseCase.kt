/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ListAvailableActionsInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ListAvailableActionsOutput

/**
 * Input for [ListAvailableActionsUseCase].
 */
internal data class ListAvailableActionsUseCaseInput(
    val dataHolderId: String,
)

/**
 * Application business logic for listing the actions available for a data holder relationship.
 */
internal class ListAvailableActionsUseCase(
    private val actionService: ActionService,
    private val logger: Logger,
) {
    suspend fun execute(input: ListAvailableActionsUseCaseInput): ListAvailableActionsOutput {
        logger.debug(this::class.java.simpleName)
        return actionService.listAvailable(
            ListAvailableActionsInput(dataHolderId = input.dataHolderId),
        )
    }
}
