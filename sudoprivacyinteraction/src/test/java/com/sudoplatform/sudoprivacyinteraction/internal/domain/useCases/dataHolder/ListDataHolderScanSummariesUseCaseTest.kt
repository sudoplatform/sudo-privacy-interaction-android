/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder

import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.ListDataHolderScanSummariesOutput
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner

/**
 * Test the correct operation of [ListDataHolderScanSummariesUseCase] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class ListDataHolderScanSummariesUseCaseTest : BaseTests() {
    private val mockDataHolderId = "mockDataHolderId"

    private val mockDataHolderService by before {
        mock<DataHolderService>()
    }

    private val instanceUnderTest by before {
        ListDataHolderScanSummariesUseCase(
            dataHolderService = mockDataHolderService,
            logger = mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockDataHolderService)
    }

    @Test
    fun `execute() should return the list output when successful`() =
        runTest {
            val output =
                ListDataHolderScanSummariesOutput(
                    items = listOf(EntityDataFactory.getDataHolderScanSummaryEntity()),
                    nextToken = "mockNextToken",
                )
            mockDataHolderService.stub {
                onBlocking { listScanSummaries(any()) } doReturn output
            }

            val result =
                instanceUnderTest.execute(
                    ListDataHolderScanSummariesUseCaseInput(
                        dataHolderId = mockDataHolderId,
                        limit = 5,
                        nextToken = "inputToken",
                    ),
                )

            result shouldNotBe null
            result shouldBe output

            verify(mockDataHolderService).listScanSummaries(
                check { input ->
                    input.dataHolderId shouldBe mockDataHolderId
                    input.limit shouldBe 5
                    input.nextToken shouldBe "inputToken"
                },
            )
        }

    @Test
    fun `execute() should pass through null optional fields`() =
        runTest {
            val output = ListDataHolderScanSummariesOutput(items = emptyList())
            mockDataHolderService.stub {
                onBlocking { listScanSummaries(any()) } doReturn output
            }

            instanceUnderTest.execute(ListDataHolderScanSummariesUseCaseInput(dataHolderId = mockDataHolderId))

            verify(mockDataHolderService).listScanSummaries(
                check { input ->
                    input.dataHolderId shouldBe mockDataHolderId
                    input.limit shouldBe null
                    input.nextToken shouldBe null
                },
            )
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockDataHolderService.stub {
                onBlocking {
                    listScanSummaries(any())
                } doThrow SudoPrivacyInteractionClient.DataHolderException.FailedException()
            }

            shouldThrow<SudoPrivacyInteractionClient.DataHolderException.FailedException> {
                instanceUnderTest.execute(ListDataHolderScanSummariesUseCaseInput(dataHolderId = mockDataHolderId))
            }

            verify(mockDataHolderService).listScanSummaries(any())
        }
}
