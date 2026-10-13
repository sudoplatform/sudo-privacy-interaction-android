/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.analysisResult

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultService

/**
 * Application business logic for retrieving a single analysis result by ID.
 */
internal class GetAnalysisResultUseCase(
    private val analysisResultService: AnalysisResultService,
    private val logger: Logger,
) {
    suspend fun execute(id: String): AnalysisResultEntity? {
        logger.debug(this::class.java.simpleName)
        return analysisResultService.get(id)
    }
}
