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
 * Test the correct operation of [GetActionUseCase] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class GetActionUseCaseTest : BaseTests() {
    private val mockActionId = "mockActionId"

    private val mockActionService by before {
        mock<ActionService>()
    }

    private val instanceUnderTest by before {
        GetActionUseCase(
            actionService = mockActionService,
            logger = mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockActionService)
    }

    @Test
    fun `execute() should return the action when found`() =
        runTest {
            val entity = EntityDataFactory.getActionEntity()
            mockActionService.stub {
                onBlocking { get(any()) } doReturn entity
            }

            val result = instanceUnderTest.execute(mockActionId)

            result shouldBe entity
            verify(mockActionService).get(eq(mockActionId))
        }

    @Test
    fun `execute() should return null when the action is not found`() =
        runTest {
            mockActionService.stub {
                onBlocking { get(any()) } doReturn null
            }

            instanceUnderTest.execute(mockActionId) shouldBe null

            verify(mockActionService).get(eq(mockActionId))
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockActionService.stub {
                onBlocking {
                    get(any())
                } doThrow SudoPrivacyInteractionClient.ActionException.ActionNotFoundException()
            }

            shouldThrow<SudoPrivacyInteractionClient.ActionException.ActionNotFoundException> {
                instanceUnderTest.execute(mockActionId)
            }

            verify(mockActionService).get(eq(mockActionId))
        }
}
