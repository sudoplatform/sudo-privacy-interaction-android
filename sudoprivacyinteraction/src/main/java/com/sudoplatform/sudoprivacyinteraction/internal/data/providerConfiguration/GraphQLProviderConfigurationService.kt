/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.providerConfiguration

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.internal.data.common.StringConstants
import com.sudoplatform.sudoprivacyinteraction.internal.data.common.transformers.ErrorTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.providerConfiguration.transformers.ProviderConfigurationTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.configuration.ProviderConfigurationEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.configuration.ProviderConfigurationService

/**
 * Implementation of [ProviderConfigurationService] backed by the GraphQL API.
 *
 * @property apiClient [ApiClient] The GraphQL API client for executing queries.
 * @property logger [Logger] Logger for logging debug and error messages.
 */
internal class GraphQLProviderConfigurationService(
    private val apiClient: ApiClient,
    private val logger: Logger,
) : ProviderConfigurationService {
    override suspend fun get(): List<ProviderConfigurationEntity> {
        logger.debug("getProviderConfiguration")

        try {
            val queryResponse = apiClient.getProviderConfigurationQuery()

            if (queryResponse.hasErrors()) {
                logger.error("errors = ${queryResponse.errors}")
                throw ErrorTransformer.interpretVirtualPresenceError(queryResponse.errors.first())
            }

            val result = queryResponse.data?.getProviderConfiguration?.data
            result?.let {
                return ProviderConfigurationTransformer.fromGraphQLToEntity(it)
            }
            logger.error("no provider configuration returned")
            throw SudoPrivacyInteractionClient.VirtualPresenceException.FailedException(
                StringConstants.NO_PROVIDER_CONFIGURATION_ERROR_MSG,
            )
        } catch (e: Throwable) {
            logger.error("unexpected error $e")
            when (e) {
                is NotAuthorizedException ->
                    throw SudoPrivacyInteractionClient.VirtualPresenceException.AuthenticationException(
                        cause = e,
                    )
                else -> throw ErrorTransformer.interpretVirtualPresenceException(e)
            }
        }
    }
}
