/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.configuration

/**
 * Core entity representation of a configuration service used in business logic. Used to retrieve
 * service configuration.
 */
internal interface ProviderConfigurationService {
    /**
     * Retrieve the provider configurations required by consumers to support the provider OAuth
     * flow.
     *
     * @return The decoded list of [ProviderConfigurationEntity].
     */
    suspend fun get(): List<ProviderConfigurationEntity>
}
