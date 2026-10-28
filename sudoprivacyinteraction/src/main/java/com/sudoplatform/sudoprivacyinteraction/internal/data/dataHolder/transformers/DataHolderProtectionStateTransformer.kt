/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.dataHolder.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderProtectionStateEntity
import com.sudoplatform.sudoprivacyinteraction.types.DataHolderProtectionState
import com.sudoplatform.sudoprivacyinteraction.graphql.type.DataHolderProtectionState as DataHolderProtectionStateGraphQL

/**
 * Transformer responsible for transforming between the [DataHolderProtectionStateGraphQL] GraphQL
 * enum, the [DataHolderProtectionStateEntity] entity enum, and the public
 * [DataHolderProtectionState] type.
 */
internal object DataHolderProtectionStateTransformer {
    /**
     * Transform the GraphQL [DataHolderProtectionStateGraphQL] to its entity type.
     */
    fun fromGraphQLToEntity(data: DataHolderProtectionStateGraphQL): DataHolderProtectionStateEntity =
        when (data) {
            DataHolderProtectionStateGraphQL.MONITORED -> DataHolderProtectionStateEntity.MONITORED
            DataHolderProtectionStateGraphQL.ACTION_REQUESTED -> DataHolderProtectionStateEntity.ACTION_REQUESTED
            DataHolderProtectionStateGraphQL.ARCHIVED -> DataHolderProtectionStateEntity.ARCHIVED
            DataHolderProtectionStateGraphQL.RESOLVED -> DataHolderProtectionStateEntity.RESOLVED
            else -> DataHolderProtectionStateEntity.UNKNOWN
        }

    /**
     * Transform the entity [DataHolderProtectionStateEntity] to its public
     * [DataHolderProtectionState] type.
     */
    fun fromEntityToApi(entity: DataHolderProtectionStateEntity): DataHolderProtectionState =
        when (entity) {
            DataHolderProtectionStateEntity.MONITORED -> DataHolderProtectionState.MONITORED
            DataHolderProtectionStateEntity.ACTION_REQUESTED -> DataHolderProtectionState.ACTION_REQUESTED
            DataHolderProtectionStateEntity.ARCHIVED -> DataHolderProtectionState.ARCHIVED
            DataHolderProtectionStateEntity.RESOLVED -> DataHolderProtectionState.RESOLVED
            DataHolderProtectionStateEntity.UNKNOWN -> DataHolderProtectionState.UNKNOWN
        }
}
