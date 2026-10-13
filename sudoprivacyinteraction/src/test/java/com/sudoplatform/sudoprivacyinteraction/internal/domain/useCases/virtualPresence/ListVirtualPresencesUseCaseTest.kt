/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence

import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ListVirtualPresencesOutput
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
 * Test the correct operation of [ListVirtualPresencesUseCase] using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class ListVirtualPresencesUseCaseTest : BaseTests() {
    private val mockVirtualPresenceService by before {
        mock<VirtualPresenceService>()
    }

    private val instanceUnderTest by before {
        ListVirtualPresencesUseCase(
            virtualPresenceService = mockVirtualPresenceService,
            logger = mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockVirtualPresenceService)
    }

    @Test
    fun `execute() should return the list output when successful`() =
        runTest {
            val output =
                ListVirtualPresencesOutput(
                    items = listOf(EntityDataFactory.getVirtualPresenceEntity()),
                    nextToken = "mockNextToken",
                )
            mockVirtualPresenceService.stub {
                onBlocking { list(any()) } doReturn output
            }

            val result =
                instanceUnderTest.execute(
                    ListVirtualPresencesUseCaseInput(limit = 10, nextToken = "inputToken"),
                )

            result shouldNotBe null
            result shouldBe output

            verify(mockVirtualPresenceService).list(
                check { input ->
                    input.limit shouldBe 10
                    input.nextToken shouldBe "inputToken"
                },
            )
        }

    @Test
    fun `execute() should pass through null optional fields`() =
        runTest {
            val output = ListVirtualPresencesOutput(items = emptyList())
            mockVirtualPresenceService.stub {
                onBlocking { list(any()) } doReturn output
            }

            instanceUnderTest.execute(ListVirtualPresencesUseCaseInput())

            verify(mockVirtualPresenceService).list(
                check { input ->
                    input.limit shouldBe null
                    input.nextToken shouldBe null
                },
            )
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockVirtualPresenceService.stub {
                onBlocking {
                    list(any())
                } doThrow SudoPrivacyInteractionClient.VirtualPresenceException.FailedException()
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.FailedException> {
                instanceUnderTest.execute(ListVirtualPresencesUseCaseInput())
            }

            verify(mockVirtualPresenceService).list(any())
        }
}
