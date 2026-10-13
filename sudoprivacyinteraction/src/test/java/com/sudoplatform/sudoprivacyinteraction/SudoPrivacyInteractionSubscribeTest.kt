/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.UseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.subscription.AnalysisResultSubscriber
import com.sudoplatform.sudoprivacyinteraction.subscription.DataHolderSubscriber
import com.sudoplatform.sudoprivacyinteraction.subscription.SubscriptionService
import com.sudoplatform.sudoprivacyinteraction.subscription.VirtualPresenceSubscriber
import io.kotlintest.shouldThrow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner

/**
 * Test the correct operation of the [SudoPrivacyInteractionClient] subscribe / unsubscribe methods
 * using mocks and spies. These verify the client delegates to the [SubscriptionService].
 */
@RunWith(RobolectricTestRunner::class)
class SudoPrivacyInteractionSubscribeTest : BaseTests() {
    private val mockVirtualPresenceSubscriber by before { mock<VirtualPresenceSubscriber>() }
    private val mockDataHolderSubscriber by before { mock<DataHolderSubscriber>() }
    private val mockAnalysisResultSubscriber by before { mock<AnalysisResultSubscriber>() }

    private val mockUseCaseFactory by before { mock<UseCaseFactory>() }

    private val mockSubscriptionService by before { mock<SubscriptionService>() }

    private val client by before {
        DefaultSudoPrivacyInteractionClient(
            context = mockContext,
            apiClient = mockApiClient,
            sudoUserClient = mockUserClient,
            logger = mockLogger,
            subscriptionService = mockSubscriptionService,
            useCaseFactory = mockUseCaseFactory,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(
            mockContext,
            mockUserClient,
            mockApiClient,
            mockUseCaseFactory,
            mockSubscriptionService,
        )
    }

    @Test
    fun `subscribeToVirtualPresence() should delegate to the subscription service`() =
        runTest {
            client.subscribeToVirtualPresence("sub-id", mockVirtualPresenceSubscriber)

            verify(mockSubscriptionService).subscribeToVirtualPresence(eq("sub-id"), eq(mockVirtualPresenceSubscriber))
        }

    @Test
    fun `subscribeToVirtualPresence() should propagate service exceptions`() =
        runTest {
            mockSubscriptionService.stub {
                onBlocking {
                    subscribeToVirtualPresence(any(), any())
                } doThrow SudoPrivacyInteractionClient.VirtualPresenceException.AuthenticationException()
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.AuthenticationException> {
                client.subscribeToVirtualPresence("sub-id", mockVirtualPresenceSubscriber)
            }

            verify(mockSubscriptionService).subscribeToVirtualPresence(any(), any())
        }

    @Test
    fun `unsubscribeFromVirtualPresence() should delegate to the subscription service`() =
        runTest {
            client.unsubscribeFromVirtualPresence("sub-id")

            verify(mockSubscriptionService).unsubscribeFromVirtualPresence(eq("sub-id"))
        }

    @Test
    fun `unsubscribeAllFromVirtualPresence() should delegate to the subscription service`() =
        runTest {
            client.unsubscribeAllFromVirtualPresence()

            verify(mockSubscriptionService).unsubscribeAllFromVirtualPresence()
        }

    @Test
    fun `subscribeToDataHolders() should delegate to the subscription service`() =
        runTest {
            client.subscribeToDataHolders("sub-id", mockDataHolderSubscriber)

            verify(mockSubscriptionService).subscribeToDataHolders(eq("sub-id"), eq(mockDataHolderSubscriber))
        }

    @Test
    fun `subscribeToDataHolders() should propagate service exceptions`() =
        runTest {
            mockSubscriptionService.stub {
                onBlocking {
                    subscribeToDataHolders(any(), any())
                } doThrow SudoPrivacyInteractionClient.DataHolderException.AuthenticationException()
            }

            shouldThrow<SudoPrivacyInteractionClient.DataHolderException.AuthenticationException> {
                client.subscribeToDataHolders("sub-id", mockDataHolderSubscriber)
            }

            verify(mockSubscriptionService).subscribeToDataHolders(any(), any())
        }

    @Test
    fun `unsubscribeFromDataHolders() should delegate to the subscription service`() =
        runTest {
            client.unsubscribeFromDataHolders("sub-id")

            verify(mockSubscriptionService).unsubscribeFromDataHolders(eq("sub-id"))
        }

    @Test
    fun `unsubscribeAllFromDataHolders() should delegate to the subscription service`() =
        runTest {
            client.unsubscribeAllFromDataHolders()

            verify(mockSubscriptionService).unsubscribeAllFromDataHolders()
        }

    @Test
    fun `subscribeToAnalysisResult() should delegate to the subscription service`() =
        runTest {
            client.subscribeToAnalysisResult("sub-id", mockAnalysisResultSubscriber)

            verify(mockSubscriptionService).subscribeToAnalysisResult(eq("sub-id"), eq(mockAnalysisResultSubscriber))
        }

    @Test
    fun `subscribeToAnalysisResult() should propagate service exceptions`() =
        runTest {
            mockSubscriptionService.stub {
                onBlocking {
                    subscribeToAnalysisResult(any(), any())
                } doThrow SudoPrivacyInteractionClient.AnalysisResultException.AuthenticationException()
            }

            shouldThrow<SudoPrivacyInteractionClient.AnalysisResultException.AuthenticationException> {
                client.subscribeToAnalysisResult("sub-id", mockAnalysisResultSubscriber)
            }

            verify(mockSubscriptionService).subscribeToAnalysisResult(any(), any())
        }

    @Test
    fun `unsubscribeFromAnalysisResult() should delegate to the subscription service`() =
        runTest {
            client.unsubscribeFromAnalysisResult("sub-id")

            verify(mockSubscriptionService).unsubscribeFromAnalysisResult(eq("sub-id"))
        }

    @Test
    fun `unsubscribeAllFromAnalysisResult() should delegate to the subscription service`() =
        runTest {
            client.unsubscribeAllFromAnalysisResult()

            verify(mockSubscriptionService).unsubscribeAllFromAnalysisResult()
        }

    @Test
    fun `close() should close the subscription service`() =
        runTest {
            client.close()

            verify(mockSubscriptionService).close()
        }
}
