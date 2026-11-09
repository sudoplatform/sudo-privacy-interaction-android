/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action

import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ListActionsForDataHolderOutput
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
 * Test the correct operation of [ListActionsForDataHolderUseCase] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class ListActionsForDataHolderUseCaseTest : BaseTests() {
    private val mockDataHolderId = "mockDataHolderId"

    private val mockActionService by before { mock<ActionService>() }

    private val instanceUnderTest by before {
        ListActionsForDataHolderUseCase(actionService = mockActionService, logger = mockLogger)
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockActionService)
    }

    @Test
    fun `execute() should return the list output when successful`() =
        runTest {
            val output =
                ListActionsForDataHolderOutput(
                    items = listOf(EntityDataFactory.getActionEntity()),
                    nextToken = "mockNextToken",
                )
            mockActionService.stub {
                onBlocking { listForDataHolder(any()) } doReturn output
            }

            val result = instanceUnderTest.execute(ListActionsForDataHolderUseCaseInput(dataHolderId = mockDataHolderId))

            result shouldNotBe null
            result shouldBe output

            verify(mockActionService).listForDataHolder(
                check { it.dataHolderId shouldBe mockDataHolderId },
            )
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockActionService.stub {
                onBlocking { listForDataHolder(any()) } doThrow SudoPrivacyInteractionClient.ActionException.FailedException()
            }

            shouldThrow<SudoPrivacyInteractionClient.ActionException.FailedException> {
                instanceUnderTest.execute(ListActionsForDataHolderUseCaseInput(dataHolderId = mockDataHolderId))
            }

            verify(mockActionService).listForDataHolder(any())
        }
}
