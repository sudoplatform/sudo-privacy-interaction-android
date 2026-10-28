/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder

import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderProtectionStateEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderService
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
 * Test the correct operation of [UnarchiveDataHolderUseCase] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class UnarchiveDataHolderUseCaseTest : BaseTests() {
    private val mockDataHolderId = "mockDataHolderId"

    private val mockDataHolderService by before {
        mock<DataHolderService>()
    }

    private val instanceUnderTest by before {
        UnarchiveDataHolderUseCase(
            dataHolderService = mockDataHolderService,
            logger = mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockDataHolderService)
    }

    @Test
    fun `execute() should return the unarchived data holder when successful`() =
        runTest {
            val entity =
                EntityDataFactory.getDataHolderEntity(
                    protectionState = DataHolderProtectionStateEntity.MONITORED,
                )
            mockDataHolderService.stub {
                onBlocking { unarchive(any()) } doReturn entity
            }

            val result = instanceUnderTest.execute(mockDataHolderId)

            result shouldBe entity
            result.protectionState shouldBe DataHolderProtectionStateEntity.MONITORED
            verify(mockDataHolderService).unarchive(eq(mockDataHolderId))
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockDataHolderService.stub {
                onBlocking {
                    unarchive(any())
                } doThrow SudoPrivacyInteractionClient.DataHolderException.DataHolderNotFoundException()
            }

            shouldThrow<SudoPrivacyInteractionClient.DataHolderException.DataHolderNotFoundException> {
                instanceUnderTest.execute(mockDataHolderId)
            }

            verify(mockDataHolderService).unarchive(eq(mockDataHolderId))
        }
}
