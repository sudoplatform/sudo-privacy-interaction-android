/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers.AnalysisResultTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.common.transformers.ErrorTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ListAnalysisResultsInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ListAnalysisResultsOutput

/**
 * Implementation of [AnalysisResultService] backed by the GraphQL API.
 *
 * @property apiClient [ApiClient] The GraphQL API client for executing queries.
 * @property logger [Logger] Logger for logging debug and error messages.
 */
internal class GraphQLAnalysisResultService(
    private val apiClient: ApiClient,
    private val logger: Logger,
) : AnalysisResultService {
    override suspend fun get(id: String): AnalysisResultEntity? {
        logger.debug("get input: $id")

        try {
            val queryResponse = apiClient.getAnalysisResultQuery(id)

            if (queryResponse.hasErrors()) {
                logger.error("errors = ${queryResponse.errors}")
                throw ErrorTransformer.interpretAnalysisResultError(queryResponse.errors.first())
            }

            val result = queryResponse.data?.getAnalysisResult?.analysisResult ?: return null
            return AnalysisResultTransformer.fromGraphQLToEntity(result)
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

    override suspend fun list(input: ListAnalysisResultsInput): ListAnalysisResultsOutput {
        logger.debug("list input: $input")

        try {
            val queryResponse =
                apiClient.listAnalysisResultsQuery(
                    virtualPresenceId = input.virtualPresenceId,
                    limit = input.limit,
                    nextToken = input.nextToken,
                )

            if (queryResponse.hasErrors()) {
                logger.error("errors = ${queryResponse.errors}")
                throw ErrorTransformer.interpretAnalysisResultError(queryResponse.errors.first())
            }

            val queryResult = queryResponse.data?.listAnalysisResults
            val analysisResults = queryResult?.items ?: emptyList()
            val newNextToken = queryResult?.nextToken

            return ListAnalysisResultsOutput(
                items = analysisResults.map { AnalysisResultTransformer.fromGraphQLToEntity(it.analysisResult) },
                nextToken = newNextToken,
            )
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
