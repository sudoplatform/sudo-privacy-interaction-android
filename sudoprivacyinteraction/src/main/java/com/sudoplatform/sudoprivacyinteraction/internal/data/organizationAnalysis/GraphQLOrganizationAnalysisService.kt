/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.organizationAnalysis

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.apollographql.apollo.api.Optional
import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.graphql.type.OrganizationAnalysisOptions
import com.sudoplatform.sudoprivacyinteraction.internal.data.common.transformers.ErrorTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.organizationAnalysis.transformers.OrganizationAnalysisDomainResolutionTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.organizationAnalysis.transformers.OrganizationAnalysisModeTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.organizationAnalysis.transformers.OrganizationAnalysisTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.GetOrganizationAnalysisInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisService

/**
 * Implementation of [OrganizationAnalysisService] backed by the GraphQL API.
 *
 * @property apiClient [ApiClient] The GraphQL API client for executing queries.
 * @property logger [Logger] Logger for logging debug and error messages.
 */
internal class GraphQLOrganizationAnalysisService(
    private val apiClient: ApiClient,
    private val logger: Logger,
) : OrganizationAnalysisService {
    override suspend fun getOrganizationAnalysis(input: GetOrganizationAnalysisInput): OrganizationAnalysisEntity? {
        logger.debug("getOrganizationAnalysis input: $input")

        val mode = input.mode?.let { OrganizationAnalysisModeTransformer.fromEntityToGraphQL(it) }
        val resolution =
            input.resolution?.let {
                OrganizationAnalysisDomainResolutionTransformer.fromEntityToGraphQL(it)
            }

        // Only send `options` when at least one of its fields is set, so the service applies its
        // own defaults otherwise.
        val options =
            if (mode != null || resolution != null) {
                OrganizationAnalysisOptions(
                    mode = Optional.presentIfNotNull(mode),
                    resolution = Optional.presentIfNotNull(resolution),
                )
            } else {
                null
            }

        try {
            val queryResponse = apiClient.getOrganizationAnalysisQuery(input.domain, input.name, options)

            if (queryResponse.hasErrors()) {
                logger.error("errors = ${queryResponse.errors}")
                throw ErrorTransformer.interpretAnalysisResultError(queryResponse.errors.first())
            }

            val result = queryResponse.data?.getOrganizationAnalysis?.organizationAnalysis ?: return null
            return OrganizationAnalysisTransformer.fromGraphQLToEntity(result)
        } catch (e: Throwable) {
            logger.error("unexpected error $e")
            when (e) {
                is NotAuthorizedException ->
                    throw SudoPrivacyInteractionClient.AnalysisResultException.AuthenticationException(
                        cause = e,
                    )
                else -> throw ErrorTransformer.interpretAnalysisResultException(e)
            }
        }
    }
}
