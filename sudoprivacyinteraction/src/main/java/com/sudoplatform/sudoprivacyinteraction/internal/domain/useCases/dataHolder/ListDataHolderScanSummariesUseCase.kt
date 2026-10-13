/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.ListDataHolderScanSummariesInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.ListDataHolderScanSummariesOutput

/**
 * Input for [ListDataHolderScanSummariesUseCase].
 */
internal data class ListDataHolderScanSummariesUseCaseInput(
    val dataHolderId: String,
    val limit: Int? = null,
    val nextToken: String? = null,
)

/**
 * Application business logic for listing scan summaries associated with a data holder.
 */
internal class ListDataHolderScanSummariesUseCase(
    private val dataHolderService: DataHolderService,
    private val logger: Logger,
) {
    suspend fun execute(input: ListDataHolderScanSummariesUseCaseInput): ListDataHolderScanSummariesOutput {
        logger.debug(this::class.java.simpleName)
        return dataHolderService.listScanSummaries(
            ListDataHolderScanSummariesInput(
                dataHolderId = input.dataHolderId,
                limit = input.limit,
                nextToken = input.nextToken,
            ),
        )
    }
}
