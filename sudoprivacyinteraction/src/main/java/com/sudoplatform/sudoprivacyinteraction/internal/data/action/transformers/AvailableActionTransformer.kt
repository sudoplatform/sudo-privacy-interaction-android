/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.AvailableActionEntity
import com.sudoplatform.sudoprivacyinteraction.types.AvailableAction
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.AvailableAction as AvailableActionFragment

/**
 * Transformer responsible for transforming the available action data between the GraphQL fragment,
 * the entity type used in business logic, and the public type exposed to consumers of the SDK.
 */
internal object AvailableActionTransformer {
    fun fromGraphQLToEntity(data: AvailableActionFragment): AvailableActionEntity =
        AvailableActionEntity(
            intent = ActionIntentTransformer.fromGraphQLToEntity(data.intent),
            additionalInfo = data.additionalInfo,
            fulfilmentMethod = ActionFulfilmentMethodTransformer.fromGraphQLToEntity(data.fulfilmentMethod),
            origin = ActionOriginTransformer.fromGraphQLToEntity(data.origin),
            title = data.title,
            description = data.description,
        )

    fun fromEntityToApi(entity: AvailableActionEntity): AvailableAction =
        AvailableAction(
            intent = ActionIntentTransformer.fromEntityToApi(entity.intent),
            additionalInfo = entity.additionalInfo,
            fulfilmentMethod = ActionFulfilmentMethodTransformer.fromEntityToApi(entity.fulfilmentMethod),
            origin = ActionOriginTransformer.fromEntityToApi(entity.origin),
            title = entity.title,
            description = entity.description,
        )
}
