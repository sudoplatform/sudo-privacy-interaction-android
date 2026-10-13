/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.organizationAnalysis.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisModeEntity
import com.sudoplatform.sudoprivacyinteraction.types.OrganizationAnalysisMode
import com.sudoplatform.sudoprivacyinteraction.graphql.type.OrganizationAnalysisMode as OrganizationAnalysisModeGraphQL

/**
 * Transformer between the public [OrganizationAnalysisMode] type, the
 * [OrganizationAnalysisModeEntity] entity enum, and the [OrganizationAnalysisModeGraphQL] GraphQL
 * enum.
 */
internal object OrganizationAnalysisModeTransformer {
    fun fromApiToEntity(mode: OrganizationAnalysisMode): OrganizationAnalysisModeEntity =
        when (mode) {
            OrganizationAnalysisMode.ANALYZE -> OrganizationAnalysisModeEntity.ANALYZE
            OrganizationAnalysisMode.FETCH -> OrganizationAnalysisModeEntity.FETCH
        }

    fun fromEntityToGraphQL(mode: OrganizationAnalysisModeEntity): OrganizationAnalysisModeGraphQL =
        when (mode) {
            OrganizationAnalysisModeEntity.ANALYZE -> OrganizationAnalysisModeGraphQL.ANALYZE
            OrganizationAnalysisModeEntity.FETCH -> OrganizationAnalysisModeGraphQL.FETCH
        }
}
