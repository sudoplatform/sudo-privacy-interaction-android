/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action

import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionFulfilmentMethodEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionIntentEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionService
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
 * Test the correct operation of [InitiateActionUseCase] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class InitiateActionUseCaseTest : BaseTests() {
    private val mockDataHolderId = "mockDataHolderId"

    private val mockActionService by before { mock<ActionService>() }

    private val instanceUnderTest by before {
        InitiateActionUseCase(actionService = mockActionService, logger = mockLogger)
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockActionService)
    }

    @Test
    fun `execute() should forward input and return the action`() =
        runTest {
            val entity = EntityDataFactory.getActionEntity()
            mockActionService.stub {
                onBlocking { initiate(any()) } doReturn entity
            }

            val result =
                instanceUnderTest.execute(
                    InitiateActionUseCaseInput(
                        dataHolderId = mockDataHolderId,
                        intent = ActionIntentEntity.DELETE_MY_DATA,
                        fulfilmentMethod = ActionFulfilmentMethodEntity.ASSISTED,
                    ),
                )

            result shouldNotBe null
            result shouldBe entity

            verify(mockActionService).initiate(
                check { input ->
                    input.dataHolderId shouldBe mockDataHolderId
                    input.intent shouldBe ActionIntentEntity.DELETE_MY_DATA
                    input.fulfilmentMethod shouldBe ActionFulfilmentMethodEntity.ASSISTED
                },
            )
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockActionService.stub {
                onBlocking { initiate(any()) } doThrow SudoPrivacyInteractionClient.ActionException.FailedException()
            }

            shouldThrow<SudoPrivacyInteractionClient.ActionException.FailedException> {
                instanceUnderTest.execute(
                    InitiateActionUseCaseInput(
                        dataHolderId = mockDataHolderId,
                        intent = ActionIntentEntity.DELETE_MY_DATA,
                        fulfilmentMethod = ActionFulfilmentMethodEntity.ASSISTED,
                    ),
                )
            }

            verify(mockActionService).initiate(any())
        }
}
