/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.action

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers.ActionFulfilmentMethodTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers.ActionIntentTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers.ActionTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers.AvailableActionTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.common.StringConstants
import com.sudoplatform.sudoprivacyinteraction.internal.data.common.transformers.ErrorTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.InitiateActionInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ListAvailableActionsInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ListAvailableActionsOutput
import com.sudoplatform.sudoprivacyinteraction.graphql.type.InitiateActionInput as InitiateActionInputGraphQL

/**
 * Implementation of [ActionService] backed by the GraphQL API.
 *
 * @property apiClient [ApiClient] The GraphQL API client for executing queries and mutations.
 * @property logger [Logger] Logger for logging debug and error messages.
 */
internal class GraphQLActionService(
    private val apiClient: ApiClient,
    private val logger: Logger,
) : ActionService {
    override suspend fun initiate(input: InitiateActionInput): ActionEntity {
        logger.debug("initiate input: $input")

        val graphQLInput =
            InitiateActionInputGraphQL(
                dataHolderId = input.dataHolderId,
                fulfilmentMethod = ActionFulfilmentMethodTransformer.fromEntityToGraphQL(input.fulfilmentMethod),
                intent = ActionIntentTransformer.fromEntityToGraphQL(input.intent),
            )

        try {
            val mutationResponse = apiClient.initiateActionMutation(graphQLInput)

            if (mutationResponse.hasErrors()) {
                logger.error("errors = ${mutationResponse.errors}")
                throw ErrorTransformer.interpretActionError(mutationResponse.errors.first())
            }

            val result = mutationResponse.data?.initiateAction?.action
            result?.let {
                return ActionTransformer.fromGraphQLToEntity(it)
            }
            logger.error("no action returned")
            throw SudoPrivacyInteractionClient.ActionException.FailedException(StringConstants.NO_ACTION_ERROR_MSG)
        } catch (e: Throwable) {
            logger.error("unexpected error $e")
            when (e) {
                is NotAuthorizedException ->
                    throw SudoPrivacyInteractionClient.ActionException.AuthenticationException(cause = e)
                else -> throw ErrorTransformer.interpretActionException(e)
            }
        }
    }

    override suspend fun confirmOutcome(id: String): ActionEntity {
        logger.debug("confirmOutcome input: $id")

        try {
            val mutationResponse = apiClient.confirmActionOutcomeMutation(id)

            if (mutationResponse.hasErrors()) {
                logger.error("errors = ${mutationResponse.errors}")
                throw ErrorTransformer.interpretActionError(mutationResponse.errors.first())
            }

            val result = mutationResponse.data?.confirmActionOutcome?.action
            result?.let {
                return ActionTransformer.fromGraphQLToEntity(it)
            }
            logger.error("no action returned")
            throw SudoPrivacyInteractionClient.ActionException.FailedException(StringConstants.NO_ACTION_ERROR_MSG)
        } catch (e: Throwable) {
            logger.error("unexpected error $e")
            when (e) {
                is NotAuthorizedException ->
                    throw SudoPrivacyInteractionClient.ActionException.AuthenticationException(cause = e)
                else -> throw ErrorTransformer.interpretActionException(e)
            }
        }
    }

    override suspend fun listAvailable(input: ListAvailableActionsInput): ListAvailableActionsOutput {
        logger.debug("listAvailable input: $input")

        try {
            val queryResponse = apiClient.listAvailableActionsQuery(input.dataHolderId)

            if (queryResponse.hasErrors()) {
                logger.error("errors = ${queryResponse.errors}")
                throw ErrorTransformer.interpretActionError(queryResponse.errors.first())
            }

            val queryResult = queryResponse.data?.listAvailableActions
            val availableActions = queryResult?.items ?: emptyList()
            val newNextToken = queryResult?.nextToken

            return ListAvailableActionsOutput(
                items = availableActions.map { AvailableActionTransformer.fromGraphQLToEntity(it.availableAction) },
                nextToken = newNextToken,
            )
        } catch (e: Throwable) {
            logger.error("unexpected error $e")
            when (e) {
                is NotAuthorizedException ->
                    throw SudoPrivacyInteractionClient.ActionException.AuthenticationException(cause = e)
                else -> throw ErrorTransformer.interpretActionException(e)
            }
        }
    }
}
