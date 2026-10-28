/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.api

import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.GraphQLOperation
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.core.Action
import com.amplifyframework.core.Consumer
import com.apollographql.apollo.api.Optional
import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.graphql.ArchiveDataHolderMutation
import com.sudoplatform.sudoprivacyinteraction.graphql.ConfirmActionOutcomeMutation
import com.sudoplatform.sudoprivacyinteraction.graphql.ConnectVirtualPresenceMutation
import com.sudoplatform.sudoprivacyinteraction.graphql.DisconnectVirtualPresenceMutation
import com.sudoplatform.sudoprivacyinteraction.graphql.GetActionQuery
import com.sudoplatform.sudoprivacyinteraction.graphql.GetAnalysisResultQuery
import com.sudoplatform.sudoprivacyinteraction.graphql.GetDataHolderQuery
import com.sudoplatform.sudoprivacyinteraction.graphql.GetOrganizationAnalysisQuery
import com.sudoplatform.sudoprivacyinteraction.graphql.GetProviderConfigurationQuery
import com.sudoplatform.sudoprivacyinteraction.graphql.InitiateActionMutation
import com.sudoplatform.sudoprivacyinteraction.graphql.ListAnalysisResultsQuery
import com.sudoplatform.sudoprivacyinteraction.graphql.ListAvailableActionsQuery
import com.sudoplatform.sudoprivacyinteraction.graphql.ListDataHolderScanSummariesQuery
import com.sudoplatform.sudoprivacyinteraction.graphql.ListDataHoldersQuery
import com.sudoplatform.sudoprivacyinteraction.graphql.ListVirtualPresencesQuery
import com.sudoplatform.sudoprivacyinteraction.graphql.OnActionStatusUpdateSubscription
import com.sudoplatform.sudoprivacyinteraction.graphql.OnAnalysisResultUpdateSubscription
import com.sudoplatform.sudoprivacyinteraction.graphql.OnDataHoldersUpdateSubscription
import com.sudoplatform.sudoprivacyinteraction.graphql.OnVirtualPresenceUpdateSubscription
import com.sudoplatform.sudoprivacyinteraction.graphql.RescanVirtualPresenceMutation
import com.sudoplatform.sudoprivacyinteraction.graphql.UnarchiveDataHolderMutation
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ConnectVirtualPresenceInput
import com.sudoplatform.sudoprivacyinteraction.graphql.type.InitiateActionInput
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ListVirtualPresencesInput
import com.sudoplatform.sudoprivacyinteraction.graphql.type.OrganizationAnalysisOptions
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ScanOptionsInput
import com.sudoplatform.sudouser.amplify.GraphQLClient

/**
 * Wrapper around the [GraphQLClient] and its generated queries and mutations.
 * Provides a testable class to handle API requests.
 *
 * @property graphQLClient [GraphQLClient] The GraphQLClient that will be used to make the requests.
 * @property logger [Logger] The logger used to log errors.
 */
