/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.ProviderTypeEntity
import com.sudoplatform.sudoprivacyinteraction.types.ProviderType
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ProviderType as ProviderTypeGraphQL

/**
 * Transformer responsible for transforming between the [ProviderTypeGraphQL] GraphQL enum,
 * the [ProviderTypeEntity] entity enum, and the public [ProviderType] type.
 */
internal object ProviderTypeTransformer {
    /**
     * Transform the GraphQL [ProviderTypeGraphQL] to its entity type.
     */
    fun fromGraphQLToEntity(data: ProviderTypeGraphQL): ProviderTypeEntity =
        when (data) {
            ProviderTypeGraphQL.EMAIL -> ProviderTypeEntity.EMAIL
            else -> ProviderTypeEntity.UNKNOWN
        }

    /**
     * Transform the entity [ProviderTypeEntity] to its public [ProviderType] type.
     */
    fun fromEntityToApi(entity: ProviderTypeEntity): ProviderType =
        when (entity) {
            ProviderTypeEntity.EMAIL -> ProviderType.EMAIL
            ProviderTypeEntity.UNKNOWN -> ProviderType.UNKNOWN
        }
}
