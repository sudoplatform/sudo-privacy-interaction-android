/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder

import java.util.Date

/**
 * Core entity representation of a data holder business rule.
 *
 * A data holder is an organization that holds data associated with a user's virtual presence.
 *
 * @property id [String] Unique identifier associated with the data holder.
 * @property owner [String] Unique identifier of the user associated with the data holder.
 * @property virtualPresenceId [String] The virtual presence this data holder is associated with.
 * @property domainName [String] Domain name of the data holder (e.g. "google.com").
 * @property name [String] Display name of the data holder (e.g. "Google").
 * @property protectionState [DataHolderProtectionStateEntity] Current protection state.
 * @property complianceConcern [Boolean] Whether there is a compliance concern with this data holder.
 * @property mostRecentInteractionAt [Date] Date of the most recent interaction.
 * @property version [Int] Version of this entity. Increments on update.
 * @property createdAt [Date] Date for when the data holder was created.
 * @property updatedAt [Date] Date for when the data holder was last updated.
 */
internal data class DataHolderEntity(
    val id: String,
    val owner: String,
    val virtualPresenceId: String,
    val domainName: String,
    val name: String,
    val protectionState: DataHolderProtectionStateEntity,
    val complianceConcern: Boolean,
    val mostRecentInteractionAt: Date,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date,
)

/**
 * Enumeration that defines the protection state of a data holder.
 */
internal enum class DataHolderProtectionStateEntity {
    /** The data holder is being monitored. */
    MONITORED,

    /** An action has been requested against this data holder. */
    ACTION_REQUESTED,

    /** The data holder has been archived by the user. */
    ARCHIVED,

    /** The data holder has been resolved. */
    RESOLVED,
    UNKNOWN,
}
