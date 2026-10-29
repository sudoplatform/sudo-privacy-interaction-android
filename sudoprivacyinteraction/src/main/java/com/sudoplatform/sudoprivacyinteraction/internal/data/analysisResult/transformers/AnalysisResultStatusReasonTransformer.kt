/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultStatusReasonEntity
import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResultStatusReason
import com.sudoplatform.sudoprivacyinteraction.graphql.type.AnalysisResultStatusReason as AnalysisResultStatusReasonGraphQL

/**
 * Transformer between the [AnalysisResultStatusReasonGraphQL] GraphQL enum, the
 * [AnalysisResultStatusReasonEntity] entity enum, and the public [AnalysisResultStatusReason] type.
 *
 * The underlying field is nullable, so each conversion maps `null` through unchanged.
 */
internal object AnalysisResultStatusReasonTransformer {
    fun fromGraphQLToEntity(data: AnalysisResultStatusReasonGraphQL?): AnalysisResultStatusReasonEntity? =
        when (data) {
            null -> null
            AnalysisResultStatusReasonGraphQL.INVALID_DOMAIN -> AnalysisResultStatusReasonEntity.INVALID_DOMAIN
            AnalysisResultStatusReasonGraphQL.POLICY_UNAVAILABLE -> AnalysisResultStatusReasonEntity.POLICY_UNAVAILABLE
            AnalysisResultStatusReasonGraphQL.MALICIOUS -> AnalysisResultStatusReasonEntity.MALICIOUS
            AnalysisResultStatusReasonGraphQL.NOT_ATTEMPTED -> AnalysisResultStatusReasonEntity.NOT_ATTEMPTED
            AnalysisResultStatusReasonGraphQL.OTHER -> AnalysisResultStatusReasonEntity.OTHER
            else -> AnalysisResultStatusReasonEntity.UNKNOWN
        }

    fun fromEntityToApi(entity: AnalysisResultStatusReasonEntity?): AnalysisResultStatusReason? =
        when (entity) {
            null -> null
            AnalysisResultStatusReasonEntity.INVALID_DOMAIN -> AnalysisResultStatusReason.INVALID_DOMAIN
            AnalysisResultStatusReasonEntity.POLICY_UNAVAILABLE -> AnalysisResultStatusReason.POLICY_UNAVAILABLE
            AnalysisResultStatusReasonEntity.MALICIOUS -> AnalysisResultStatusReason.MALICIOUS
            AnalysisResultStatusReasonEntity.NOT_ATTEMPTED -> AnalysisResultStatusReason.NOT_ATTEMPTED
            AnalysisResultStatusReasonEntity.OTHER -> AnalysisResultStatusReason.OTHER
            AnalysisResultStatusReasonEntity.UNKNOWN -> AnalysisResultStatusReason.UNKNOWN
        }
}
