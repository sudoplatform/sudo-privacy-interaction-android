/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.providerConfiguration

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.amplifyframework.api.graphql.GraphQLResponse
import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.data.DataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ProviderTypeEntity
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
 * Test the correct operation of [GraphQLProviderConfigurationService].
 */
@RunWith(RobolectricTestRunner::class)
class GraphQLProviderConfigurationServiceTest : BaseTests() {
    override val mockApiClient by before {
        mock<ApiClient>()
    }

    private val instanceUnderTest by before {
        GraphQLProviderConfigurationService(
            mockApiClient,
            mockLogger,
        )
    }

    @After
    fun fini() {
        verifyNoMoreInteractions(mockApiClient)
    }

    @Test
    fun `get() should return decoded provider configurations when successful`() =
        runTest {
            val queryResponse =
                DataFactory.getProviderConfigurationQueryResponse(
                    DataFactory.encodedProviderConfiguration(
                        providers =
                            listOf(
                                Triple("default", "EMAIL", "client-default"),
                                Triple("google", "EMAIL", "client-google"),
                            ),
                    ),
                )
            mockApiClient.stub {
                onBlocking { getProviderConfigurationQuery() } doReturn queryResponse
            }

            val result = instanceUnderTest.get()

            result shouldNotBe null
            result.size shouldBe 2
            result[0].name shouldBe "default"
            result[0].providerType shouldBe ProviderTypeEntity.EMAIL
            result[0].clientId shouldBe "client-default"
            result[1].name shouldBe "google"

            verify(mockApiClient).getProviderConfigurationQuery()
        }

    @Test
    fun `get() should throw FailedException when result is null`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getProviderConfigurationQuery() } doReturn GraphQLResponse(null, null)
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.FailedException> {
                instanceUnderTest.get()
            }

            verify(mockApiClient).getProviderConfigurationQuery()
        }

    @Test
    fun `get() should throw when response has an error`() =
        runTest {
            val testError =
                GraphQLResponse.Error(
                    "Test generated error",
                    emptyList(),
                    emptyList(),
                    mapOf("errorType" to "sudoplatform.privacy-interaction.IdentityContextMissing"),
                )
            mockApiClient.stub {
                onBlocking { getProviderConfigurationQuery() } doReturn GraphQLResponse(null, listOf(testError))
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.IdentityContextMissingException> {
                instanceUnderTest.get()
            }

            verify(mockApiClient).getProviderConfigurationQuery()
        }

    @Test
    fun `get() should throw AuthenticationException when not authorized`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getProviderConfigurationQuery() } doThrow NotAuthorizedException("not authorized")
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.AuthenticationException> {
                instanceUnderTest.get()
            }

            verify(mockApiClient).getProviderConfigurationQuery()
        }

    @Test
    fun `get() should throw UnknownException for unexpected errors`() =
        runTest {
            mockApiClient.stub {
                onBlocking { getProviderConfigurationQuery() } doThrow RuntimeException("unexpected")
            }

            shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.UnknownException> {
                instanceUnderTest.get()
            }

            verify(mockApiClient).getProviderConfigurationQuery()
        }
}
