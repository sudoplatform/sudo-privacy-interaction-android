/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisModeEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.UseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.organizationAnalysis.GetOrganizationAnalysisUseCase
import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResultStatus
import com.sudoplatform.sudoprivacyinteraction.types.OrganizationAnalysisMode
import com.sudoplatform.sudoprivacyinteraction.types.inputs.GetOrganizationAnalysisInput
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner

/**
 * Test the correct operation of [SudoPrivacyInteractionClient.getOrganizationAnalysis]
 * using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class SudoPrivacyInteractionGetOrganizationAnalysisTest : BaseTests() {
    private val mockDomain = "netflix.com"

    private val mockOrganizationAnalysisService by before {
        mock<OrganizationAnalysisService>()
    }

    private val mockUseCase by before {
        mock<GetOrganizationAnalysisUseCase>().stub {
            onBlocking { execute(any()) } doReturn EntityDataFactory.getOrganizationAnalysisEntity()
        }
    }

    private val mockUseCaseFactory by before {
        mock<UseCaseFactory>().stub {
            on { createGetOrganizationAnalysisUseCase() } doReturn mockUseCase
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
            organizationAnalysisService = mockOrganizationAnalysisService,
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
    fun `getOrganizationAnalysis() should return mapped result when no error present`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.getOrganizationAnalysis(
                        GetOrganizationAnalysisInput(domain = mockDomain, mode = OrganizationAnalysisMode.FETCH),
                    )
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldNotBe null
            result!!.id shouldBe mockDomain
            result.domain shouldBe mockDomain
            result.status shouldBe AnalysisResultStatus.COMPLETE
            result.data shouldNotBe null

            verify(mockUseCaseFactory).createGetOrganizationAnalysisUseCase()
            verify(mockUseCase).execute(
                check { useCaseInput ->
                    useCaseInput.domain shouldBe mockDomain
                    useCaseInput.mode shouldBe OrganizationAnalysisModeEntity.FETCH
                },
            )
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `getOrganizationAnalysis() should pass null mode through`() =
        runTest {
            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.getOrganizationAnalysis(GetOrganizationAnalysisInput(domain = mockDomain))
                }
            deferredResult.start()
            deferredResult.await()

            verify(mockUseCaseFactory).createGetOrganizationAnalysisUseCase()
            verify(mockUseCase).execute(
                check { useCaseInput ->
                    useCaseInput.domain shouldBe mockDomain
                    useCaseInput.mode shouldBe null
                },
            )
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `getOrganizationAnalysis() should return null when use case returns null`() =
        runTest {
            mockUseCase.stub {
                onBlocking { execute(any()) } doReturn null
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    client.getOrganizationAnalysis(GetOrganizationAnalysisInput(domain = mockDomain))
                }
            deferredResult.start()
            val result = deferredResult.await()

            result shouldBe null

            verify(mockUseCaseFactory).createGetOrganizationAnalysisUseCase()
            verify(mockUseCase).execute(any())
            verify(mockSignInGuard).ensureSignedIn()
        }

    @Test
    fun `getOrganizationAnalysis() should throw when use case throws`() =
        runTest {
            mockUseCase.stub {
                onBlocking {
                    execute(any())
                } doThrow SudoPrivacyInteractionClient.AnalysisResultException.FailedException()
            }

            val deferredResult =
                async(StandardTestDispatcher(testScheduler)) {
                    shouldThrow<SudoPrivacyInteractionClient.AnalysisResultException.FailedException> {
                        client.getOrganizationAnalysis(GetOrganizationAnalysisInput(domain = mockDomain))
                    }
                }
            deferredResult.start()
            deferredResult.await()

            verify(mockUseCaseFactory).createGetOrganizationAnalysisUseCase()
            verify(mockUseCase).execute(any())
            verify(mockSignInGuard).ensureSignedIn()
        }
}
