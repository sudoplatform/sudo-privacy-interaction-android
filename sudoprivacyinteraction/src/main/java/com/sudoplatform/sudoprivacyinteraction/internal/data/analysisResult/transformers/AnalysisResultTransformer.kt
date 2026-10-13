/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultEntity
import com.sudoplatform.sudoprivacyinteraction.internal.util.toDate
import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResult
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.AnalysisResult as AnalysisResultFragment

/**
 * Transformer responsible for transforming the analysis result data between the GraphQL fragment,
 * the entity type used in business logic, and the public type exposed to consumers of the SDK.
 */
internal object AnalysisResultTransformer {
    fun fromGraphQLToEntity(data: AnalysisResultFragment): AnalysisResultEntity =
        AnalysisResultEntity(
            id = data.id,
            owner = data.owner,
            virtualPresenceId = data.virtualPresenceId,
            dataHolderIdentifier = data.dataHolderIdentifier,
            status = AnalysisResultStatusTransformer.fromGraphQLToEntity(data.status),
            lastAnalyzedAt = data.lastAnalyzedAtEpochMs.toDate(),
            data = data.data?.let { AnalysisResultDataTransformer.fromGraphQLToEntity(it.analysisResultData) },
            version = data.version,
            createdAt = data.createdAtEpochMs.toDate(),
            updatedAt = data.updatedAtEpochMs.toDate(),
        )

    fun fromEntityToApi(entity: AnalysisResultEntity): AnalysisResult =
        AnalysisResult(
            id = entity.id,
            owner = entity.owner,
            virtualPresenceId = entity.virtualPresenceId,
            dataHolderIdentifier = entity.dataHolderIdentifier,
            status = AnalysisResultStatusTransformer.fromEntityToApi(entity.status),
            lastAnalyzedAt = entity.lastAnalyzedAt,
            data = entity.data?.let { AnalysisResultDataTransformer.fromEntityToApi(it) },
            version = entity.version,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
