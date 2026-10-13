/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionFulfilmentMethodEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionIntentEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.InitiateActionInput

/**
 * Input for [InitiateActionUseCase].
 */
internal data class InitiateActionUseCaseInput(
    val dataHolderId: String,
    val intent: ActionIntentEntity,
    val fulfilmentMethod: ActionFulfilmentMethodEntity,
)

/**
 * Application business logic for initiating an action against a data holder relationship.
 */
internal class InitiateActionUseCase(
    private val actionService: ActionService,
    private val logger: Logger,
) {
    suspend fun execute(input: InitiateActionUseCaseInput): ActionEntity {
        logger.debug(this::class.java.simpleName)
        return actionService.initiate(
            InitiateActionInput(
                dataHolderId = input.dataHolderId,
                intent = input.intent,
                fulfilmentMethod = input.fulfilmentMethod,
            ),
        )
    }
}
