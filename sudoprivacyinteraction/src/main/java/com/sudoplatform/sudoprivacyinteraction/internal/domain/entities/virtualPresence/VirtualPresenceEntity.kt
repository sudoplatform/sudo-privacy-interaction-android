/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence

import java.util.Date

/**
 * Core entity representation of a virtual presence business rule.
 *
 * A virtual presence is a user's identity on an external platform.
 *
 * @property id [String] Unique identifier associated with the virtual presence.
 * @property owner [String] Unique identifier of the user associated with the virtual presence.
 * @property version [Int] Version of this entity. Increments on update.
 * @property providerType [ProviderTypeEntity] The provider type for this virtual presence.
 * @property identifier [String] The presence identifier (e.g. email address).
 * @property state [VirtualPresenceStateEntity] The current state of the virtual presence.
 * @property relationshipProvider [RelationshipProviderEntity] The relationship provider backing
 *  this virtual presence connection. `null` when the service did not supply one.
 * @property lastScannedAt [Date] Date for when the virtual presence was last scanned.
 * @property lastScanFailureReason [String] Reason the most recent completed scan failed (an
 *  error/exception name). `null` when the most recent completed scan succeeded (or none has
 *  completed).
 * @property createdAt [Date] Date for when the virtual presence was created.
 * @property updatedAt [Date] Date for when the virtual presence was last updated.
 */
internal data class VirtualPresenceEntity(
    val id: String,
    val owner: String,
    val version: Int,
    val providerType: ProviderTypeEntity,
    val identifier: String,
    val state: VirtualPresenceStateEntity,
    val relationshipProvider: RelationshipProviderEntity? = null,
    val lastScannedAt: Date,
    val lastScanFailureReason: String? = null,
    val createdAt: Date,
    val updatedAt: Date,
)

/**
 * Enumeration that defines the provider type of the virtual presence.
 */
internal enum class ProviderTypeEntity {
    EMAIL,
    UNKNOWN,
}

/**
 * Enumeration that defines the possible states of a virtual presence.
 */
internal enum class VirtualPresenceStateEntity {
    /** The virtual presence is active and connected to the provider. */
    CONNECTED,

    /** The virtual presence is currently being scanned. */
    SCANNING,

    /** The virtual presence requires re-authentication with the provider. */
    NEEDS_REAUTH,

    /** The virtual presence encountered an error and is in a failed state. */
    ERROR,

    /** The virtual presence is no longer active. */
    INACTIVE,
    UNKNOWN,
}

/**
 * Enumeration of the relationship providers that back a virtual presence connection.
 */
internal enum class RelationshipProviderEntity {
    /** The Gmail relationship provider. */
    GMAIL_PROVIDER,

    /** The test relationship provider. */
    TEST_PROVIDER,
    UNKNOWN,
}
