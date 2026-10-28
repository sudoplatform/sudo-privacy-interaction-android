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
 * The Sudo Platform SDK representation of a virtual presence.
 *
 * A virtual presence is a user's identity on an external platform.
 *
 * @property id [String] Unique identifier of the virtual presence.
 * @property owner [String] Unique identifier of the user associated with the virtual presence.
 * @property providerType [ProviderType] The provider type for this virtual presence.
 * @property identifier [String] The presence identifier (e.g. email address).
 * @property state [VirtualPresenceState] Current state of the virtual presence.
 * @property relationshipProvider [RelationshipProvider] Which relationship provider to use for
 *  discovery. May be `null` if not yet determined.
 * @property lastScannedAt [Date] Time for when the virtual presence was last scanned.
 * @property lastScanFailureReason [String] Reason the most recent completed scan failed (an
 *  error/exception name). `null` when the most recent completed scan succeeded (or none has
 *  completed).
 * @property dataHolderCount [DataHolderCount] Counts of this virtual presence's data holders,
 *  broken down by protection state.
 * @property version [Int] Current version of the virtual presence. Increments on update.
 * @property createdAt [Date] When the virtual presence was created.
 * @property updatedAt [Date] When the virtual presence was last updated.
 */
@Parcelize
data class VirtualPresence(
    val id: String,
    val owner: String,
    val providerType: ProviderType,
    val identifier: String,
    val state: VirtualPresenceState,
    val relationshipProvider: RelationshipProvider?,
    val lastScannedAt: Date,
    val lastScanFailureReason: String? = null,
    val dataHolderCount: DataHolderCount,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date,
) : Parcelable

/**
 * Counts of a [VirtualPresence]'s data holders, broken down by protection state.
 *
 * @property count [Int] Active (non-archived) data holders: the sum of [monitored],
 *  [initiatedActions], and [completedActions]. Excludes [archived].
 * @property monitored [Int] Data holders the system has identified (protection state
 *  [DataHolderProtectionState.MONITORED]).
 * @property initiatedActions [Int] Data holders with an action in progress (protection state
 *  [DataHolderProtectionState.ACTION_REQUESTED]).
 * @property completedActions [Int] Data holders whose action has completed (protection state
 *  [DataHolderProtectionState.RESOLVED]).
 * @property archived [Int] Data holders the user has archived (protection state
 *  [DataHolderProtectionState.ARCHIVED]).
 */
@Parcelize
data class DataHolderCount(
    val count: Int,
    val monitored: Int,
    val initiatedActions: Int,
    val completedActions: Int,
    val archived: Int,
) : Parcelable

/**
 * Enumeration that defines the provider type of the [VirtualPresence].
 */
enum class ProviderType {
    EMAIL,
    UNKNOWN,
}

/**
 * Enumeration that defines the possible states of a [VirtualPresence].
 */
enum class VirtualPresenceState {
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
 * The relationship provider used for discovery on a [VirtualPresence].
 */
enum class RelationshipProvider {
    /** The Gmail relationship provider. */
    GMAIL_PROVIDER,

    /** The test relationship provider. */
    TEST_PROVIDER,
    UNKNOWN,
}
