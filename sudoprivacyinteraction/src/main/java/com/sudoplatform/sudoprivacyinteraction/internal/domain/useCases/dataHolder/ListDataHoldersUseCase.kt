/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.ListDataHoldersInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.ListDataHoldersOutput

/**
 * Input for [ListDataHoldersUseCase].
 */
internal data class ListDataHoldersUseCaseInput(
    val virtualPresenceId: String,
    val limit: Int? = null,
    val nextToken: String? = null,
)

/**
 * Application business logic for listing data holders associated with a virtual presence.
 */
internal class ListDataHoldersUseCase(
    private val dataHolderService: DataHolderService,
    private val logger: Logger,
) {
    suspend fun execute(input: ListDataHoldersUseCaseInput): ListDataHoldersOutput {
        logger.debug(this::class.java.simpleName)
        return dataHolderService.list(
            ListDataHoldersInput(
                virtualPresenceId = input.virtualPresenceId,
                limit = input.limit,
                nextToken = input.nextToken,
            ),
        )
    }
}
