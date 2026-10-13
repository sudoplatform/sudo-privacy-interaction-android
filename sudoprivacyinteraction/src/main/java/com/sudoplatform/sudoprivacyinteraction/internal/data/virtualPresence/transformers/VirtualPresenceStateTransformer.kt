/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.VirtualPresenceStateEntity
import com.sudoplatform.sudoprivacyinteraction.types.VirtualPresenceState
import com.sudoplatform.sudoprivacyinteraction.graphql.type.VirtualPresenceState as VirtualPresenceStateGraphQL

/**
 * Transformer responsible for transforming between the [VirtualPresenceStateGraphQL] GraphQL
 * enum, the [VirtualPresenceStateEntity] entity enum, and the public [VirtualPresenceState] type.
 */
internal object VirtualPresenceStateTransformer {
    /**
     * Transform the GraphQL [VirtualPresenceStateGraphQL] to its entity type.
     */
    fun fromGraphQLToEntity(data: VirtualPresenceStateGraphQL): VirtualPresenceStateEntity =
        when (data) {
            VirtualPresenceStateGraphQL.CONNECTED -> VirtualPresenceStateEntity.CONNECTED
            VirtualPresenceStateGraphQL.SCANNING -> VirtualPresenceStateEntity.SCANNING
            VirtualPresenceStateGraphQL.NEEDS_REAUTH -> VirtualPresenceStateEntity.NEEDS_REAUTH
            VirtualPresenceStateGraphQL.ERROR -> VirtualPresenceStateEntity.ERROR
            VirtualPresenceStateGraphQL.INACTIVE -> VirtualPresenceStateEntity.INACTIVE
            else -> VirtualPresenceStateEntity.UNKNOWN
        }

    /**
     * Transform the entity [VirtualPresenceStateEntity] to its public [VirtualPresenceState] type.
     */
    fun fromEntityToApi(entity: VirtualPresenceStateEntity): VirtualPresenceState =
        when (entity) {
            VirtualPresenceStateEntity.CONNECTED -> VirtualPresenceState.CONNECTED
            VirtualPresenceStateEntity.SCANNING -> VirtualPresenceState.SCANNING
            VirtualPresenceStateEntity.NEEDS_REAUTH -> VirtualPresenceState.NEEDS_REAUTH
            VirtualPresenceStateEntity.ERROR -> VirtualPresenceState.ERROR
            VirtualPresenceStateEntity.INACTIVE -> VirtualPresenceState.INACTIVE
            VirtualPresenceStateEntity.UNKNOWN -> VirtualPresenceState.UNKNOWN
        }
}
