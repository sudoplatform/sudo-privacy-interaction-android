/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.SignalValueEntity
import com.sudoplatform.sudoprivacyinteraction.types.SignalValue
import com.sudoplatform.sudoprivacyinteraction.graphql.type.SignalValue as SignalValueGraphQL

/**
 * Transformer between the [SignalValueGraphQL] GraphQL enum, the [SignalValueEntity] entity enum,
 * and the public [SignalValue] type.
 */
internal object SignalValueTransformer {
    fun fromGraphQLToEntity(data: SignalValueGraphQL): SignalValueEntity =
        when (data) {
            SignalValueGraphQL.YES -> SignalValueEntity.YES
            SignalValueGraphQL.NO -> SignalValueEntity.NO
            SignalValueGraphQL.UNKNOWN -> SignalValueEntity.UNKNOWN
            else -> SignalValueEntity.UNKNOWN
        }

    fun fromEntityToApi(entity: SignalValueEntity): SignalValue =
        when (entity) {
            SignalValueEntity.YES -> SignalValue.YES
            SignalValueEntity.NO -> SignalValue.NO
            SignalValueEntity.UNKNOWN -> SignalValue.UNKNOWN
        }
}
