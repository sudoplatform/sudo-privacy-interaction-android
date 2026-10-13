package com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.inputs

/**
 * Options to control a rescan operation.
 *
 * @property maximumItemsProcessed [Int] Maximum number of items to process during the scan.
 * @property earliestScanDate [String] Earliest date boundary for the scan (ISO 8601 string).
 * @property latestScanDate [String] Latest date boundary for the scan (ISO 8601 string).
 * @property excludeDomains [List] Domains to exclude from the scan.
 * @property excludeCategories [List] Categories to exclude from the scan.
 */
data class ScanOptionsEntity(
    val maximumItemsProcessed: Int? = null,
    val earliestScanDate: String? = null,
    val latestScanDate: String? = null,
    val excludeDomains: List<String>? = null,
    val excludeCategories: List<String>? = null,
)
