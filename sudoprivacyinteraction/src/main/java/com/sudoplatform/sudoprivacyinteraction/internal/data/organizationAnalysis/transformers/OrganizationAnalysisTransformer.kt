/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.organizationAnalysis.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers.AnalysisResultDataTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers.AnalysisResultStatusTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisEntity
import com.sudoplatform.sudoprivacyinteraction.internal.util.toDate
import com.sudoplatform.sudoprivacyinteraction.types.OrganizationAnalysis
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.OrganizationAnalysis as OrganizationAnalysisFragment

/**
 * Transformer responsible for transforming the organization analysis data between the GraphQL
 * fragment, the entity type used in business logic, and the public type exposed to consumers.
 */
internal object OrganizationAnalysisTransformer {
    fun fromGraphQLToEntity(data: OrganizationAnalysisFragment): OrganizationAnalysisEntity =
        OrganizationAnalysisEntity(
            id = data.id,
            domain = data.domain,
            status = AnalysisResultStatusTransformer.fromGraphQLToEntity(data.status),
            lastAnalyzedAt = data.lastAnalyzedAtEpochMs.toDate(),
            data = data.data?.let { AnalysisResultDataTransformer.fromGraphQLToEntity(it.analysisResultData) },
            owner = data.owner,
            version = data.version,
            createdAt = data.createdAtEpochMs.toDate(),
            updatedAt = data.updatedAtEpochMs.toDate(),
        )

    fun fromEntityToApi(entity: OrganizationAnalysisEntity): OrganizationAnalysis =
        OrganizationAnalysis(
            id = entity.id,
            domain = entity.domain,
            status = AnalysisResultStatusTransformer.fromEntityToApi(entity.status),
            lastAnalyzedAt = entity.lastAnalyzedAt,
            data = entity.data?.let { AnalysisResultDataTransformer.fromEntityToApi(it) },
            owner = entity.owner,
            version = entity.version,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
