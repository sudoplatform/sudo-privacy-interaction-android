/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.providerConfiguration.transformers

import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.data.DataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.ProviderTypeEntity
import com.sudoplatform.sudoprivacyinteraction.types.ProviderConfiguration
import com.sudoplatform.sudoprivacyinteraction.types.ProviderType
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Test the correct operation of [ProviderConfigurationTransformer].
 */
@RunWith(RobolectricTestRunner::class)
class ProviderConfigurationTransformerTest {
    @Test
    fun `fromGraphQLToEntity() decodes a single provider`() {
        val data =
            DataFactory.encodedProviderConfiguration(
                providers = listOf(Triple("google", "EMAIL", "client-123")),
            )

        val result = ProviderConfigurationTransformer.fromGraphQLToEntity(data)

        result.size shouldBe 1
        result[0].name shouldBe "google"
        result[0].providerType shouldBe ProviderTypeEntity.EMAIL
        result[0].clientId shouldBe "client-123"
    }

    @Test
    fun `fromGraphQLToEntity() decodes multiple providers`() {
        val data =
            DataFactory.encodedProviderConfiguration(
                providers =
                    listOf(
                        Triple("default", "EMAIL", "client-default"),
                        Triple("google", "EMAIL", "client-google"),
                    ),
            )

        val result = ProviderConfigurationTransformer.fromGraphQLToEntity(data)

        result.size shouldBe 2
        result[0].name shouldBe "default"
        result[1].name shouldBe "google"
    }

    @Test
    fun `fromGraphQLToEntity() maps unknown provider types to UNKNOWN`() {
        val data =
            DataFactory.encodedProviderConfiguration(
                providers = listOf(Triple("mystery", "SOME_FUTURE_TYPE", "client-x")),
            )

        val result = ProviderConfigurationTransformer.fromGraphQLToEntity(data)

        result[0].providerType shouldBe ProviderTypeEntity.UNKNOWN
    }

    @Test
    fun `fromGraphQLToEntity() throws FailedException on invalid base64`() {
        shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.FailedException> {
            ProviderConfigurationTransformer.fromGraphQLToEntity("!!!not-base64!!!")
        }
    }

    @Test
    fun `fromGraphQLToEntity() throws FailedException on invalid JSON`() {
        val notJson =
            android.util.Base64.encodeToString(
                "this is not json".toByteArray(Charsets.UTF_8),
                android.util.Base64.DEFAULT,
            )

        shouldThrow<SudoPrivacyInteractionClient.VirtualPresenceException.FailedException> {
            ProviderConfigurationTransformer.fromGraphQLToEntity(notJson)
        }
    }

    @Test
    fun `fromEntityToApi() maps entity to public type`() {
        val entity =
            com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory.getProviderConfigurationEntity(
                name = "google",
                providerType = ProviderTypeEntity.EMAIL,
                clientId = "client-abc",
            )

        val result: ProviderConfiguration = ProviderConfigurationTransformer.fromEntityToApi(entity)

        result.name shouldBe "google"
        result.providerType shouldBe ProviderType.EMAIL
        result.clientId shouldBe "client-abc"
    }
}
