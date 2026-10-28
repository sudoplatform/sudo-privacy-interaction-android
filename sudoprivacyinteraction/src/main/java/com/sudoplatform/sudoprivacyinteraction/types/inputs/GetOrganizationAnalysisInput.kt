/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.types.inputs

import com.sudoplatform.sudoprivacyinteraction.types.OrganizationAnalysisDomainResolution
import com.sudoplatform.sudoprivacyinteraction.types.OrganizationAnalysisMode

/**
 * Input object containing the properties required to obtain an organization analysis.
 *
 * @property domain [String] The domain to analyze.
 * @property name [String] Organization name to analyze (e.g. "amazon", "stripe"). When it
 *  resolves to a domain it takes precedence over [domain]; [domain] remains required and is used
 *  as the fallback when the name cannot be resolved.
 * @property mode [OrganizationAnalysisMode] How to obtain source results. When `null`, the
 *  backend behaves as [OrganizationAnalysisMode.ANALYZE] (read-through).
 * @property resolution [OrganizationAnalysisDomainResolution] How the requested domain is treated
 *  before analysis. When `null`, defaults to [OrganizationAnalysisDomainResolution.EXACT] (the
 *  domain is analyzed verbatim).
 */
data class GetOrganizationAnalysisInput(
    val domain: String,
    val name: String? = null,
    val mode: OrganizationAnalysisMode? = null,
    val resolution: OrganizationAnalysisDomainResolution? = null,
)
