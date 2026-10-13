/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.ProviderTypeEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.configuration.ProviderConfigurationService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.UseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.configuration.GetProviderConfigurationUseCase
import com.sudoplatform.sudoprivacyinteraction.types.ProviderType
import com.sudoplatform.sudouser.SignInGuard
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
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
 * Test the correct operation of [SudoPrivacyInteractionClient.getProviderConfiguration]
 * using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class SudoPrivacyInteractionGetProviderConfigurationTest : BaseTests() {
    private val providerConfigurationEntities by before {
        listOf(
            EntityDataFactory.getProviderConfigurationEntity(
                name = "google",
                providerType = ProviderTypeEntity.EMAIL,
                clientId = "mockClientId",
            ),
        )
    }

    private val mockVirtualPresenceService by before {
        mock<VirtualPresenceService>()
    }

    private val mockProviderConfigurationService by before {
        mock<ProviderConfigurationService>()
    }

    private val mockUseCase by before {
        mock<GetProviderConfigurationUseCase>().stub {
            onBlocking {
                execute()
            } doReturn providerConfigurationEntities
        }
    }

    private val mockUseCaseFactory by before {
        mock<UseCaseFactory>().stub {
            on { createGetProviderConfigurationUseCase() } doReturn mockUseCase
        }
    }

    private val mockSignInGuard by before {
        mock<SignInGuard>()
    }

    private val client by before {
        DefaultSudoPrivacyInteractionClient(
            context = mockContext,
            apiClient = mockApiClient,
            sudoUserClient = mockUserClient,
            logger = mockLogger,
            virtualPresenceService = mockVirtualPresenceService,
            providerConfigurationService = mockProviderConfigurationService,
            useCaseFactory = mockUseCaseFactory,
            signInGuard = mockSignInGuard,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(
            mockContext,
            mockUserClient,
            mockApiClient,
            mockUseCaseFactory,
            mockUseCase,
            mockSignInGuard,
        )
    }

    @Test
    fun `getProviderConfiguration() should return results when no error present`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.getProviderConfiguration()
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldNotBe null
            result.size shouldBe 1
            with(result[0]) {
                name shouldBe "google"
                providerType shouldBe ProviderType.EMAIL
                clientId shouldBe "mockClientId"
            }

            verify(mockUseCaseFactory).createGetProviderConfigurationUseCase()
            verify(mockUseCase).execute()
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `getProviderConfiguration() should throw when use case throws`() =
        runTest {
            mockUseCase.stub {
                onBlocking {
                    execute()
                } doThrow SudoPrivacyInteractionClient.VirtualPresenceException.FailedException()
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.FailedException> {
                        client.getProviderConfiguration()
                    }
                }
            deferredResult.start()
            deferredResult.await()

            verify(mockUseCaseFactory).createGetProviderConfigurationUseCase()
            verify(mockUseCase).execute()
            verify(mockSignInGuard).ensureSignedIn()
        }
}
