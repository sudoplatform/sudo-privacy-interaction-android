/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionFulfilmentMethodEntity
import com.sudoplatform.sudoprivacyinteraction.types.ActionFulfilmentMethod
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ActionFulfilmentMethod as ActionFulfilmentMethodGraphQL

/**
 * Transformer between the [ActionFulfilmentMethodGraphQL] GraphQL enum, the
 * [ActionFulfilmentMethodEntity] entity enum, and the public [ActionFulfilmentMethod] type.
 */
internal object ActionFulfilmentMethodTransformer {
    fun fromGraphQLToEntity(data: ActionFulfilmentMethodGraphQL): ActionFulfilmentMethodEntity =
        when (data) {
            ActionFulfilmentMethodGraphQL.ASSISTED -> ActionFulfilmentMethodEntity.ASSISTED
            ActionFulfilmentMethodGraphQL.DELEGATED -> ActionFulfilmentMethodEntity.DELEGATED
            else -> ActionFulfilmentMethodEntity.UNKNOWN
        }

    fun fromEntityToApi(entity: ActionFulfilmentMethodEntity): ActionFulfilmentMethod =
        when (entity) {
            ActionFulfilmentMethodEntity.ASSISTED -> ActionFulfilmentMethod.ASSISTED
            ActionFulfilmentMethodEntity.DELEGATED -> ActionFulfilmentMethod.DELEGATED
            ActionFulfilmentMethodEntity.UNKNOWN -> ActionFulfilmentMethod.UNKNOWN
        }

    fun fromApiToEntity(method: ActionFulfilmentMethod): ActionFulfilmentMethodEntity =
        when (method) {
            ActionFulfilmentMethod.ASSISTED -> ActionFulfilmentMethodEntity.ASSISTED
            ActionFulfilmentMethod.DELEGATED -> ActionFulfilmentMethodEntity.DELEGATED
            ActionFulfilmentMethod.UNKNOWN -> ActionFulfilmentMethodEntity.UNKNOWN
        }

    fun fromEntityToGraphQL(entity: ActionFulfilmentMethodEntity): ActionFulfilmentMethodGraphQL =
        when (entity) {
            ActionFulfilmentMethodEntity.ASSISTED -> ActionFulfilmentMethodGraphQL.ASSISTED
            ActionFulfilmentMethodEntity.DELEGATED -> ActionFulfilmentMethodGraphQL.DELEGATED
            ActionFulfilmentMethodEntity.UNKNOWN -> ActionFulfilmentMethodGraphQL.UNKNOWN__
        }
}
