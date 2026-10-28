/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderService

/**
 * Application business logic for unarchiving a data holder.
 */
internal class UnarchiveDataHolderUseCase(
    private val dataHolderService: DataHolderService,
    private val logger: Logger,
) {
    suspend fun execute(id: String): DataHolderEntity {
        logger.debug(this::class.java.simpleName)
        return dataHolderService.unarchive(id)
    }
}
