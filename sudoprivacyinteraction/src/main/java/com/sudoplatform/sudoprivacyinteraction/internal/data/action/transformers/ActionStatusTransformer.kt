/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionStatusEntity
import com.sudoplatform.sudoprivacyinteraction.types.ActionStatus
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ActionStatus as ActionStatusGraphQL

/**
 * Transformer between the [ActionStatusGraphQL] GraphQL enum, the [ActionStatusEntity] entity enum,
 * and the public [ActionStatus] type.
 */
internal object ActionStatusTransformer {
    fun fromGraphQLToEntity(data: ActionStatusGraphQL): ActionStatusEntity =
        when (data) {
            ActionStatusGraphQL.PENDING -> ActionStatusEntity.PENDING
            ActionStatusGraphQL.IN_PROGRESS -> ActionStatusEntity.IN_PROGRESS
            ActionStatusGraphQL.COMPLETED_UNVERIFIED -> ActionStatusEntity.COMPLETED_UNVERIFIED
            ActionStatusGraphQL.COMPLETED_VERIFIED -> ActionStatusEntity.COMPLETED_VERIFIED
            ActionStatusGraphQL.FAILED -> ActionStatusEntity.FAILED
            ActionStatusGraphQL.REQUIRES_USER_INPUT -> ActionStatusEntity.REQUIRES_USER_INPUT
            ActionStatusGraphQL.CONTRADICTED -> ActionStatusEntity.CONTRADICTED
            else -> ActionStatusEntity.UNKNOWN
        }

    fun fromEntityToApi(entity: ActionStatusEntity): ActionStatus =
        when (entity) {
            ActionStatusEntity.PENDING -> ActionStatus.PENDING
            ActionStatusEntity.IN_PROGRESS -> ActionStatus.IN_PROGRESS
            ActionStatusEntity.COMPLETED_UNVERIFIED -> ActionStatus.COMPLETED_UNVERIFIED
            ActionStatusEntity.COMPLETED_VERIFIED -> ActionStatus.COMPLETED_VERIFIED
            ActionStatusEntity.FAILED -> ActionStatus.FAILED
            ActionStatusEntity.REQUIRES_USER_INPUT -> ActionStatus.REQUIRES_USER_INPUT
            ActionStatusEntity.CONTRADICTED -> ActionStatus.CONTRADICTED
            ActionStatusEntity.UNKNOWN -> ActionStatus.UNKNOWN
        }
}
