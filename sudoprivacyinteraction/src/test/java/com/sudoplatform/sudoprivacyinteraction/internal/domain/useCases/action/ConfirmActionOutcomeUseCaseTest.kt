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
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner

/**
 * Test the correct operation of [ConfirmActionOutcomeUseCase] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class ConfirmActionOutcomeUseCaseTest : BaseTests() {
    private val mockActionId = "mockActionId"

    private val mockActionService by before { mock<ActionService>() }

    private val instanceUnderTest by before {
        ConfirmActionOutcomeUseCase(actionService = mockActionService, logger = mockLogger)
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockActionService)
    }

    @Test
    fun `execute() should forward id and return the action`() =
        runTest {
            val entity = EntityDataFactory.getActionEntity()
            mockActionService.stub {
                onBlocking { confirmOutcome(any()) } doReturn entity
            }

            val result = instanceUnderTest.execute(mockActionId)

            result shouldBe entity

            verify(mockActionService).confirmOutcome(eq(mockActionId))
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockActionService.stub {
                onBlocking {
                    confirmOutcome(any())
                } doThrow SudoPrivacyInteractionClient.ActionException.ActionNotFoundException()
            }

            shouldThrow<SudoPrivacyInteractionClient.ActionException.ActionNotFoundException> {
                instanceUnderTest.execute(mockActionId)
            }

            verify(mockActionService).confirmOutcome(any())
        }
}
