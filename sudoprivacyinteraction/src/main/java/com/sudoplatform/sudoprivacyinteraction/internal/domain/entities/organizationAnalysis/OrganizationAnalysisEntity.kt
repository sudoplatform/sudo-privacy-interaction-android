/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultDataEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultStatusEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultStatusReasonEntity
import java.util.Date

/**
 * Core entity representation of a shared, domain-keyed organization analysis.
 */
internal data class OrganizationAnalysisEntity(
    val id: String,
    val domain: String,
    val status: AnalysisResultStatusEntity,
    val statusReason: AnalysisResultStatusReasonEntity? = null,
    val lastAnalyzedAt: Date,
    val data: AnalysisResultDataEntity? = null,
    val owner: String,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date,
)

/**
 * Controls how an organization analysis obtains source results when the stored analysis is stale
 * or absent.
 */
internal enum class OrganizationAnalysisModeEntity {
    ANALYZE,
    FETCH,
}

/**
 * Controls how an organization analysis treats the requested domain before sources query their
 * backing services.
 */
internal enum class OrganizationAnalysisDomainResolutionEntity {
    /** Reduce the domain to its registrable (eTLD+1) form before analysis. */
    REGISTRABLE,

    /** Analyze the domain verbatim, without reducing it to a registrable domain. */
    EXACT,
}
