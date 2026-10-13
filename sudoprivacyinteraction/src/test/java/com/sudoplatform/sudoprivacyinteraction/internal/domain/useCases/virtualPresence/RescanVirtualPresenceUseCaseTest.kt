/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence

import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.inputs.ScanOptionsEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService
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
 * Test the correct operation of [RescanVirtualPresenceUseCase] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class RescanVirtualPresenceUseCaseTest : BaseTests() {
    private val mockVirtualPresenceService by before {
        mock<VirtualPresenceService>()
    }

    private val instanceUnderTest by before {
        RescanVirtualPresenceUseCase(
            virtualPresenceService = mockVirtualPresenceService,
            logger = mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockVirtualPresenceService)
    }

    @Test
    fun `execute() should return the rescanned virtual presence when successful`() =
        runTest {
            val entity = EntityDataFactory.getVirtualPresenceEntity()
            mockVirtualPresenceService.stub {
                onBlocking { rescan(any()) } doReturn entity
            }

            val options =
                ScanOptionsEntity(
                    maximumItemsProcessed = 50,
                    earliestScanDate = "2025-01-01",
                    latestScanDate = "2026-06-01",
                    excludeDomains = listOf("spam.com"),
                    excludeCategories = listOf("newsletters"),
                )

            val result =
                instanceUnderTest.execute(
                    RescanVirtualPresenceUseCaseInput(id = mockVirtualPresenceId, options = options),
                )

            result shouldNotBe null
            result shouldBe entity

            verify(mockVirtualPresenceService).rescan(
                check { input ->
                    input.id shouldBe mockVirtualPresenceId
                    input.options shouldBe options
                },
            )
        }

    @Test
    fun `execute() should pass through null options`() =
        runTest {
            val entity = EntityDataFactory.getVirtualPresenceEntity()
            mockVirtualPresenceService.stub {
                onBlocking { rescan(any()) } doReturn entity
            }

            instanceUnderTest.execute(RescanVirtualPresenceUseCaseInput(id = mockVirtualPresenceId))

            verify(mockVirtualPresenceService).rescan(
                check { input ->
                    input.id shouldBe mockVirtualPresenceId
                    input.options shouldBe null
                },
            )
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockVirtualPresenceService.stub {
                onBlocking {
                    rescan(any())
                } doThrow SudoPrivacyInteractionClient.VirtualPresenceException.VirtualPresenceNotFoundException()
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.VirtualPresenceNotFoundException> {
                instanceUnderTest.execute(RescanVirtualPresenceUseCaseInput(id = mockVirtualPresenceId))
            }

            verify(mockVirtualPresenceService).rescan(any())
        }
}
