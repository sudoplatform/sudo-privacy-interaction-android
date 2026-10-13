/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * The Sudo Platform SDK representation of a persisted, initiated action instance scoped to a data
 * holder relationship.
 *
 * @property id [String] Unique identifier associated with the action.
 * @property owner [String] Unique identifier of the user associated with the action.
 * @property virtualPresenceId [String] The virtual presence this action belongs to.
 * @property dataHolderId [String] The data holder relationship this action is scoped to. May be
 *  `null`.
 * @property intent [ActionIntent] The user-facing intent of the action.
 * @property additionalInfo [String] Additional info, populated when intent is [ActionIntent.OTHER].
 * @property fulfilmentMethod [ActionFulfilmentMethod] How the action is fulfilled.
 * @property status [ActionStatus] Current lifecycle status of the action.
 * @property assistedPayload [AssistedPayload] Materials for an assisted action. `null` for a
 *  delegated action.
 * @property initiatedAt [Date] Date for when the action was initiated. May be `null`.
 * @property completedAt [Date] Date for when the action completed. May be `null`.
 * @property version [Int] Version of this entity. Increments on update.
 * @property createdAt [Date] Date for when the action was created.
 * @property updatedAt [Date] Date for when the action was last updated.
 */
@Parcelize
data class Action(
    val id: String,
    val owner: String,
    val virtualPresenceId: String,
    val dataHolderId: String? = null,
    val intent: ActionIntent,
    val additionalInfo: String? = null,
    val fulfilmentMethod: ActionFulfilmentMethod,
    val status: ActionStatus,
    val assistedPayload: AssistedPayload? = null,
    val initiatedAt: Date? = null,
    val completedAt: Date? = null,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date,
) : Parcelable

/**
 * The Sudo Platform SDK representation of a derived, non-persisted action the user could take
 * against a data holder.
 *
 * @property intent [ActionIntent] The user-facing intent of the action.
 * @property additionalInfo [String] Additional info, populated when intent is [ActionIntent.OTHER].
 * @property fulfilmentMethod [ActionFulfilmentMethod] How the action is fulfilled.
 * @property origin [ActionOrigin] Which source contributed this action.
 * @property title [String] Short presentation hint for the action's title.
 * @property description [String] Short presentation hint describing the action.
 */
@Parcelize
data class AvailableAction(
    val intent: ActionIntent,
    val additionalInfo: String? = null,
    val fulfilmentMethod: ActionFulfilmentMethod,
    val origin: ActionOrigin,
    val title: String? = null,
    val description: String? = null,
) : Parcelable

/**
 * Materials for an assisted action. The service supplies the organisation contact points and any
 * source-provided instructions, but never the user's own data.
 *
 * @property toAddress [String] Recipient address for an assisted email action.
 * @property unsubscribeUrl [String] Unsubscribe URL, when available.
 * @property unsubscribeMailto [String] Unsubscribe mailto link, when available.
 * @property contact [ActionContact] Organisation contact points, when available.
 * @property jurisdiction [String] Jurisdiction relevant to the action, when available.
 * @property instructions [String] Free-text instructions, potentially provided verbatim by an
 *  analysis source.
 * @property emailTemplate [AssistedEmailTemplate] An email template for the action, resolved from
 *  a deployment-time template store.
 */
@Parcelize
data class AssistedPayload(
    val toAddress: String? = null,
    val unsubscribeUrl: String? = null,
    val unsubscribeMailto: String? = null,
    val contact: ActionContact? = null,
    val jurisdiction: String? = null,
    val instructions: String? = null,
    val emailTemplate: AssistedEmailTemplate? = null,
) : Parcelable

/**
 * Contact materials for an assisted action.
 *
 * @property email [String] Contact email address, when available.
 * @property phone [String] Contact phone number, when available.
 * @property address [String] Contact postal address, when available.
 */
@Parcelize
data class ActionContact(
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
) : Parcelable

/**
 * An email template returned for an assisted action. The body may contain placeholders (e.g.
 * "{{fullName}}") for fields the user supplies client-side.
 *
 * @property subject [String] Subject line for the email, when available.
 * @property body [String] Body of the email; may contain placeholders.
 */
@Parcelize
data class AssistedEmailTemplate(
    val subject: String? = null,
    val body: String,
) : Parcelable

/**
 * The user-facing intent of an action.
 *
 * Clients must tolerate unknown values: an unrecognised intent is represented as [UNKNOWN] rather
 * than failing, so newly added intents do not break existing clients.
 */
enum class ActionIntent {
    /** Unsubscribe / opt out of marketing communications. */
    STOP_CONTACT,

    /** Data subject access / portability request. */
    ACCESS_MY_DATA,

    /** Account deletion / data erasure. */
    DELETE_MY_DATA,

    /** Opt out of sale / tracking, manage consent. */
    LIMIT_DATA_USE,

    /** Anything unmapped; carries additionalInfo. */
    OTHER,
    UNKNOWN,
}

/**
 * How an action is fulfilled from the user's perspective.
 */
enum class ActionFulfilmentMethod {
    /** The service returns structured materials for the client to complete the action. */
    ASSISTED,

    /** The service performs the action on the user's behalf. */
    DELEGATED,
    UNKNOWN,
}

/**
 * Which source contributed an available action.
 */
enum class ActionOrigin {
    /** Always-possible action for the virtual presence type. */
    STATIC,

    /** Derived from an analysis source's reported actions. */
    ANALYSIS,

    /** Derived from a discovery scan (e.g. List-Unsubscribe). */
    DISCOVERY,
    UNKNOWN,
}

/**
 * Lifecycle status of a persisted action instance.
 */
enum class ActionStatus {
    /** The action has been created but not yet started. */
    PENDING,

    /** The action is being performed. */
    IN_PROGRESS,

    /** The action completed but has not been verified. */
    COMPLETED_UNVERIFIED,

    /** The action completed and has been verified. */
    COMPLETED_VERIFIED,

    /** The action failed to complete. */
    FAILED,

    /** The action requires further input from the user. */
    REQUIRES_USER_INPUT,

    /** Subsequent evidence contradicts the action's completion. */
    CONTRADICTED,
    UNKNOWN,
}
