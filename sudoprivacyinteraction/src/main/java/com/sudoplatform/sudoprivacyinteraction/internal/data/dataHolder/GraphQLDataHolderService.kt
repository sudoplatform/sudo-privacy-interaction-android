/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.dataHolder

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.internal.data.common.transformers.ErrorTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.dataHolder.transformers.DataHolderScanSummaryTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.dataHolder.transformers.DataHolderTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.ListDataHolderScanSummariesInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.ListDataHolderScanSummariesOutput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.ListDataHoldersInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.ListDataHoldersOutput

/**
 * Implementation of [DataHolderService] backed by the GraphQL API.
 *
 * @property apiClient [ApiClient] The GraphQL API client for executing queries.
 * @property logger [Logger] Logger for logging debug and error messages.
 */
internal class GraphQLDataHolderService(
    private val apiClient: ApiClient,
    private val logger: Logger,
) : DataHolderService {
    override suspend fun get(id: String): DataHolderEntity? {
        logger.debug("get input: $id")

        try {
            val queryResponse = apiClient.getDataHolderQuery(id)

            if (queryResponse.hasErrors()) {
                logger.error("errors = ${queryResponse.errors}")
                throw ErrorTransformer.interpretDataHolderError(queryResponse.errors.first())
            }

            val result = queryResponse.data?.getDataHolder?.dataHolder ?: return null
            return DataHolderTransformer.fromGraphQLToEntity(result)
        } catch (e: Throwable) {
            logger.error("unexpected error $e")
            when (e) {
                is NotAuthorizedException ->
                    throw SudoPrivacyInteractionClient.DataHolderException.AuthenticationException(
                        cause = e,
                    )
                else -> throw ErrorTransformer.interpretDataHolderException(e)
            }
        }
    }

    override suspend fun list(input: ListDataHoldersInput): ListDataHoldersOutput {
        logger.debug("list input: $input")

        try {
            val queryResponse =
                apiClient.listDataHoldersQuery(
                    virtualPresenceId = input.virtualPresenceId,
                    limit = input.limit,
                    nextToken = input.nextToken,
                )

            if (queryResponse.hasErrors()) {
                logger.error("errors = ${queryResponse.errors}")
                throw ErrorTransformer.interpretDataHolderError(queryResponse.errors.first())
            }

            val queryResult = queryResponse.data?.listDataHolders
            val dataHolders = queryResult?.items ?: emptyList()
            val newNextToken = queryResult?.nextToken

            return ListDataHoldersOutput(
                items = dataHolders.map { DataHolderTransformer.fromGraphQLToEntity(it.dataHolder) },
                nextToken = newNextToken,
            )
        } catch (e: Throwable) {
            logger.error("unexpected error $e")
            when (e) {
                is NotAuthorizedException ->
                    throw SudoPrivacyInteractionClient.DataHolderException.AuthenticationException(
                        cause = e,
                    )
                else -> throw ErrorTransformer.interpretDataHolderException(e)
            }
        }
    }

    override suspend fun listScanSummaries(input: ListDataHolderScanSummariesInput): ListDataHolderScanSummariesOutput {
        logger.debug("listScanSummaries input: $input")

        try {
            val queryResponse =
                apiClient.listDataHolderScanSummariesQuery(
                    dataHolderId = input.dataHolderId,
                    limit = input.limit,
                    nextToken = input.nextToken,
                )

            if (queryResponse.hasErrors()) {
                logger.error("errors = ${queryResponse.errors}")
                throw ErrorTransformer.interpretDataHolderError(queryResponse.errors.first())
            }

            val queryResult = queryResponse.data?.listDataHolderScanSummaries
            val scanSummaries = queryResult?.items ?: emptyList()
            val newNextToken = queryResult?.nextToken

            return ListDataHolderScanSummariesOutput(
                items =
                    scanSummaries.map {
                        DataHolderScanSummaryTransformer.fromGraphQLToEntity(it.dataHolderScanSummary)
                    },
                nextToken = newNextToken,
            )
        } catch (e: Throwable) {
            logger.error("unexpected error $e")
            when (e) {
                is NotAuthorizedException ->
                    throw SudoPrivacyInteractionClient.DataHolderException.AuthenticationException(
                        cause = e,
                    )
                else -> throw ErrorTransformer.interpretDataHolderException(e)
            }
        }
    }
}
