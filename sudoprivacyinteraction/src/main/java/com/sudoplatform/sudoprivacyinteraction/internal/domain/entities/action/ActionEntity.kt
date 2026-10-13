/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action

import java.util.Date

/**
 * Core entity representation of a persisted, initiated action instance scoped to
 * a data holder relationship.
 */
internal data class ActionEntity(
    val id: String,
    val owner: String,
    val virtualPresenceId: String,
    val dataHolderId: String? = null,
    val intent: ActionIntentEntity,
    val additionalInfo: String? = null,
    val fulfilmentMethod: ActionFulfilmentMethodEntity,
    val status: ActionStatusEntity,
    val assistedPayload: AssistedPayloadEntity? = null,
    val initiatedAt: Date? = null,
    val completedAt: Date? = null,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date,
)

/**
 * Core entity representation of a derived, non-persisted action the user could
 * take against a data holder. Produced at query time by merging static,
 * analysis, and discovery sources.
 */
internal data class AvailableActionEntity(
    val intent: ActionIntentEntity,
    val additionalInfo: String? = null,
    val fulfilmentMethod: ActionFulfilmentMethodEntity,
    val origin: ActionOriginEntity,
    val title: String? = null,
    val description: String? = null,
)

/**
 * Materials for an assisted action. The service supplies the organisation
 * contact points and any source-provided instructions, but never the user's own
 * data.
 */
internal data class AssistedPayloadEntity(
    val toAddress: String? = null,
    val unsubscribeUrl: String? = null,
    val unsubscribeMailto: String? = null,
    val contact: ActionContactEntity? = null,
    val jurisdiction: String? = null,
    val instructions: String? = null,
    val emailTemplate: AssistedEmailTemplateEntity? = null,
)

/**
 * Contact materials for an assisted action.
 */
internal data class ActionContactEntity(
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
)

/**
 * An email template returned for an assisted action. The body may contain
 * placeholders (e.g. "{{fullName}}") for fields the user supplies client-side.
 */
internal data class AssistedEmailTemplateEntity(
    val subject: String? = null,
    val body: String,
)

/**
 * The user-facing intent of an action. This is a service-owned taxonomy,
 * decoupled from any upstream provider's action vocabulary.
 */
internal enum class ActionIntentEntity {
    STOP_CONTACT,
    ACCESS_MY_DATA,
    DELETE_MY_DATA,
    LIMIT_DATA_USE,
    OTHER,
    UNKNOWN,
}

/**
 * How an action is fulfilled from the user's perspective.
 */
internal enum class ActionFulfilmentMethodEntity {
    ASSISTED,
    DELEGATED,
    UNKNOWN,
}

/**
 * Which source contributed an available action.
 */
internal enum class ActionOriginEntity {
    STATIC,
    ANALYSIS,
    DISCOVERY,
    UNKNOWN,
}

/**
 * Lifecycle status of a persisted action instance.
 */
internal enum class ActionStatusEntity {
    PENDING,
    IN_PROGRESS,
    COMPLETED_UNVERIFIED,
    COMPLETED_VERIFIED,
    FAILED,
    REQUIRES_USER_INPUT,
    CONTRADICTED,
    UNKNOWN,
}
