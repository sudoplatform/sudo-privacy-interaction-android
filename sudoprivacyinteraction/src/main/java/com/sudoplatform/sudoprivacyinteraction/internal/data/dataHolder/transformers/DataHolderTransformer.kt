/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.dataHolder.transformers

import com.sudoplatform.sudoprivacyinteraction.graphql.OnDataHoldersUpdateSubscription
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderEntity
import com.sudoplatform.sudoprivacyinteraction.internal.util.toDate
import com.sudoplatform.sudoprivacyinteraction.types.DataHolder
import com.sudoplatform.sudoprivacyinteraction.types.DataHolderScanSummary
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.DataHolder as DataHolderFragment

/**
 * Transformer responsible for transforming the data holder data between the GraphQL type, the
 * entity type used in business logic, and the public type exposed to consumers of the SDK.
 */
internal object DataHolderTransformer {
    /**
     * Transform the [DataHolderFragment] GraphQL type to its entity type.
     *
     * @param data [DataHolderFragment] The GraphQL fragment type.
     * @return The [DataHolderEntity] entity type.
     */
    fun fromGraphQLToEntity(data: DataHolderFragment): DataHolderEntity =
        DataHolderEntity(
            id = data.id,
            owner = data.owner,
            virtualPresenceId = data.virtualPresenceId,
            domainName = data.domainName,
            name = data.name,
            protectionState = DataHolderProtectionStateTransformer.fromGraphQLToEntity(data.protectionState),
            complianceConcern = data.complianceConcern,
            mostRecentInteractionAt = data.mostRecentInteractionEpochMs.toDate(),
            version = data.version,
            createdAt = data.createdAtEpochMs.toDate(),
            updatedAt = data.updatedAtEpochMs.toDate(),
        )

    /**
     * Transform a data holder subscription update [OnDataHoldersUpdateSubscription.Item] to its
     * entity type.
     *
     * @param item [OnDataHoldersUpdateSubscription.Item] The subscription update item.
     * @return The [DataHolderEntity] entity type.
     */
    fun fromSubscriptionItemToEntity(item: OnDataHoldersUpdateSubscription.Item): DataHolderEntity =
        DataHolderEntity(
            id = item.id,
            owner = item.owner,
            virtualPresenceId = item.virtualPresenceId,
            domainName = item.domainName,
            name = item.name,
            protectionState = DataHolderProtectionStateTransformer.fromGraphQLToEntity(item.protectionState),
            complianceConcern = item.complianceConcern,
            mostRecentInteractionAt = item.mostRecentInteractionEpochMs.toDate(),
            version = item.version,
            createdAt = item.createdAtEpochMs.toDate(),
            updatedAt = item.updatedAtEpochMs.toDate(),
        )

    /**
     * Transform a [DataHolderEntity] to its public [DataHolder] type.
     *
     * @param entity [DataHolderEntity] The entity to transform.
     * @param latestScanSummary [DataHolderScanSummary] Optional latest scan summary to attach.
     * @return The public [DataHolder] type.
     */
    fun fromEntityToApi(
        entity: DataHolderEntity,
        latestScanSummary: DataHolderScanSummary? = null,
    ): DataHolder =
        DataHolder(
            id = entity.id,
            owner = entity.owner,
            virtualPresenceId = entity.virtualPresenceId,
            domainName = entity.domainName,
            name = entity.name,
            protectionState = DataHolderProtectionStateTransformer.fromEntityToApi(entity.protectionState),
            complianceConcern = entity.complianceConcern,
            mostRecentInteractionAt = entity.mostRecentInteractionAt,
            version = entity.version,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            latestScanSummary = latestScanSummary,
        )
}
