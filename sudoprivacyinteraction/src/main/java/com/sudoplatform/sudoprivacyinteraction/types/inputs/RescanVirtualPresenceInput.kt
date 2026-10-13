/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.types.inputs

/**
 * Input object containing the properties required to rescan a virtual presence.
 *
 * @property id [String] The unique identifier of the virtual presence to rescan.
 * @property options [ScanOptions] Optional scan options to control the rescan behavior.
 */
data class RescanVirtualPresenceInput(
    val id: String,
    val options: ScanOptions? = null,
)

/**
 * Options to control a rescan operation.
 *
 * @property maximumItemsProcessed [Int] Maximum number of items to process during the scan.
 * @property earliestScanDate [String] Earliest date boundary for the scan (ISO 8601 string).
 * @property latestScanDate [String] Latest date boundary for the scan (ISO 8601 string).
 * @property excludeDomains [List] Domains to exclude from the scan.
 * @property excludeCategories [List] Categories to exclude from the scan.
 */
data class ScanOptions(
    val maximumItemsProcessed: Int? = null,
    val earliestScanDate: String? = null,
    val latestScanDate: String? = null,
    val excludeDomains: List<String>? = null,
    val excludeCategories: List<String>? = null,
)
