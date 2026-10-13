/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.RetentionStyleEntity
import com.sudoplatform.sudoprivacyinteraction.types.RetentionStyle
import com.sudoplatform.sudoprivacyinteraction.graphql.type.RetentionStyle as RetentionStyleGraphQL

/**
 * Transformer between the [RetentionStyleGraphQL] GraphQL enum, the [RetentionStyleEntity] entity
 * enum, and the public [RetentionStyle] type.
 */
internal object RetentionStyleTransformer {
    fun fromGraphQLToEntity(data: RetentionStyleGraphQL): RetentionStyleEntity =
        when (data) {
            RetentionStyleGraphQL.INDEFINITE -> RetentionStyleEntity.INDEFINITE
            RetentionStyleGraphQL.RELATIVE_TO_ACCOUNT_CLOSURE_TIME -> RetentionStyleEntity.RELATIVE_TO_ACCOUNT_CLOSURE_TIME
            RetentionStyleGraphQL.RELATIVE_TO_DATA_STORAGE_TIME -> RetentionStyleEntity.RELATIVE_TO_DATA_STORAGE_TIME
            RetentionStyleGraphQL.OTHER -> RetentionStyleEntity.OTHER
            else -> RetentionStyleEntity.OTHER
        }

    fun fromEntityToApi(entity: RetentionStyleEntity): RetentionStyle =
        when (entity) {
            RetentionStyleEntity.INDEFINITE -> RetentionStyle.INDEFINITE
            RetentionStyleEntity.RELATIVE_TO_ACCOUNT_CLOSURE_TIME -> RetentionStyle.RELATIVE_TO_ACCOUNT_CLOSURE_TIME
            RetentionStyleEntity.RELATIVE_TO_DATA_STORAGE_TIME -> RetentionStyle.RELATIVE_TO_DATA_STORAGE_TIME
            RetentionStyleEntity.OTHER -> RetentionStyle.OTHER
            RetentionStyleEntity.UNKNOWN -> RetentionStyle.UNKNOWN
        }
}
