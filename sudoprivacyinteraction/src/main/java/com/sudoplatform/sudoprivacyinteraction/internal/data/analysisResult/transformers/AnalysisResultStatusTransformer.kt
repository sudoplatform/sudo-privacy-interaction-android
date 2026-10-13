/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultStatusEntity
import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResultStatus
import com.sudoplatform.sudoprivacyinteraction.graphql.type.AnalysisResultStatus as AnalysisResultStatusGraphQL

/**
 * Transformer between the [AnalysisResultStatusGraphQL] GraphQL enum, the
 * [AnalysisResultStatusEntity] entity enum, and the public [AnalysisResultStatus] type.
 */
internal object AnalysisResultStatusTransformer {
    fun fromGraphQLToEntity(data: AnalysisResultStatusGraphQL): AnalysisResultStatusEntity =
        when (data) {
            AnalysisResultStatusGraphQL.PENDING -> AnalysisResultStatusEntity.PENDING
            AnalysisResultStatusGraphQL.COMPLETE -> AnalysisResultStatusEntity.COMPLETE
            AnalysisResultStatusGraphQL.PARTIAL -> AnalysisResultStatusEntity.PARTIAL
            AnalysisResultStatusGraphQL.FAILED -> AnalysisResultStatusEntity.FAILED
            AnalysisResultStatusGraphQL.UNAVAILABLE -> AnalysisResultStatusEntity.UNAVAILABLE
            else -> AnalysisResultStatusEntity.UNAVAILABLE
        }

    fun fromEntityToApi(entity: AnalysisResultStatusEntity): AnalysisResultStatus =
        when (entity) {
            AnalysisResultStatusEntity.PENDING -> AnalysisResultStatus.PENDING
            AnalysisResultStatusEntity.COMPLETE -> AnalysisResultStatus.COMPLETE
            AnalysisResultStatusEntity.PARTIAL -> AnalysisResultStatus.PARTIAL
            AnalysisResultStatusEntity.FAILED -> AnalysisResultStatus.FAILED
            AnalysisResultStatusEntity.UNAVAILABLE -> AnalysisResultStatus.UNAVAILABLE
            AnalysisResultStatusEntity.UNKNOWN -> AnalysisResultStatus.UNKNOWN
        }
}
