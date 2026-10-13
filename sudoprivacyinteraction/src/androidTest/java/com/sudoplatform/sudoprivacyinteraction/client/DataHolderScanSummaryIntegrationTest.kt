/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.client

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sudoplatform.sudoprivacyinteraction.BaseIntegrationTest
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.types.inputs.GetDataHolderOptions
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListDataHolderScanSummariesInput
import io.kotlintest.matchers.numerics.shouldBeGreaterThanOrEqual
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test the data holder scan summary operations of the [SudoPrivacyInteractionClient].
 */
@RunWith(AndroidJUnit4::class)
class DataHolderScanSummaryIntegrationTest : BaseIntegrationTest() {
    @Test
    fun listsScanSummariesForADataHolderWhenAvailable() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("scansummary-list")

            val dataHolderId = awaitFirstDataHolderId(connectedVp.id) ?: return@runBlocking

            val result =
                privacyInteractionClient.listDataHolderScanSummaries(
                    ListDataHolderScanSummariesInput(dataHolderId = dataHolderId),
                )

            result shouldNotBe null
            result.items shouldNotBe null

            for (item in result.items) {
                item.dataHolderId shouldBe dataHolderId
                item.owner shouldNotBe null
                item.scannedAt shouldNotBe null
                item.scanRangeFrom shouldNotBe null
                item.scanRangeTo shouldNotBe null
                item.emailCount shouldBeGreaterThanOrEqual 0
                item.readCount shouldBeGreaterThanOrEqual 0
                item.marketingEmailCount shouldBeGreaterThanOrEqual 0
                item.marketingEmailOpened shouldBeGreaterThanOrEqual 0
                item.uncategorizedCount shouldBeGreaterThanOrEqual 0
                item.categoryBreakdown shouldNotBe null
            }
        }

    @Test
    fun returnsAnEmptyListForADataHolderWithNoScans() =
        runBlocking<Unit> {
            val result =
                privacyInteractionClient.listDataHolderScanSummaries(
                    ListDataHolderScanSummariesInput(dataHolderId = "non-existent-data-holder-id"),
                )

            result shouldNotBe null
            result.items shouldNotBe null
            result.items.size shouldBe 0
        }

    @Test
    fun respectsTheLimitParameterWhenListingScanSummaries() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("scansummary-limit")

            val dataHolderId = awaitFirstDataHolderId(connectedVp.id) ?: return@runBlocking

            val firstPage =
                privacyInteractionClient.listDataHolderScanSummaries(
                    ListDataHolderScanSummariesInput(dataHolderId = dataHolderId, limit = 1),
                )

            firstPage shouldNotBe null
            (firstPage.items.size <= 1) shouldBe true

            val nextToken = firstPage.nextToken
            if (nextToken != null && firstPage.items.isNotEmpty()) {
                val secondPage =
                    privacyInteractionClient.listDataHolderScanSummaries(
                        ListDataHolderScanSummariesInput(
                            dataHolderId = dataHolderId,
                            limit = 1,
                            nextToken = nextToken,
                        ),
                    )

                secondPage shouldNotBe null
                (secondPage.items.size <= 1) shouldBe true
                if (secondPage.items.isNotEmpty()) {
                    secondPage.items[0].scannedAt.time shouldNotBe firstPage.items[0].scannedAt.time
                }
            }
        }

    @Test
    fun attachesLatestScanSummaryByDefault() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("scansummary-get")

            val dataHolderId = awaitFirstDataHolderId(connectedVp.id) ?: return@runBlocking

            val dataHolder = privacyInteractionClient.getDataHolder(dataHolderId)

            dataHolder shouldNotBe null
            dataHolder!!.id shouldBe dataHolderId
            val summary = dataHolder.latestScanSummary
            if (summary != null) {
                summary.dataHolderId shouldBe dataHolderId
                summary.scannedAt shouldNotBe null
                summary.emailCount shouldBeGreaterThanOrEqual 0
            }
        }

    @Test
    fun doesNotAttachLatestScanSummaryWhenIncludeScanSummaryIsFalse() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("scansummary-opt-out")

            val dataHolderId = awaitFirstDataHolderId(connectedVp.id) ?: return@runBlocking

            val dataHolder =
                privacyInteractionClient.getDataHolder(
                    dataHolderId,
                    GetDataHolderOptions(includeScanSummary = false),
                )

            dataHolder shouldNotBe null
            dataHolder!!.id shouldBe dataHolderId
            dataHolder.latestScanSummary shouldBe null
        }
}
