/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.client

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sudoplatform.sudoprivacyinteraction.BaseIntegrationTest
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResultStatus
import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResultStatusReason
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListAnalysisResultsInput
import io.kotlintest.matchers.numerics.shouldBeGreaterThanOrEqual
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test the analysis result query operations of the [SudoPrivacyInteractionClient].
 */
@RunWith(AndroidJUnit4::class)
class AnalysisResultIntegrationTest : BaseIntegrationTest() {
    @Test
    fun returnsNullForNonExistentAnalysisResult() =
        runBlocking<Unit> {
            val result = privacyInteractionClient.getAnalysisResult("non-existent-analysis-result-id")

            result shouldBe null
        }

    @Test
    fun returnsAnalysisResultListForAConnectedVirtualPresence() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("analysisresult-list")

            val result =
                privacyInteractionClient.listAnalysisResults(
                    ListAnalysisResultsInput(virtualPresenceId = connectedVp.id),
                )

            result shouldNotBe null
            result.items shouldNotBe null

            for (item in result.items) {
                item.id shouldNotBe null
                item.virtualPresenceId shouldBe connectedVp.id
                item.dataHolderIdentifier shouldNotBe null
                item.status shouldNotBe null
                AnalysisResultStatus.entries shouldContain item.status
                // statusReason is optional; when present it must be a known value.
                item.statusReason?.let { AnalysisResultStatusReason.entries shouldContain it }
                item.lastAnalyzedAt shouldNotBe null
                item.owner shouldNotBe null
                item.version shouldBeGreaterThanOrEqual 1
                item.createdAt shouldNotBe null
                item.updatedAt shouldNotBe null
            }
        }

    @Test
    fun listsAnalysisResultsAndGetsOneWhenAvailable() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("analysisresult-get")

            val first = awaitFirstOrNull { listAnalysisResults(connectedVp.id) } ?: return@runBlocking

            val fetched = privacyInteractionClient.getAnalysisResult(first.id)

            fetched shouldNotBe null
            fetched!!.id shouldBe first.id
            fetched.virtualPresenceId shouldBe connectedVp.id
            fetched.dataHolderIdentifier shouldNotBe null
            fetched.status shouldNotBe null
            // statusReason is optional; when present it must be a known value.
            fetched.statusReason?.let { AnalysisResultStatusReason.entries shouldContain it }
            fetched.lastAnalyzedAt shouldNotBe null

            if (fetched.status == AnalysisResultStatus.COMPLETE || fetched.status == AnalysisResultStatus.PARTIAL) {
                val data = fetched.data
                data shouldNotBe null
                data!!.privacySummary shouldNotBe null
                data.categories shouldNotBe null
                data.capabilities shouldNotBe null
                data.riskIndicators shouldNotBe null
                data.attribution shouldNotBe null
            }
        }

    @Test
    fun respectsTheLimitParameterWhenListingAnalysisResults() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("analysisresult-limit")

            awaitFirstOrNull { listAnalysisResults(connectedVp.id) } ?: return@runBlocking

            val firstPage =
                privacyInteractionClient.listAnalysisResults(
                    ListAnalysisResultsInput(virtualPresenceId = connectedVp.id, limit = 1),
                )

            firstPage shouldNotBe null
            (firstPage.items.size <= 1) shouldBe true

            val nextToken = firstPage.nextToken
            if (nextToken != null && firstPage.items.isNotEmpty()) {
                val secondPage =
                    privacyInteractionClient.listAnalysisResults(
                        ListAnalysisResultsInput(
                            virtualPresenceId = connectedVp.id,
                            limit = 1,
                            nextToken = nextToken,
                        ),
                    )

                secondPage shouldNotBe null
                (secondPage.items.size <= 1) shouldBe true
            }
        }
}
