/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ShareStyleEntity
import com.sudoplatform.sudoprivacyinteraction.types.ShareStyle
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ShareStyle as ShareStyleGraphQL

/**
 * Transformer between the [ShareStyleGraphQL] GraphQL enum, the [ShareStyleEntity] entity enum,
 * and the public [ShareStyle] type.
 */
internal object ShareStyleTransformer {
    fun fromGraphQLToEntity(data: ShareStyleGraphQL): ShareStyleEntity =
        when (data) {
            ShareStyleGraphQL.SHARED -> ShareStyleEntity.SHARED
            ShareStyleGraphQL.SHARED_WITH_CONSENT -> ShareStyleEntity.SHARED_WITH_CONSENT
            ShareStyleGraphQL.INFERENCE_SHARED -> ShareStyleEntity.INFERENCE_SHARED
            ShareStyleGraphQL.NONE -> ShareStyleEntity.NONE
            else -> ShareStyleEntity.UNKNOWN
        }

    fun fromEntityToApi(entity: ShareStyleEntity): ShareStyle =
        when (entity) {
            ShareStyleEntity.SHARED -> ShareStyle.SHARED
            ShareStyleEntity.SHARED_WITH_CONSENT -> ShareStyle.SHARED_WITH_CONSENT
            ShareStyleEntity.INFERENCE_SHARED -> ShareStyle.INFERENCE_SHARED
            ShareStyleEntity.NONE -> ShareStyle.NONE
            ShareStyleEntity.UNKNOWN -> ShareStyle.UNKNOWN
        }
}
