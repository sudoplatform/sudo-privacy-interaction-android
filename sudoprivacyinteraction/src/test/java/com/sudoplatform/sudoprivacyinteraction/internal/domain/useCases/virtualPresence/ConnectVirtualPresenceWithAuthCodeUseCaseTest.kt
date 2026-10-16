/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence

import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.RelationshipProviderEntity
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
 * Test the correct operation of [ConnectVirtualPresenceWithAuthCodeUseCase]
 * using mocks and spies.
 */
@RunWith(RobolectricTestRunner::class)
class ConnectVirtualPresenceWithAuthCodeUseCaseTest : BaseTests() {
    private val mockVirtualPresenceService by before {
        mock<VirtualPresenceService>()
    }

    private val instanceUnderTest by before {
        ConnectVirtualPresenceWithAuthCodeUseCase(
            virtualPresenceService = mockVirtualPresenceService,
            logger = mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockVirtualPresenceService)
    }

    @Test
    fun `execute() should return the connected virtual presence when successful`() =
        runTest {
            val entity = EntityDataFactory.getVirtualPresenceEntity()
            mockVirtualPresenceService.stub {
                onBlocking { connect(any()) } doReturn entity
            }

            val result =
                instanceUnderTest.execute(
                    ConnectVirtualPresenceWithAuthCodeUseCaseInput(
                        authCode = mockAuthCode,
                        redirectUri = mockRedirectUri,
                        relationshipProvider = RelationshipProviderEntity.GMAIL_PROVIDER,
                    ),
                )

            result shouldNotBe null
            result shouldBe entity

            verify(mockVirtualPresenceService).connect(
                check { input ->
                    input.authCode shouldNotBe null
                    input.authCode!!.authCode shouldBe mockAuthCode
                    input.authCode.redirectUri shouldBe mockRedirectUri
                    input.refreshToken shouldBe null
                    input.relationshipProvider shouldBe RelationshipProviderEntity.GMAIL_PROVIDER
                },
            )
        }

    @Test
    fun `execute() should pass through null optional fields`() =
        runTest {
            val entity = EntityDataFactory.getVirtualPresenceEntity()
            mockVirtualPresenceService.stub {
                onBlocking { connect(any()) } doReturn entity
            }

            instanceUnderTest.execute(
                ConnectVirtualPresenceWithAuthCodeUseCaseInput(authCode = mockAuthCode),
            )

            verify(mockVirtualPresenceService).connect(
                check { input ->
                    input.authCode!!.authCode shouldBe mockAuthCode
                    input.authCode.redirectUri shouldBe null
                    input.relationshipProvider shouldBe null
                },
            )
        }

    @Test
    fun `execute() should propagate service exceptions`() =
        runTest {
            mockVirtualPresenceService.stub {
                onBlocking {
                    connect(any())
                } doThrow SudoPrivacyInteractionClient.VirtualPresenceException.InvalidAuthCodeException()
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.InvalidAuthCodeException> {
                instanceUnderTest.execute(
                    ConnectVirtualPresenceWithAuthCodeUseCaseInput(authCode = mockAuthCode),
                )
            }

            verify(mockVirtualPresenceService).connect(any())
        }
}
