/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.apollographql.apollo.api.Optional
import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.internal.data.common.StringConstants
import com.sudoplatform.sudoprivacyinteraction.internal.data.common.transformers.ErrorTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence.transformers.RelationshipProviderTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence.transformers.VirtualPresenceTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ConnectVirtualPresenceInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ListVirtualPresencesInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ListVirtualPresencesOutput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.RescanVirtualPresenceInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService
import com.sudoplatform.sudoprivacyinteraction.graphql.type.AuthCodeInput as AuthCodeInputGraphQL
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ConnectVirtualPresenceInput as ConnectVirtualPresenceInputGraphQL
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ListVirtualPresencesInput as ListVirtualPresencesInputGraphQL
import com.sudoplatform.sudoprivacyinteraction.graphql.type.RefreshTokenInput as RefreshTokenInputGraphQL
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ScanOptionsInput as ScanOptionsInputGraphQL

/**
 * Implementation of [VirtualPresenceService] backed by the GraphQL API.
 *
 * @property apiClient [ApiClient] The GraphQL API client for executing mutations and queries.
 * @property logger [Logger] Logger for logging debug and error messages.
 */
internal class GraphQLVirtualPresenceService(
    private val apiClient: ApiClient,
    private val logger: Logger,
) : VirtualPresenceService {
    override suspend fun connect(input: ConnectVirtualPresenceInput): VirtualPresenceEntity {
        logger.debug("connect input: $input")

        val graphQLInput =
            ConnectVirtualPresenceInputGraphQL(
                authCode =
                    Optional.presentIfNotNull(
                        input.authCode?.let {
                            AuthCodeInputGraphQL(
                                authCode = it.authCode,
                                redirectUri = Optional.presentIfNotNull(it.redirectUri),
                            )
                        },
                    ),
                refreshToken =
                    Optional.presentIfNotNull(
                        input.refreshToken?.let {
                            RefreshTokenInputGraphQL(
                                refreshToken = it.refreshToken,
                                providerIdentity = it.providerIdentity,
                                scopes = Optional.presentIfNotNull(it.scopes),
                                expiresInEpochMs = Optional.presentIfNotNull(it.expiresInEpochMs),
                            )
                        },
                    ),
                relationshipProvider =
                    Optional.presentIfNotNull(
                        input.relationshipProvider?.let {
                            RelationshipProviderTransformer.fromEntityToGraphQL(it)
                        },
                    ),
            )

        try {
            val mutationResponse = apiClient.connectVirtualPresenceMutation(graphQLInput)

            if (mutationResponse.hasErrors()) {
                logger.error("errors = ${mutationResponse.errors}")
                throw ErrorTransformer.interpretVirtualPresenceError(mutationResponse.errors.first())
            }

            val result = mutationResponse.data?.connectVirtualPresence?.virtualPresence
            result?.let {
                return VirtualPresenceTransformer.fromGraphQLToEntity(it)
            }
            logger.error("no virtual presence returned")
            throw SudoPrivacyInteractionClient.VirtualPresenceException.FailedException(
                StringConstants.NO_VIRTUAL_PRESENCE_ERROR_MSG,
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

    override suspend fun disconnect(id: String): VirtualPresenceEntity {
        logger.debug("disconnect input: $id")

        try {
            val mutationResponse = apiClient.disconnectVirtualPresenceMutation(id)

            if (mutationResponse.hasErrors()) {
                logger.error("errors = ${mutationResponse.errors}")
                throw ErrorTransformer.interpretVirtualPresenceError(mutationResponse.errors.first())
            }

            val result = mutationResponse.data?.disconnectVirtualPresence?.virtualPresence
            result?.let {
                return VirtualPresenceTransformer.fromGraphQLToEntity(it)
            }
            logger.error("no virtual presence returned")
            throw SudoPrivacyInteractionClient.VirtualPresenceException.FailedException(
                StringConstants.NO_VIRTUAL_PRESENCE_ERROR_MSG,
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

    override suspend fun list(input: ListVirtualPresencesInput): ListVirtualPresencesOutput {
        logger.debug("list input: $input")

        try {
            val queryInput =
                ListVirtualPresencesInputGraphQL(
                    limit = Optional.presentIfNotNull(input.limit),
                    nextToken = Optional.presentIfNotNull(input.nextToken),
                )
            val queryResponse = apiClient.listVirtualPresencesQuery(queryInput)

            if (queryResponse.hasErrors()) {
                logger.error("errors = ${queryResponse.errors}")
                throw ErrorTransformer.interpretVirtualPresenceError(queryResponse.errors.first())
            }

            val queryResult = queryResponse.data?.listVirtualPresences
            val virtualPresences = queryResult?.items ?: emptyList()
            val newNextToken = queryResult?.nextToken

            return ListVirtualPresencesOutput(
                items =
                    virtualPresences.map {
                        VirtualPresenceTransformer.fromGraphQLToEntity(it.virtualPresence)
                    },
                nextToken = newNextToken,
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

    override suspend fun rescan(input: RescanVirtualPresenceInput): VirtualPresenceEntity {
        logger.debug("rescan input: $input")

        val graphQLOptions =
            input.options?.let {
                ScanOptionsInputGraphQL(
                    maximumItemsProcessed = Optional.presentIfNotNull(it.maximumItemsProcessed),
                    earliestScanDate = Optional.presentIfNotNull(it.earliestScanDate),
                    latestScanDate = Optional.presentIfNotNull(it.latestScanDate),
                    excludeDomains = Optional.presentIfNotNull(it.excludeDomains),
                    excludeCategories = Optional.presentIfNotNull(it.excludeCategories),
                )
            }

        try {
            val mutationResponse = apiClient.rescanVirtualPresenceMutation(input.id, graphQLOptions)

            if (mutationResponse.hasErrors()) {
                logger.error("errors = ${mutationResponse.errors}")
                throw ErrorTransformer.interpretVirtualPresenceError(mutationResponse.errors.first())
            }

            val result = mutationResponse.data?.rescanVirtualPresence?.virtualPresence
            result?.let {
                return VirtualPresenceTransformer.fromGraphQLToEntity(it)
            }
            logger.error("no virtual presence returned")
            throw SudoPrivacyInteractionClient.VirtualPresenceException.FailedException(
                StringConstants.NO_VIRTUAL_PRESENCE_ERROR_MSG,
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
