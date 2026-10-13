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
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
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
 * Test the correct operation of [GetDataHolderUseCase] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class GetDataHolderUseCaseTest : BaseTests() {
    private val mockDataHolderId = "mockDataHolderId"

    private val mockDataHolderService by before {
        mock<DataHolderService>()
    }

    private val instanceUnderTest by before {
        GetDataHolderUseCase(
            dataHolderService = mockDataHolderService,
            logger = mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockDataHolderService)
    }

    @Test
    fun `execute() should return the data holder entity when successful`() =
        runTest {
            val entity = EntityDataFactory.getDataHolderEntity()
            mockDataHolderService.stub {
                onBlocking { get(any()) } doReturn entity
            }

            val result = instanceUnderTest.execute(mockDataHolderId)

            result shouldNotBe null
            result shouldBe entity

            verify(mockDataHolderService).get(eq(mockDataHolderId))
        }

    @Test
    fun `execute() should return null when data holder not found`() =
        runTest {
            mockDataHolderService.stub {
                onBlocking { get(any()) } doReturn null
            }

            val result = instanceUnderTest.execute(mockDataHolderId)

            result shouldBe null

            verify(mockDataHolderService).get(eq(mockDataHolderId))
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockDataHolderService.stub {
                onBlocking {
                    get(any())
                } doThrow SudoPrivacyInteractionClient.DataHolderException.FailedException()
            }

            shouldThrow<SudoPrivacyInteractionClient.DataHolderException.FailedException> {
                instanceUnderTest.execute(mockDataHolderId)
            }

            verify(mockDataHolderService).get(any())
        }
}
