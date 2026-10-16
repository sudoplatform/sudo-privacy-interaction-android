/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.RelationshipProviderEntity
import com.sudoplatform.sudoprivacyinteraction.types.RelationshipProvider
import com.sudoplatform.sudoprivacyinteraction.graphql.type.RelationshipProvider as RelationshipProviderGraphQL

/**
 * Transformer responsible for transforming between the [RelationshipProviderEntity] entity enum
 * and the [RelationshipProviderGraphQL] GraphQL enum, as well as the public
 * [RelationshipProvider] type.
 */
internal object RelationshipProviderTransformer {
    /**
     * Transform the entity [RelationshipProviderEntity] to its GraphQL type.
     */
    fun fromEntityToGraphQL(entity: RelationshipProviderEntity): RelationshipProviderGraphQL =
        when (entity) {
            RelationshipProviderEntity.GMAIL_PROVIDER -> RelationshipProviderGraphQL.GMAIL_PROVIDER
            RelationshipProviderEntity.TEST_PROVIDER -> RelationshipProviderGraphQL.TEST_PROVIDER
            RelationshipProviderEntity.UNKNOWN -> RelationshipProviderGraphQL.UNKNOWN__
        }

    /**
     * Transform the public [RelationshipProvider] type to its entity type. Returns `null`
     * for values that have no entity representation (e.g. [RelationshipProvider.UNKNOWN]).
     */
    fun fromApiToEntity(data: RelationshipProvider): RelationshipProviderEntity? =
        when (data) {
            RelationshipProvider.GMAIL_PROVIDER -> RelationshipProviderEntity.GMAIL_PROVIDER
            RelationshipProvider.TEST_PROVIDER -> RelationshipProviderEntity.TEST_PROVIDER
            RelationshipProvider.UNKNOWN -> RelationshipProviderEntity.UNKNOWN
        }

    /**
     * Transform the GraphQL [RelationshipProviderGraphQL] enum to its entity type. Returns `null`
     * when the GraphQL value is absent (the server did not supply a relationship provider).
     */
    fun fromGraphQLToEntity(data: RelationshipProviderGraphQL?): RelationshipProviderEntity? =
        when (data) {
            null -> null
            RelationshipProviderGraphQL.GMAIL_PROVIDER -> RelationshipProviderEntity.GMAIL_PROVIDER
            RelationshipProviderGraphQL.TEST_PROVIDER -> RelationshipProviderEntity.TEST_PROVIDER
            RelationshipProviderGraphQL.UNKNOWN__ -> RelationshipProviderEntity.UNKNOWN
        }

    /**
     * Transform the entity [RelationshipProviderEntity] to its public [RelationshipProvider]
     * type. Returns `null` when the entity value is absent.
     */
    fun fromEntityToApi(entity: RelationshipProviderEntity?): RelationshipProvider? =
        when (entity) {
            null -> null
            RelationshipProviderEntity.GMAIL_PROVIDER -> RelationshipProvider.GMAIL_PROVIDER
            RelationshipProviderEntity.TEST_PROVIDER -> RelationshipProvider.TEST_PROVIDER
            RelationshipProviderEntity.UNKNOWN -> RelationshipProvider.UNKNOWN
        }
}
