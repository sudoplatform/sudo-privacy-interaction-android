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
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder.ArchiveDataHolderUseCase
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
import java.util.Date

/**
 * Test the correct operation of [SudoPrivacyInteractionClient.archiveDataHolder] using mocks and
 * spies.
 */
@RunWith(RobolectricTestRunner::class)
class SudoPrivacyInteractionArchiveDataHolderTest : BaseTests() {
    private val mockDataHolderId = "mockDataHolderId"

    private val archivedEntity by before {
        EntityDataFactory.getDataHolderEntity(
            protectionState = DataHolderProtectionStateEntity.ARCHIVED,
        )
    }

    private val mockDataHolderService by before {
        mock<DataHolderService>()
    }

    private val mockUseCase by before {
        mock<ArchiveDataHolderUseCase>().stub {
            onBlocking { execute(any()) } doReturn archivedEntity
        }
    }

    private val mockUseCaseFactory by before {
        mock<UseCaseFactory>().stub {
            on { createArchiveDataHolderUseCase() } doReturn mockUseCase
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
    fun `archiveDataHolder() should return the archived data holder`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.archiveDataHolder(mockDataHolderId)
                }
            deferredResult.start()
            val result = deferredResult.await()

            with(result) {
                id shouldBe mockDataHolderId
                owner shouldBe mockOwner
                virtualPresenceId shouldBe mockVirtualPresenceId
                domainName shouldBe "google.com"
                name shouldBe "Google"
                protectionState shouldBe DataHolderProtectionState.ARCHIVED
                complianceConcern shouldBe false
                mostRecentInteractionAt shouldBe Date(1L)
                version shouldBe 1
                createdAt shouldBe Date(1L)
                updatedAt shouldBe Date(1L)
                latestScanSummary shouldBe null
            }

            verify(mockUseCaseFactory).createArchiveDataHolderUseCase()
            verify(mockUseCase).execute(eq(mockDataHolderId))
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `archiveDataHolder() should throw when use case throws`() =
        runTest {
            mockUseCase.stub {
                onBlocking {
                    execute(any())
                } doThrow SudoPrivacyInteractionClient.DataHolderException.DataHolderNotFoundException()
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    shouldThrow<SudoPrivacyInteractionClient.DataHolderException.DataHolderNotFoundException> {
                        client.archiveDataHolder(mockDataHolderId)
                    }
                }
            deferredResult.start()
            deferredResult.await()

            verify(mockUseCaseFactory).createArchiveDataHolderUseCase()
            verify(mockUseCase).execute(eq(mockDataHolderId))
            verify(mockSignInGuard).ensureSignedIn()
        }
}
