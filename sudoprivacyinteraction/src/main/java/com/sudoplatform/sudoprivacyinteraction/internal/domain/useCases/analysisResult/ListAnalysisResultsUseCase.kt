/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.analysisResult

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ListAnalysisResultsInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ListAnalysisResultsOutput

/**
 * Input for [ListAnalysisResultsUseCase].
 */
internal data class ListAnalysisResultsUseCaseInput(
    val virtualPresenceId: String,
    val limit: Int? = null,
    val nextToken: String? = null,
)

/**
 * Application business logic for listing analysis results associated with a virtual presence.
 */
internal class ListAnalysisResultsUseCase(
    private val analysisResultService: AnalysisResultService,
    private val logger: Logger,
) {
    suspend fun execute(input: ListAnalysisResultsUseCaseInput): ListAnalysisResultsOutput {
        logger.debug(this::class.java.simpleName)
        return analysisResultService.list(
            ListAnalysisResultsInput(
                virtualPresenceId = input.virtualPresenceId,
                limit = input.limit,
                nextToken = input.nextToken,
            ),
        )
    }
}
