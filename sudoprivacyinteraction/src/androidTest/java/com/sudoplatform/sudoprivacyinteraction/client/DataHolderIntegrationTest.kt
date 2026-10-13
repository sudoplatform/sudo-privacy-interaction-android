/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.client

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sudoplatform.sudoprivacyinteraction.BaseIntegrationTest
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.types.DataHolderProtectionState
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListDataHoldersInput
import io.kotlintest.matchers.numerics.shouldBeGreaterThanOrEqual
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test the data holder query operations of the [SudoPrivacyInteractionClient].
 */
@RunWith(AndroidJUnit4::class)
class DataHolderIntegrationTest : BaseIntegrationTest() {
    @Test
    fun listsDataHoldersForAConnectedVirtualPresence() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("dataholder-list")

            val result =
                privacyInteractionClient.listDataHolders(
                    ListDataHoldersInput(virtualPresenceId = connectedVp.id),
                )

            result shouldNotBe null
            result.items shouldNotBe null

            for (item in result.items) {
                item.id shouldNotBe null
                item.virtualPresenceId shouldBe connectedVp.id
                item.domainName shouldNotBe null
                item.name shouldNotBe null
                item.protectionState shouldNotBe null
                DataHolderProtectionState.entries shouldContain item.protectionState
                item.mostRecentInteractionAt shouldNotBe null
                item.owner shouldNotBe null
                item.version shouldBeGreaterThanOrEqual 1
                item.createdAt shouldNotBe null
                item.updatedAt shouldNotBe null
            }
        }

    @Test
    fun retrievesAListOfDataHoldersRespectingLimitAndPagination() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("dataholder-page")

            val firstPage =
                privacyInteractionClient.listDataHolders(
                    ListDataHoldersInput(virtualPresenceId = connectedVp.id, limit = 1),
                )

            firstPage shouldNotBe null
            (firstPage.items.size <= 1) shouldBe true

            val nextToken = firstPage.nextToken
            if (nextToken != null) {
                val secondPage =
                    privacyInteractionClient.listDataHolders(
                        ListDataHoldersInput(
                            virtualPresenceId = connectedVp.id,
                            limit = 1,
                            nextToken = nextToken,
                        ),
                    )

                secondPage shouldNotBe null
                secondPage.items shouldNotBe null
            }
        }

    @Test
    fun retrievesASingleDataHolderSuccessfully() =
        runBlocking<Unit> {
            assumeTrue(isTestProviderAvailable())
            val connectedVp = connectVirtualPresence("dataholder-get")

            val listResult =
                privacyInteractionClient.listDataHolders(
                    ListDataHoldersInput(virtualPresenceId = connectedVp.id),
                )

            val first = listResult.items.firstOrNull() ?: return@runBlocking
            val dataHolder = privacyInteractionClient.getDataHolder(first.id)

            dataHolder shouldNotBe null
            dataHolder!!.id shouldBe first.id
            dataHolder.virtualPresenceId shouldBe connectedVp.id
            dataHolder.domainName shouldNotBe null
            dataHolder.name shouldNotBe null
            dataHolder.protectionState shouldNotBe null
            dataHolder.mostRecentInteractionAt shouldNotBe null
            dataHolder.createdAt shouldNotBe null
            dataHolder.updatedAt shouldNotBe null
        }

    @Test
    fun returnsNullForNonExistentDataHolder() =
        runBlocking<Unit> {
            val result = privacyInteractionClient.getDataHolder("non-existent-data-holder-id")

            result shouldBe null
        }
}
