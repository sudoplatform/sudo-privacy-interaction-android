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
 * The Sudo Platform SDK representation of a data holder.
 *
 * A data holder is an organization that holds data associated with a user's virtual presence.
 *
 * @property id [String] Unique identifier associated with the data holder.
 * @property owner [String] Unique identifier of the user associated with the data holder.
 * @property virtualPresenceId [String] The virtual presence this data holder is associated with.
 * @property domainName [String] Domain name of the data holder (e.g. "google.com").
 * @property name [String] Display name of the data holder (e.g. "Google").
 * @property protectionState [DataHolderProtectionState] Current protection state.
 * @property complianceConcern [Boolean] Whether there is a compliance concern with this data holder.
 * @property mostRecentInteractionAt [Date] Date of the most recent interaction.
 * @property version [Int] Version of this entity. Increments on update.
 * @property createdAt [Date] Date for when the data holder was created.
 * @property updatedAt [Date] Date for when the data holder was last updated.
 * @property latestScanSummary [DataHolderScanSummary] The most recent scan summary for this
 *  data holder, when requested via `getDataHolder`. `null` when not requested or when the data
 *  holder has no scan summaries.
 */
@Parcelize
data class DataHolder(
    val id: String,
    val owner: String,
    val virtualPresenceId: String,
    val domainName: String,
    val name: String,
    val protectionState: DataHolderProtectionState,
    val complianceConcern: Boolean,
    val mostRecentInteractionAt: Date,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date,
    val latestScanSummary: DataHolderScanSummary? = null,
) : Parcelable

/**
 * The protection state of a [DataHolder].
 */
enum class DataHolderProtectionState {
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

/**
 * The Sudo Platform SDK representation of email interaction metrics captured during a single
 * scan for a specific data holder.
 *
 * @property dataHolderId [String] The data holder these metrics apply to.
 * @property owner [String] Unique identifier of the user associated with the scan summary.
 * @property scannedAt [Date] Date for when the scan was performed.
 * @property scanRangeFrom [Date] Start of the scanned time range.
 * @property scanRangeTo [Date] End of the scanned time range.
 * @property emailCount [Int] Total number of emails observed in the scan range.
 * @property readCount [Int] Number of emails that were read.
 * @property readRate [Double] Ratio of read emails to total emails.
 * @property marketingEmailCount [Int] Number of marketing emails observed.
 * @property marketingEmailOpened [Int] Number of marketing emails that were opened.
 * @property marketingOpenRate [Double] Ratio of opened marketing emails to marketing emails.
 * @property categoryBreakdown [Map] Count of emails per category.
 * @property uncategorizedCount [Int] Number of emails that could not be categorized.
 */
@Parcelize
data class DataHolderScanSummary(
    val dataHolderId: String,
    val owner: String,
    val scannedAt: Date,
    val scanRangeFrom: Date,
    val scanRangeTo: Date,
    val emailCount: Int,
    val readCount: Int,
    val readRate: Double,
    val marketingEmailCount: Int,
    val marketingEmailOpened: Int,
    val marketingOpenRate: Double,
    val categoryBreakdown: Map<String, Int>,
    val uncategorizedCount: Int,
) : Parcelable
