/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionIntentEntity
import com.sudoplatform.sudoprivacyinteraction.types.ActionIntent
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ActionIntent as ActionIntentGraphQL

/**
 * Transformer between the [ActionIntentGraphQL] GraphQL enum, the [ActionIntentEntity] entity enum,
 * and the public [ActionIntent] type.
 */
internal object ActionIntentTransformer {
    fun fromGraphQLToEntity(data: ActionIntentGraphQL): ActionIntentEntity =
        when (data) {
            ActionIntentGraphQL.STOP_CONTACT -> ActionIntentEntity.STOP_CONTACT
            ActionIntentGraphQL.ACCESS_MY_DATA -> ActionIntentEntity.ACCESS_MY_DATA
            ActionIntentGraphQL.DELETE_MY_DATA -> ActionIntentEntity.DELETE_MY_DATA
            ActionIntentGraphQL.LIMIT_DATA_USE -> ActionIntentEntity.LIMIT_DATA_USE
            ActionIntentGraphQL.OTHER -> ActionIntentEntity.OTHER
            else -> ActionIntentEntity.UNKNOWN
        }

    fun fromEntityToApi(entity: ActionIntentEntity): ActionIntent =
        when (entity) {
            ActionIntentEntity.STOP_CONTACT -> ActionIntent.STOP_CONTACT
            ActionIntentEntity.ACCESS_MY_DATA -> ActionIntent.ACCESS_MY_DATA
            ActionIntentEntity.DELETE_MY_DATA -> ActionIntent.DELETE_MY_DATA
            ActionIntentEntity.LIMIT_DATA_USE -> ActionIntent.LIMIT_DATA_USE
            ActionIntentEntity.OTHER -> ActionIntent.OTHER
            ActionIntentEntity.UNKNOWN -> ActionIntent.UNKNOWN
        }

    fun fromApiToEntity(intent: ActionIntent): ActionIntentEntity =
        when (intent) {
            ActionIntent.STOP_CONTACT -> ActionIntentEntity.STOP_CONTACT
            ActionIntent.ACCESS_MY_DATA -> ActionIntentEntity.ACCESS_MY_DATA
            ActionIntent.DELETE_MY_DATA -> ActionIntentEntity.DELETE_MY_DATA
            ActionIntent.LIMIT_DATA_USE -> ActionIntentEntity.LIMIT_DATA_USE
            ActionIntent.OTHER -> ActionIntentEntity.OTHER
            ActionIntent.UNKNOWN -> ActionIntentEntity.UNKNOWN
        }

    fun fromEntityToGraphQL(entity: ActionIntentEntity): ActionIntentGraphQL =
        when (entity) {
            ActionIntentEntity.STOP_CONTACT -> ActionIntentGraphQL.STOP_CONTACT
            ActionIntentEntity.ACCESS_MY_DATA -> ActionIntentGraphQL.ACCESS_MY_DATA
            ActionIntentEntity.DELETE_MY_DATA -> ActionIntentGraphQL.DELETE_MY_DATA
            ActionIntentEntity.LIMIT_DATA_USE -> ActionIntentGraphQL.LIMIT_DATA_USE
            ActionIntentEntity.OTHER -> ActionIntentGraphQL.OTHER
            ActionIntentEntity.UNKNOWN -> ActionIntentGraphQL.UNKNOWN__
        }
}
