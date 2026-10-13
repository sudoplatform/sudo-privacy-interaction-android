/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionService

/**
 * Application business logic for confirming that an action was completed.
 */
internal class ConfirmActionOutcomeUseCase(
    private val actionService: ActionService,
    private val logger: Logger,
) {
    suspend fun execute(id: String): ActionEntity {
        logger.debug(this::class.java.simpleName)
        return actionService.confirmOutcome(id)
    }
}
