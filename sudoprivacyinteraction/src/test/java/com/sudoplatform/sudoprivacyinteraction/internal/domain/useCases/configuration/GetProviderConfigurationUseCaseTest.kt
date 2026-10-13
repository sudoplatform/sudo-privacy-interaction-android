/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.configuration

import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.configuration.ProviderConfigurationService
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner

/**
 * Test the correct operation of [GetProviderConfigurationUseCase].
 */
@RunWith(RobolectricTestRunner::class)
class GetProviderConfigurationUseCaseTest : BaseTests() {
    private val mockProviderConfigurationService by before {
        mock<ProviderConfigurationService>()
    }

    private val instanceUnderTest by before {
        GetProviderConfigurationUseCase(
            providerConfigurationService = mockProviderConfigurationService,
            logger = mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockProviderConfigurationService)
    }

    @Test
    fun `execute() should return the provider configurations when successful`() =
        runTest {
            val entities = listOf(EntityDataFactory.getProviderConfigurationEntity())
            mockProviderConfigurationService.stub {
                onBlocking { get() } doReturn entities
            }

            val result = instanceUnderTest.execute()

            result shouldNotBe null
            result shouldBe entities

            verify(mockProviderConfigurationService).get()
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockProviderConfigurationService.stub {
                onBlocking {
                    get()
                } doThrow SudoPrivacyInteractionClient.VirtualPresenceException.FailedException()
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.FailedException> {
                instanceUnderTest.execute()
            }

            verify(mockProviderConfigurationService).get()
        }
}
