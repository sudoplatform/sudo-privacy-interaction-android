/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.dataHolder.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderScanSummaryEntity
import com.sudoplatform.sudoprivacyinteraction.internal.util.toDate
import com.sudoplatform.sudoprivacyinteraction.types.DataHolderScanSummary
import org.json.JSONObject
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.DataHolderScanSummary as DataHolderScanSummaryFragment

/**
 * Transformer responsible for transforming the data holder scan summary data between the GraphQL
 * type, the entity type used in business logic, and the public type exposed to consumers of the
 * SDK.
 */
internal object DataHolderScanSummaryTransformer {
    /**
     * Transform the [DataHolderScanSummaryFragment] GraphQL type to its entity type.
     *
     * @param data [DataHolderScanSummaryFragment] The GraphQL fragment type.
     * @return The [DataHolderScanSummaryEntity] entity type.
     */
    fun fromGraphQLToEntity(data: DataHolderScanSummaryFragment): DataHolderScanSummaryEntity =
        DataHolderScanSummaryEntity(
            dataHolderId = data.dataHolderId,
            owner = data.owner,
            scannedAt = data.scannedAtEpochMs.toDate(),
            scanRangeFrom = data.scanRangeFromEpochMs.toDate(),
            scanRangeTo = data.scanRangeToEpochMs.toDate(),
            emailCount = data.emailCount,
            readCount = data.readCount,
            readRate = data.readRate,
            marketingEmailCount = data.marketingEmailCount,
            marketingEmailOpened = data.marketingEmailOpened,
            marketingOpenRate = data.marketingOpenRate,
            categoryBreakdown = parseCategoryBreakdown(data.categoryBreakdown),
            uncategorizedCount = data.uncategorizedCount,
        )

    /**
     * Transform a [DataHolderScanSummaryEntity] to its public [DataHolderScanSummary] type.
     *
     * @param entity [DataHolderScanSummaryEntity] The entity to transform.
     * @return The public [DataHolderScanSummary] type.
     */
    fun fromEntityToApi(entity: DataHolderScanSummaryEntity): DataHolderScanSummary =
        DataHolderScanSummary(
            dataHolderId = entity.dataHolderId,
            owner = entity.owner,
            scannedAt = entity.scannedAt,
            scanRangeFrom = entity.scanRangeFrom,
            scanRangeTo = entity.scanRangeTo,
            emailCount = entity.emailCount,
            readCount = entity.readCount,
            readRate = entity.readRate,
            marketingEmailCount = entity.marketingEmailCount,
            marketingEmailOpened = entity.marketingEmailOpened,
            marketingOpenRate = entity.marketingOpenRate,
            categoryBreakdown = entity.categoryBreakdown.toMap(),
            uncategorizedCount = entity.uncategorizedCount,
        )

    /**
     * The `categoryBreakdown` field is an AWSJSON scalar, which is delivered as a JSON-encoded
     * string. Parse it into a category-to-count map. Accepts an already-parsed [Map] for
     * resilience.
     *
     * @param value [Any] The raw value from the GraphQL response.
     * @return A category-to-count map. Returns an empty map when the value cannot be parsed.
     */
    private fun parseCategoryBreakdown(value: Any?): Map<String, Int> {
        val json =
            when (value) {
                null -> return emptyMap()
                is String -> {
                    if (value.isBlank()) return emptyMap()
                    JSONObject(value)
                }
                is Map<*, *> -> {
                    val parsed = mutableMapOf<String, Int>()
                    value.forEach { (key, count) ->
                        val name = key as? String
                        val intCount = (count as? Number)?.toInt()
                        if (name != null && intCount != null) {
                            parsed[name] = intCount
                        }
                    }
                    return parsed
                }
                else -> JSONObject(value.toString())
            }

        val result = mutableMapOf<String, Int>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            result[key] = json.optInt(key)
        }
        return result
    }
}
