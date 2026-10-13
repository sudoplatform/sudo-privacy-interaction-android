/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.client

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sudoplatform.sudoapiclient.ApiClientManager
import com.sudoplatform.sudoprivacyinteraction.BaseIntegrationTest
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import io.kotlintest.shouldThrow
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test the operation of [SudoPrivacyInteractionClient.builder].
 */
@RunWith(AndroidJUnit4::class)
class BuilderIntegrationTest : BaseIntegrationTest() {
    @Test
    fun shouldThrowIfRequiredItemsNotProvidedToBuilder() {
        // All required items not provided
        shouldThrow<NullPointerException> {
            SudoPrivacyInteractionClient.builder().build()
        }

        // Context not provided
        shouldThrow<NullPointerException> {
            SudoPrivacyInteractionClient
                .builder()
                .setSudoUserClient(userClient)
                .build()
        }

        // SudoUserClient not provided
        shouldThrow<NullPointerException> {
            SudoPrivacyInteractionClient
                .builder()
                .setContext(context)
                .build()
        }
    }

    @Test
    fun shouldNotThrowIfTheRequiredItemsAreProvidedToBuilder() {
        SudoPrivacyInteractionClient
            .builder()
            .setContext(context)
            .setSudoUserClient(userClient)
            .build()
    }

    @Test
    fun shouldNotThrowIfAllItemsAreProvidedToBuilder() {
        val apiClient = ApiClient(ApiClientManager.getClient(context, userClient), logger)

        SudoPrivacyInteractionClient
            .builder()
            .setContext(context)
            .setSudoUserClient(userClient)
            .setApiClient(apiClient)
            .setLogger(logger)
            .build()
    }
}
