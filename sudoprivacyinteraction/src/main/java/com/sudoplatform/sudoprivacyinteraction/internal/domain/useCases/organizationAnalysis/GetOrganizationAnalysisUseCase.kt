/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.organizationAnalysis

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.GetOrganizationAnalysisInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisModeEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisService

/**
 * Input for [GetOrganizationAnalysisUseCase].
 */
internal data class GetOrganizationAnalysisUseCaseInput(
    val domain: String,
    val mode: OrganizationAnalysisModeEntity? = null,
)

/**
 * Application business logic for obtaining the shared, organization-generic analysis for a domain.
 */
internal class GetOrganizationAnalysisUseCase(
    private val organizationAnalysisService: OrganizationAnalysisService,
    private val logger: Logger,
) {
    suspend fun execute(input: GetOrganizationAnalysisUseCaseInput): OrganizationAnalysisEntity? {
        logger.debug(this::class.java.simpleName)
        return organizationAnalysisService.getOrganizationAnalysis(
            GetOrganizationAnalysisInput(
                domain = input.domain,
                mode = input.mode,
            ),
        )
    }
}
