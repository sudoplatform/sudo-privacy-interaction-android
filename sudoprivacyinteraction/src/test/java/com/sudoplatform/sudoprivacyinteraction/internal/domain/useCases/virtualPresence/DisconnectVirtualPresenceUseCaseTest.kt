/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence

import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService
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
 * Test the correct operation of [DisconnectVirtualPresenceUseCase]
 * using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class DisconnectVirtualPresenceUseCaseTest : BaseTests() {
    private val mockVirtualPresenceService by before {
        mock<VirtualPresenceService>()
    }

    private val instanceUnderTest by before {
        DisconnectVirtualPresenceUseCase(
            virtualPresenceService = mockVirtualPresenceService,
            logger = mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockVirtualPresenceService)
    }

    @Test
    fun `execute() should return the disconnected virtual presence when successful`() =
        runTest {
            val entity = EntityDataFactory.getVirtualPresenceEntity()
            mockVirtualPresenceService.stub {
                onBlocking { disconnect(any()) } doReturn entity
            }

            val result = instanceUnderTest.execute(mockVirtualPresenceId)

            result shouldNotBe null
            result shouldBe entity

            verify(mockVirtualPresenceService).disconnect(eq(mockVirtualPresenceId))
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockVirtualPresenceService.stub {
                onBlocking {
                    disconnect(any())
                } doThrow SudoPrivacyInteractionClient.VirtualPresenceException.VirtualPresenceNotFoundException()
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.VirtualPresenceNotFoundException> {
                instanceUnderTest.execute(mockVirtualPresenceId)
            }

            verify(mockVirtualPresenceService).disconnect(eq(mockVirtualPresenceId))
        }
}
