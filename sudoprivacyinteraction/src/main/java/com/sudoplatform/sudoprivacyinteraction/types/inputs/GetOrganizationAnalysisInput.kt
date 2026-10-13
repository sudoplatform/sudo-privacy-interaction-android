/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.types.inputs

import com.sudoplatform.sudoprivacyinteraction.types.OrganizationAnalysisMode

/**
 * Input object containing the properties required to obtain an organization analysis.
 *
 * @property domain [String] The domain to analyze.
 * @property mode [OrganizationAnalysisMode] How to obtain source results. When `null`, the
 *  backend behaves as [OrganizationAnalysisMode.ANALYZE] (read-through).
 */
data class GetOrganizationAnalysisInput(
    val domain: String,
    val mode: OrganizationAnalysisMode? = null,
)
