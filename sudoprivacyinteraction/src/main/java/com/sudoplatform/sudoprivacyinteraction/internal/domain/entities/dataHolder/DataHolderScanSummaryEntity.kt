/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder

import java.util.Date

/**
 * Core entity representation of email interaction metrics captured during a single scan for a
 * specific data holder.
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
internal data class DataHolderScanSummaryEntity(
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
)