open class ApiClient(
    private val graphQLClient: GraphQLClient,
    private val logger: Logger,
) {
    // Queries
    open suspend fun getProviderConfigurationQuery(): GraphQLResponse<GetProviderConfigurationQuery.Data> =
        this.graphQLClient.query<GetProviderConfigurationQuery, GetProviderConfigurationQuery.Data>(
            GetProviderConfigurationQuery.OPERATION_DOCUMENT,
            emptyMap(),
        )

    open suspend fun listVirtualPresencesQuery(input: ListVirtualPresencesInput): GraphQLResponse<ListVirtualPresencesQuery.Data> =
        this.graphQLClient.query<ListVirtualPresencesQuery, ListVirtualPresencesQuery.Data>(
            ListVirtualPresencesQuery.OPERATION_DOCUMENT,
            mapOf("input" to Optional.presentIfNotNull(input)),
        )

    open suspend fun getDataHolderQuery(id: String): GraphQLResponse<GetDataHolderQuery.Data> =
        this.graphQLClient.query<GetDataHolderQuery, GetDataHolderQuery.Data>(
            GetDataHolderQuery.OPERATION_DOCUMENT,
            mapOf("id" to id),
        )

    open suspend fun listDataHoldersQuery(
        virtualPresenceId: String,
        limit: Int?,
        nextToken: String?,
    ): GraphQLResponse<ListDataHoldersQuery.Data> =
        this.graphQLClient.query<ListDataHoldersQuery, ListDataHoldersQuery.Data>(
            ListDataHoldersQuery.OPERATION_DOCUMENT,
            mapOf(
                "virtualPresenceId" to virtualPresenceId,
                "limit" to Optional.presentIfNotNull(limit),
                "nextToken" to Optional.presentIfNotNull(nextToken),
            ),
        )

    open suspend fun listDataHolderScanSummariesQuery(
        dataHolderId: String,
        limit: Int?,
        nextToken: String?,
    ): GraphQLResponse<ListDataHolderScanSummariesQuery.Data> =
        this.graphQLClient.query<ListDataHolderScanSummariesQuery, ListDataHolderScanSummariesQuery.Data>(
            ListDataHolderScanSummariesQuery.OPERATION_DOCUMENT,
            mapOf(
                "dataHolderId" to dataHolderId,
                "limit" to Optional.presentIfNotNull(limit),
                "nextToken" to Optional.presentIfNotNull(nextToken),
            ),
        )

    open suspend fun getAnalysisResultQuery(id: String): GraphQLResponse<GetAnalysisResultQuery.Data> =
        this.graphQLClient.query<GetAnalysisResultQuery, GetAnalysisResultQuery.Data>(
            GetAnalysisResultQuery.OPERATION_DOCUMENT,
            mapOf("id" to id),
        )

    open suspend fun listAnalysisResultsQuery(
        virtualPresenceId: String,
        limit: Int?,
        nextToken: String?,
    ): GraphQLResponse<ListAnalysisResultsQuery.Data> =
        this.graphQLClient.query<ListAnalysisResultsQuery, ListAnalysisResultsQuery.Data>(
            ListAnalysisResultsQuery.OPERATION_DOCUMENT,
            mapOf(
                "virtualPresenceId" to virtualPresenceId,
                "limit" to Optional.presentIfNotNull(limit),
                "nextToken" to Optional.presentIfNotNull(nextToken),
            ),
        )

    open suspend fun getOrganizationAnalysisQuery(
        domain: String,
        name: String?,
        options: OrganizationAnalysisOptions?,
    ): GraphQLResponse<GetOrganizationAnalysisQuery.Data> =
        this.graphQLClient.query<GetOrganizationAnalysisQuery, GetOrganizationAnalysisQuery.Data>(
            GetOrganizationAnalysisQuery.OPERATION_DOCUMENT,
            mapOf(
                "domain" to domain,
                "name" to Optional.presentIfNotNull(name),
                "options" to Optional.presentIfNotNull(options),
            ),
        )

    open suspend fun listAvailableActionsQuery(dataHolderId: String): GraphQLResponse<ListAvailableActionsQuery.Data> =
        this.graphQLClient.query<ListAvailableActionsQuery, ListAvailableActionsQuery.Data>(
            ListAvailableActionsQuery.OPERATION_DOCUMENT,
            mapOf("dataHolderId" to dataHolderId),
        )

    open suspend fun getActionQuery(id: String): GraphQLResponse<GetActionQuery.Data> =
        this.graphQLClient.query<GetActionQuery, GetActionQuery.Data>(
            GetActionQuery.OPERATION_DOCUMENT,
            mapOf("id" to id),
        )

    // Mutations
    open suspend fun connectVirtualPresenceMutation(
        input: ConnectVirtualPresenceInput,
    ): GraphQLResponse<ConnectVirtualPresenceMutation.Data> =
        this.graphQLClient.mutate<ConnectVirtualPresenceMutation, ConnectVirtualPresenceMutation.Data>(
            ConnectVirtualPresenceMutation.OPERATION_DOCUMENT,
            mapOf("input" to Optional.presentIfNotNull(input)),
        )

    open suspend fun disconnectVirtualPresenceMutation(id: String): GraphQLResponse<DisconnectVirtualPresenceMutation.Data> =
        this.graphQLClient.mutate<DisconnectVirtualPresenceMutation, DisconnectVirtualPresenceMutation.Data>(
            DisconnectVirtualPresenceMutation.OPERATION_DOCUMENT,
            mapOf("id" to id),
        )

    open suspend fun rescanVirtualPresenceMutation(
        id: String,
        options: ScanOptionsInput?,
    ): GraphQLResponse<RescanVirtualPresenceMutation.Data> =
        this.graphQLClient.mutate<RescanVirtualPresenceMutation, RescanVirtualPresenceMutation.Data>(
            RescanVirtualPresenceMutation.OPERATION_DOCUMENT,
            mapOf(
                "id" to id,
                "options" to Optional.presentIfNotNull(options),
            ),
        )

    open suspend fun archiveDataHolderMutation(id: String): GraphQLResponse<ArchiveDataHolderMutation.Data> =
        this.graphQLClient.mutate<ArchiveDataHolderMutation, ArchiveDataHolderMutation.Data>(
            ArchiveDataHolderMutation.OPERATION_DOCUMENT,
            mapOf("id" to id),
        )

    open suspend fun unarchiveDataHolderMutation(id: String): GraphQLResponse<UnarchiveDataHolderMutation.Data> =
        this.graphQLClient.mutate<UnarchiveDataHolderMutation, UnarchiveDataHolderMutation.Data>(
            UnarchiveDataHolderMutation.OPERATION_DOCUMENT,
            mapOf("id" to id),
        )

    open suspend fun initiateActionMutation(input: InitiateActionInput): GraphQLResponse<InitiateActionMutation.Data> =
        this.graphQLClient.mutate<InitiateActionMutation, InitiateActionMutation.Data>(
            InitiateActionMutation.OPERATION_DOCUMENT,
            mapOf("input" to input),
        )

    open suspend fun confirmActionOutcomeMutation(id: String): GraphQLResponse<ConfirmActionOutcomeMutation.Data> =
        this.graphQLClient.mutate<ConfirmActionOutcomeMutation, ConfirmActionOutcomeMutation.Data>(
            ConfirmActionOutcomeMutation.OPERATION_DOCUMENT,
            mapOf("id" to id),
        )

    // Subscriptions
    open suspend fun onVirtualPresenceUpdateSubscription(
        owner: String,
        onSubscriptionEstablished: Consumer<GraphQLResponse<OnVirtualPresenceUpdateSubscription.Data>>,
        onSubscription: Consumer<GraphQLResponse<OnVirtualPresenceUpdateSubscription.Data>>,
        onSubscriptionCompleted: Action,
        onFailure: Consumer<ApiException>,
    ): GraphQLOperation<OnVirtualPresenceUpdateSubscription.Data>? =
        this.graphQLClient.subscribe<OnVirtualPresenceUpdateSubscription, OnVirtualPresenceUpdateSubscription.Data>(
            OnVirtualPresenceUpdateSubscription.OPERATION_DOCUMENT,
            mapOf("owner" to owner),
            onSubscriptionEstablished,
            onSubscription,
            onSubscriptionCompleted,
            onFailure,
        )

    open suspend fun onDataHoldersUpdateSubscription(
        owner: String,
        onSubscriptionEstablished: Consumer<GraphQLResponse<OnDataHoldersUpdateSubscription.Data>>,
        onSubscription: Consumer<GraphQLResponse<OnDataHoldersUpdateSubscription.Data>>,
        onSubscriptionCompleted: Action,
        onFailure: Consumer<ApiException>,
    ): GraphQLOperation<OnDataHoldersUpdateSubscription.Data>? =
        this.graphQLClient.subscribe<OnDataHoldersUpdateSubscription, OnDataHoldersUpdateSubscription.Data>(
            OnDataHoldersUpdateSubscription.OPERATION_DOCUMENT,
            mapOf("owner" to owner),
            onSubscriptionEstablished,
            onSubscription,
            onSubscriptionCompleted,
            onFailure,
        )

    open suspend fun onAnalysisResultUpdateSubscription(
        owner: String,
        onSubscriptionEstablished: Consumer<GraphQLResponse<OnAnalysisResultUpdateSubscription.Data>>,
        onSubscription: Consumer<GraphQLResponse<OnAnalysisResultUpdateSubscription.Data>>,
        onSubscriptionCompleted: Action,
        onFailure: Consumer<ApiException>,
    ): GraphQLOperation<OnAnalysisResultUpdateSubscription.Data>? =
        this.graphQLClient.subscribe<OnAnalysisResultUpdateSubscription, OnAnalysisResultUpdateSubscription.Data>(
            OnAnalysisResultUpdateSubscription.OPERATION_DOCUMENT,
            mapOf("owner" to owner),
            onSubscriptionEstablished,
            onSubscription,
            onSubscriptionCompleted,
            onFailure,
        )

    open suspend fun onActionStatusUpdateSubscription(
        owner: String,
        onSubscriptionEstablished: Consumer<GraphQLResponse<OnActionStatusUpdateSubscription.Data>>,
        onSubscription: Consumer<GraphQLResponse<OnActionStatusUpdateSubscription.Data>>,
        onSubscriptionCompleted: Action,
        onFailure: Consumer<ApiException>,
    ): GraphQLOperation<OnActionStatusUpdateSubscription.Data>? =
        this.graphQLClient.subscribe<OnActionStatusUpdateSubscription, OnActionStatusUpdateSubscription.Data>(
            OnActionStatusUpdateSubscription.OPERATION_DOCUMENT,
            mapOf("owner" to owner),
            onSubscriptionEstablished,
            onSubscription,
            onSubscriptionCompleted,
            onFailure,
        )
}
