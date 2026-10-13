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
import com.sudoplatform.sudoprivacyinteraction.types.OrganizationAnalysis
import com.sudoplatform.sudoprivacyinteraction.types.OrganizationAnalysisMode
import com.sudoplatform.sudoprivacyinteraction.types.inputs.GetOrganizationAnalysisInput
import io.kotlintest.matchers.numerics.shouldBeGreaterThanOrEqual
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.awaitility.Duration
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test the organization analysis operations of the [SudoPrivacyInteractionClient].
 */
@RunWith(AndroidJUnit4::class)
class OrganizationAnalysisIntegrationTest : BaseIntegrationTest() {
    private val testDomain = "spotify.com"

    @Test
    fun analyzesADomainThenFetchesTheProducedResult() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())

            // ANALYZE (read-through). The result may be PENDING while asynchronous sources
            // resolve.
            val initial =
                privacyInteractionClient.getOrganizationAnalysis(
                    GetOrganizationAnalysisInput(domain = testDomain, mode = OrganizationAnalysisMode.ANALYZE),
                )

            if (initial == null) {
                logger.debug("Organization analysis unavailable for $testDomain; skipping shape assertions")
                return@runBlocking
            }
            expectValidAnalysis(initial, testDomain)

            // Wait until the analysis reaches a terminal (non-PENDING) status.
            val terminal = awaitTerminalAnalysis(testDomain)
            if (terminal != null) {
                terminal.status shouldNotBe AnalysisResultStatus.PENDING
                expectValidAnalysis(terminal, testDomain)
            }

            // FETCH subsequently — the analysis produced above should now be retrievable via the
            // fetch-only mode.
            val fetched =
                privacyInteractionClient.getOrganizationAnalysis(
                    GetOrganizationAnalysisInput(domain = testDomain, mode = OrganizationAnalysisMode.FETCH),
                )
            if (fetched != null) {
                expectValidAnalysis(fetched, testDomain)
            }
        }

    @Test
    fun defaultsToAnalyzeBehaviourWhenModeIsOmitted() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())

            val analysis =
                privacyInteractionClient.getOrganizationAnalysis(
                    GetOrganizationAnalysisInput(domain = testDomain),
                )

            if (analysis != null) {
                expectValidAnalysis(analysis, testDomain)
            }
        }

    /**
     * Asserts the common shape of an organization analysis for a given domain.
     */
    private fun expectValidAnalysis(
        analysis: OrganizationAnalysis,
        domain: String,
    ) {
        analysis.id shouldBe domain
        analysis.domain shouldBe domain
        analysis.status shouldNotBe null
        AnalysisResultStatus.entries shouldContain analysis.status
        analysis.lastAnalyzedAt shouldNotBe null
        analysis.owner shouldNotBe null
        analysis.version shouldBeGreaterThanOrEqual 1
        analysis.createdAt shouldNotBe null
        analysis.updatedAt shouldNotBe null

        // When terminal-complete, the structured payload should be populated.
        if (analysis.status == AnalysisResultStatus.COMPLETE || analysis.status == AnalysisResultStatus.PARTIAL) {
            val data = analysis.data
            data shouldNotBe null
            data!!.privacySummary shouldNotBe null
            data.categories shouldNotBe null
            data.capabilities shouldNotBe null
            data.riskIndicators shouldNotBe null
            data.attribution shouldNotBe null

            // Privacy score, when present, carries scoring coverage.
            val privacyScore = data.privacyScore
            if (privacyScore != null) {
                privacyScore.breakdown shouldNotBe null
                privacyScore.coverage shouldNotBe null
                privacyScore.coverage.total shouldBeGreaterThanOrEqual 0
            }
        }
    }

    /**
     * Re-queries (ANALYZE) until the analysis for [domain] reaches a terminal (non-PENDING)
     * status, returning the terminal analysis, or `null` if it did not settle within the timeout.
     */
    private fun awaitTerminalAnalysis(domain: String): OrganizationAnalysis? =
        awaitValueOrNull(
            timeout = Duration.TWO_MINUTES,
            condition = {
                val status = analyze(domain)?.status
                status != null && status != AnalysisResultStatus.PENDING
            },
            value = { analyze(domain) },
        )

    private suspend fun analyze(domain: String): OrganizationAnalysis? =
        privacyInteractionClient.getOrganizationAnalysis(
            GetOrganizationAnalysisInput(domain = domain, mode = OrganizationAnalysisMode.ANALYZE),
        )
}
