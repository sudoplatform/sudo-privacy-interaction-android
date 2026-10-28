/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderProtectionStateEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.UseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder.UnarchiveDataHolderUseCase
import com.sudoplatform.sudoprivacyinteraction.types.DataHolderProtectionState
import com.sudoplatform.sudouser.SignInGuard
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
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
 * Test the correct operation of [SudoPrivacyInteractionClient.unarchiveDataHolder] using mocks
 * and spies.
 */
@RunWith(RobolectricTestRunner::class)
class SudoPrivacyInteractionUnarchiveDataHolderTest : BaseTests() {
    private val mockDataHolderId = "mockDataHolderId"

    private val unarchivedEntity by before {
        EntityDataFactory.getDataHolderEntity(
            protectionState = DataHolderProtectionStateEntity.MONITORED,
        )
    }

    private val mockDataHolderService by before {
        mock<DataHolderService>()
    }

    private val mockUseCase by before {
        mock<UnarchiveDataHolderUseCase>().stub {
            onBlocking { execute(any()) } doReturn unarchivedEntity
        }
    }

    private val mockUseCaseFactory by before {
        mock<UseCaseFactory>().stub {
            on { createUnarchiveDataHolderUseCase() } doReturn mockUseCase
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
            dataHolderService = mockDataHolderService,
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
    fun `unarchiveDataHolder() should return the data holder returned to monitored`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.unarchiveDataHolder(mockDataHolderId)
                }
            deferredResult.start()
            val result = deferredResult.await()

            result.id shouldBe mockDataHolderId
            result.protectionState shouldBe DataHolderProtectionState.MONITORED

            verify(mockUseCaseFactory).createUnarchiveDataHolderUseCase()
            verify(mockUseCase).execute(eq(mockDataHolderId))
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `unarchiveDataHolder() should throw when use case throws`() =
        runTest {
            mockUseCase.stub {
                onBlocking {
                    execute(any())
                } doThrow SudoPrivacyInteractionClient.DataHolderException.DataHolderNotFoundException()
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    shouldThrow<SudoPrivacyInteractionClient.DataHolderException.DataHolderNotFoundException> {
                        client.unarchiveDataHolder(mockDataHolderId)
                    }
                }
            deferredResult.start()
            deferredResult.await()

            verify(mockUseCaseFactory).createUnarchiveDataHolderUseCase()
            verify(mockUseCase).execute(eq(mockDataHolderId))
            verify(mockSignInGuard).ensureSignedIn()
        }
}
