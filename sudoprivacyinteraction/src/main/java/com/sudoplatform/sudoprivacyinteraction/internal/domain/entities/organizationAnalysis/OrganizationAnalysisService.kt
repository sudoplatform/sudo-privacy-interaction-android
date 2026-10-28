/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis

/**
 * Input for [OrganizationAnalysisService.getOrganizationAnalysis].
 *
 * @property domain [String] The domain to analyze.
 * @property name [String] Organization name to analyze. When it resolves to a domain it takes
 *  precedence over [domain]; [domain] remains required and is used as the fallback when the name
 *  cannot be resolved.
 * @property mode [OrganizationAnalysisModeEntity] How to obtain source results. When `null`, the
 *  backend behaves as [OrganizationAnalysisModeEntity.ANALYZE].
 * @property resolution [OrganizationAnalysisDomainResolutionEntity] How the requested domain is
 *  treated before analysis.
 */
internal data class GetOrganizationAnalysisInput(
    val domain: String,
    val name: String? = null,
    val mode: OrganizationAnalysisModeEntity? = null,
    val resolution: OrganizationAnalysisDomainResolutionEntity? = null,
)

/**
 * Core entity representation of an organization analysis service used in business logic.
 */
internal interface OrganizationAnalysisService {
    /**
     * Analyze (read-through) and return the shared, organization-generic analysis for a domain.
     *
     * @param input [GetOrganizationAnalysisInput] Parameters used to obtain the analysis.
     * @return The [OrganizationAnalysisEntity], or `null` if not found.
     */
    suspend fun getOrganizationAnalysis(input: GetOrganizationAnalysisInput): OrganizationAnalysisEntity?
}
