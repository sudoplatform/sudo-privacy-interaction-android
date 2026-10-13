/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionContactEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.AssistedEmailTemplateEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.AssistedPayloadEntity
import com.sudoplatform.sudoprivacyinteraction.internal.util.toDate
import com.sudoplatform.sudoprivacyinteraction.types.Action
import com.sudoplatform.sudoprivacyinteraction.types.ActionContact
import com.sudoplatform.sudoprivacyinteraction.types.AssistedEmailTemplate
import com.sudoplatform.sudoprivacyinteraction.types.AssistedPayload
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.Action as ActionFragment

/**
 * Transformer responsible for transforming the action data between the GraphQL fragment, the
 * entity type used in business logic, and the public type exposed to consumers of the SDK.
 */
internal object ActionTransformer {
    fun fromGraphQLToEntity(data: ActionFragment): ActionEntity =
        ActionEntity(
            id = data.id,
            owner = data.owner,
            virtualPresenceId = data.virtualPresenceId,
            dataHolderId = data.dataHolderId,
            intent = ActionIntentTransformer.fromGraphQLToEntity(data.intent),
            additionalInfo = data.additionalInfo,
            fulfilmentMethod = ActionFulfilmentMethodTransformer.fromGraphQLToEntity(data.fulfilmentMethod),
            status = ActionStatusTransformer.fromGraphQLToEntity(data.status),
            assistedPayload =
                data.assistedPayload?.let { payload ->
                    AssistedPayloadEntity(
                        toAddress = payload.toAddress,
                        unsubscribeUrl = payload.unsubscribeUrl,
                        unsubscribeMailto = payload.unsubscribeMailto,
                        contact =
                            payload.contact?.let {
                                ActionContactEntity(email = it.email, phone = it.phone, address = it.address)
                            },
                        jurisdiction = payload.jurisdiction,
                        instructions = payload.instructions,
                        emailTemplate =
                            payload.emailTemplate?.let {
                                AssistedEmailTemplateEntity(subject = it.subject, body = it.body)
                            },
                    )
                },
            initiatedAt = data.initiatedAtEpochMs.toDate(),
            completedAt = data.completedAtEpochMs.toDate(),
            version = data.version,
            createdAt = data.createdAtEpochMs.toDate(),
            updatedAt = data.updatedAtEpochMs.toDate(),
        )

    fun fromEntityToApi(entity: ActionEntity): Action =
        Action(
            id = entity.id,
            owner = entity.owner,
            virtualPresenceId = entity.virtualPresenceId,
            dataHolderId = entity.dataHolderId,
            intent = ActionIntentTransformer.fromEntityToApi(entity.intent),
            additionalInfo = entity.additionalInfo,
            fulfilmentMethod = ActionFulfilmentMethodTransformer.fromEntityToApi(entity.fulfilmentMethod),
            status = ActionStatusTransformer.fromEntityToApi(entity.status),
            assistedPayload =
                entity.assistedPayload?.let { payload ->
                    AssistedPayload(
                        toAddress = payload.toAddress,
                        unsubscribeUrl = payload.unsubscribeUrl,
                        unsubscribeMailto = payload.unsubscribeMailto,
                        contact =
                            payload.contact?.let {
                                ActionContact(email = it.email, phone = it.phone, address = it.address)
                            },
                        jurisdiction = payload.jurisdiction,
                        instructions = payload.instructions,
                        emailTemplate =
                            payload.emailTemplate?.let {
                                AssistedEmailTemplate(subject = it.subject, body = it.body)
                            },
                    )
                },
            initiatedAt = entity.initiatedAt,
            completedAt = entity.completedAt,
            version = entity.version,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
