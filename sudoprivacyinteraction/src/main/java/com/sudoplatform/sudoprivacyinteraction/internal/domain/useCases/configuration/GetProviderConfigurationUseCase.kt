/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.configuration

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.configuration.ProviderConfigurationEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.configuration.ProviderConfigurationService

/**
 * Application business logic for retrieving the provider configuration.
 */
internal class GetProviderConfigurationUseCase(
    private val providerConfigurationService: ProviderConfigurationService,
    private val logger: Logger,
) {
    suspend fun execute(): List<ProviderConfigurationEntity> {
        logger.debug(this::class.java.simpleName)
        return providerConfigurationService.get()
    }
}
