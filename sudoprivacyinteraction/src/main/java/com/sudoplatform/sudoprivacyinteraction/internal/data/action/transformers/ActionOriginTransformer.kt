/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionOriginEntity
import com.sudoplatform.sudoprivacyinteraction.types.ActionOrigin
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ActionOrigin as ActionOriginGraphQL

/**
 * Transformer between the [ActionOriginGraphQL] GraphQL enum, the [ActionOriginEntity] entity enum,
 * and the public [ActionOrigin] type.
 */
internal object ActionOriginTransformer {
    fun fromGraphQLToEntity(data: ActionOriginGraphQL): ActionOriginEntity =
        when (data) {
            ActionOriginGraphQL.STATIC -> ActionOriginEntity.STATIC
            ActionOriginGraphQL.ANALYSIS -> ActionOriginEntity.ANALYSIS
            ActionOriginGraphQL.DISCOVERY -> ActionOriginEntity.DISCOVERY
            else -> ActionOriginEntity.UNKNOWN
        }

    fun fromEntityToApi(entity: ActionOriginEntity): ActionOrigin =
        when (entity) {
            ActionOriginEntity.STATIC -> ActionOrigin.STATIC
            ActionOriginEntity.ANALYSIS -> ActionOrigin.ANALYSIS
            ActionOriginEntity.DISCOVERY -> ActionOrigin.DISCOVERY
            ActionOriginEntity.UNKNOWN -> ActionOrigin.UNKNOWN
        }
}
