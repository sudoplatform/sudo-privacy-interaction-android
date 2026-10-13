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
 * @property mode [OrganizationAnalysisModeEntity] How to obtain source results. When `null`, the
 *  backend behaves as [OrganizationAnalysisModeEntity.ANALYZE].
 */
internal data class GetOrganizationAnalysisInput(
    val domain: String,
    val mode: OrganizationAnalysisModeEntity? = null,
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
