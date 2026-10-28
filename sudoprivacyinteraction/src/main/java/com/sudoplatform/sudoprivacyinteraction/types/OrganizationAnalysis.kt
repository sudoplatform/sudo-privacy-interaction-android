/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * The Sudo Platform SDK representation of a shared, domain-keyed privacy analysis of an
 * organization.
 *
 * The same underlying analysis is returned to every caller asking about the same domain; it
 * carries only organization-generic signals. [id] equals the analyzed domain and [owner] is the
 * requesting caller.
 *
 * @property id [String] Identifies the shared analysis. Equal to the analyzed domain.
 * @property domain [String] The analyzed domain (equal to [id]).
 * @property status [AnalysisResultStatus] Aggregate status. PENDING while an asynchronous source
 *  is still resolving; re-query for the terminal result.
 * @property lastAnalyzedAt [Date] When the aggregate was last derived.
 * @property data [AnalysisResultData] Structured analysis payload. Populated when status is
 *  COMPLETE or PARTIAL, otherwise `null`.
 * @property owner [String] The caller who requested this view. The underlying analysis is shared.
 * @property version [Int] Version of this entity. Increments on update.
 * @property createdAt [Date] Date for when the analysis was created.
 * @property updatedAt [Date] Date for when the analysis was last updated.
 */
@Parcelize
data class OrganizationAnalysis(
    val id: String,
    val domain: String,
    val status: AnalysisResultStatus,
    val lastAnalyzedAt: Date,
    val data: AnalysisResultData? = null,
    val owner: String,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date,
) : Parcelable

/**
 * Controls how `getOrganizationAnalysis` obtains source results when the stored analysis is stale
 * or absent.
 */
enum class OrganizationAnalysisMode {
    /**
     * Read-through: request analysis from the sources, which may compute and leave the result
     * PENDING until asynchronous work completes.
     */
    ANALYZE,

    /**
     * Pull only the sources' existing values; never request new work. A stale domain with no
     * existing source value is not moved to PENDING.
     */
    FETCH,
}

/**
 * Controls how `getOrganizationAnalysis` treats the requested domain before sources query their
 * backing services.
 */
enum class OrganizationAnalysisDomainResolution {
    /**
     * Reduce the domain to its registrable (eTLD+1) form before analysis, e.g.
     * `mail.sub.example.co.uk` becomes `example.co.uk`.
     */
    REGISTRABLE,

    /**
     * Analyze the domain verbatim, without reducing it to a registrable domain. Used to analyze a
     * specific host/subdomain rather than the organization.
     */
    EXACT,
}
