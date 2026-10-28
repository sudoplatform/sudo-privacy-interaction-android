/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.organizationAnalysis.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisDomainResolutionEntity
import com.sudoplatform.sudoprivacyinteraction.types.OrganizationAnalysisDomainResolution
import com.sudoplatform.sudoprivacyinteraction.graphql.type.OrganizationAnalysisDomainResolution as OrganizationAnalysisDomainResolutionGraphQL

/**
 * Transformer between the public [OrganizationAnalysisDomainResolution] type, the
 * [OrganizationAnalysisDomainResolutionEntity] entity enum, and the
 * [OrganizationAnalysisDomainResolutionGraphQL] GraphQL enum.
 */
internal object OrganizationAnalysisDomainResolutionTransformer {
    fun fromApiToEntity(resolution: OrganizationAnalysisDomainResolution): OrganizationAnalysisDomainResolutionEntity =
        when (resolution) {
            OrganizationAnalysisDomainResolution.REGISTRABLE -> OrganizationAnalysisDomainResolutionEntity.REGISTRABLE
            OrganizationAnalysisDomainResolution.EXACT -> OrganizationAnalysisDomainResolutionEntity.EXACT
        }

    fun fromEntityToGraphQL(resolution: OrganizationAnalysisDomainResolutionEntity): OrganizationAnalysisDomainResolutionGraphQL =
        when (resolution) {
            OrganizationAnalysisDomainResolutionEntity.REGISTRABLE -> OrganizationAnalysisDomainResolutionGraphQL.REGISTRABLE
            OrganizationAnalysisDomainResolutionEntity.EXACT -> OrganizationAnalysisDomainResolutionGraphQL.EXACT
        }
}
