/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.ListDataHolderScanSummariesOutput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.UseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder.GetDataHolderUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder.ListDataHolderScanSummariesUseCase
import com.sudoplatform.sudoprivacyinteraction.types.DataHolderProtectionState
import com.sudoplatform.sudoprivacyinteraction.types.inputs.GetDataHolderOptions
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
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner

/**
 * Test the correct operation of [SudoPrivacyInteractionClient.getDataHolder]
 * using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class SudoPrivacyInteractionGetDataHolderTest : BaseTests() {
    private val mockDataHolderId = "mockDataHolderId"

    private val scanSummariesOutput by before {
        ListDataHolderScanSummariesOutput(
            items = listOf(EntityDataFactory.getDataHolderScanSummaryEntity()),
            nextToken = null,
        )
    }

    private val mockDataHolderService by before {
        mock<DataHolderService>()
    }

    private val mockGetUseCase by before {
        mock<GetDataHolderUseCase>().stub {
            onBlocking { execute(any()) } doReturn EntityDataFactory.getDataHolderEntity()
        }
    }

    private val mockScanSummariesUseCase by before {
        mock<ListDataHolderScanSummariesUseCase>().stub {
            onBlocking { execute(any()) } doReturn scanSummariesOutput
        }
    }

    private val mockUseCaseFactory by before {
        mock<UseCaseFactory>().stub {
            on { createGetDataHolderUseCase() } doReturn mockGetUseCase
            on { createListDataHolderScanSummariesUseCase() } doReturn mockScanSummariesUseCase
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
            mockGetUseCase,
            mockScanSummariesUseCase,
            mockSignInGuard,
        )
    }

    @Test
    fun `getDataHolder() should return data holder with latest scan summary by default`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.getDataHolder(mockDataHolderId)
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldNotBe null
            result!!.id shouldBe mockDataHolderId
            result.protectionState shouldBe DataHolderProtectionState.MONITORED
            result.latestScanSummary shouldNotBe null
            result.latestScanSummary!!.dataHolderId shouldBe mockDataHolderId
            result.latestScanSummary.categoryBreakdown shouldBe mapOf("promotions" to 3, "updates" to 2)

            verify(mockUseCaseFactory).createGetDataHolderUseCase()
            verify(mockUseCaseFactory).createListDataHolderScanSummariesUseCase()
            verify(mockGetUseCase).execute(eq(mockDataHolderId))
            verify(mockScanSummariesUseCase).execute(
                check { input ->
                    input.dataHolderId shouldBe mockDataHolderId
                    input.limit shouldBe 1
                },
            )
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `getDataHolder() should not fetch scan summary when includeScanSummary is false`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.getDataHolder(mockDataHolderId, GetDataHolderOptions(includeScanSummary = false))
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldNotBe null
            result!!.id shouldBe mockDataHolderId
            result.latestScanSummary shouldBe null

            verify(mockUseCaseFactory).createGetDataHolderUseCase()
            verify(mockUseCaseFactory, never()).createListDataHolderScanSummariesUseCase()
            verify(mockGetUseCase).execute(eq(mockDataHolderId))
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `getDataHolder() should return data holder without scan summary when scan summary fetch fails`() =
        runTest {
            mockScanSummariesUseCase.stub {
                onBlocking { execute(any()) } doThrow
                    SudoPrivacyInteractionClient.DataHolderException.FailedException()
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.getDataHolder(mockDataHolderId)
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldNotBe null
            result!!.id shouldBe mockDataHolderId
            result.latestScanSummary shouldBe null

            verify(mockUseCaseFactory).createGetDataHolderUseCase()
            verify(mockUseCaseFactory).createListDataHolderScanSummariesUseCase()
            verify(mockGetUseCase).execute(eq(mockDataHolderId))
            verify(mockScanSummariesUseCase).execute(any())
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `getDataHolder() should return data holder with null scan summary when none exist`() =
        runTest {
            mockScanSummariesUseCase.stub {
                onBlocking { execute(any()) } doReturn
                    ListDataHolderScanSummariesOutput(items = emptyList(), nextToken = null)
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.getDataHolder(mockDataHolderId)
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldNotBe null
            result!!.latestScanSummary shouldBe null

            verify(mockUseCaseFactory).createGetDataHolderUseCase()
            verify(mockUseCaseFactory).createListDataHolderScanSummariesUseCase()
            verify(mockGetUseCase).execute(eq(mockDataHolderId))
            verify(mockScanSummariesUseCase).execute(any())
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `getDataHolder() should return null when data holder not found`() =
        runTest {
            mockGetUseCase.stub {
                onBlocking { execute(any()) } doReturn null
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.getDataHolder(mockDataHolderId)
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldBe null

            verify(mockUseCaseFactory).createGetDataHolderUseCase()
            verify(mockGetUseCase).execute(eq(mockDataHolderId))
            verify(mockUseCaseFactory).createListDataHolderScanSummariesUseCase()
            verify(mockScanSummariesUseCase).execute(any())
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `getDataHolder() should throw when data holder use case throws`() =
        runTest {
            mockGetUseCase.stub {
                onBlocking { execute(any()) } doThrow
                    SudoPrivacyInteractionClient.DataHolderException.FailedException()
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    shouldThrow<SudoPrivacyInteractionClient.DataHolderException.FailedException> {
                        client.getDataHolder(mockDataHolderId)
                    }
                }
            deferredResult.start()
            deferredResult.await()

            verify(mockUseCaseFactory).createGetDataHolderUseCase()
            verify(mockGetUseCase).execute(eq(mockDataHolderId))
            verify(mockSignInGuard).ensureSignedIn()
        }
}
