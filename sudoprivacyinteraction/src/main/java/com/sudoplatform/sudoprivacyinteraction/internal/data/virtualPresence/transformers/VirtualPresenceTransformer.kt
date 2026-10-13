/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.VirtualPresenceEntity
import com.sudoplatform.sudoprivacyinteraction.internal.util.toDate
import com.sudoplatform.sudoprivacyinteraction.types.VirtualPresence
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.VirtualPresence as VirtualPresenceFragment

/**
 * Transformer responsible for transforming the virtual presence data between the GraphQL type,
 * the entity type used in business logic, and the public type exposed to consumers of the SDK.
 */
internal object VirtualPresenceTransformer {
    /**
     * Transform the [VirtualPresenceFragment] GraphQL type to its entity type.
     *
     * @param data [VirtualPresenceFragment] The GraphQL fragment type.
     * @return The [VirtualPresenceEntity] entity type.
     */
    fun fromGraphQLToEntity(data: VirtualPresenceFragment): VirtualPresenceEntity =
        VirtualPresenceEntity(
            id = data.id,
            owner = data.owner,
            version = data.version,
            providerType = ProviderTypeTransformer.fromGraphQLToEntity(data.providerType),
            identifier = data.identifier,
            state = VirtualPresenceStateTransformer.fromGraphQLToEntity(data.state),
            lastScannedAt = data.lastScannedAtEpochMs.toDate(),
            lastScanFailureReason = null,
            createdAt = data.createdAtEpochMs.toDate(),
            updatedAt = data.updatedAtEpochMs.toDate(),
        )

    /**
     * Transform a [VirtualPresenceEntity] to its public [VirtualPresence] type.
     *
     * @param entity [VirtualPresenceEntity] The entity to transform.
     * @return The public [VirtualPresence] type.
     */
    fun fromEntityToApi(entity: VirtualPresenceEntity): VirtualPresence =
        VirtualPresence(
            id = entity.id,
            owner = entity.owner,
            providerType = ProviderTypeTransformer.fromEntityToApi(entity.providerType),
            identifier = entity.identifier,
            state = VirtualPresenceStateTransformer.fromEntityToApi(entity.state),
            relationshipProvider = null,
            lastScannedAt = entity.lastScannedAt,
            lastScanFailureReason = entity.lastScanFailureReason,
            version = entity.version,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
