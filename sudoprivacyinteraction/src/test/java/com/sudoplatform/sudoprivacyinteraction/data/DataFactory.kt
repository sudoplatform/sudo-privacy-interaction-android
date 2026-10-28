/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.data

import android.util.Base64.encodeToString
import com.amplifyframework.api.graphql.GraphQLResponse
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
import com.sudoplatform.sudoprivacyinteraction.graphql.RescanVirtualPresenceMutation
import com.sudoplatform.sudoprivacyinteraction.graphql.UnarchiveDataHolderMutation
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.Action
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.AnalysisResult
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.AnalysisResultData
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.AvailableAction
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.DataHolder
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.DataHolderScanSummary
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.OrganizationAnalysis
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.VirtualPresence
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ActionFulfilmentMethod
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ActionIntent
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ActionOrigin
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ActionStatus
import com.sudoplatform.sudoprivacyinteraction.graphql.type.AnalysisResultStatus
import com.sudoplatform.sudoprivacyinteraction.graphql.type.DataCategory
import com.sudoplatform.sudoprivacyinteraction.graphql.type.DataHolderProtectionState
import com.sudoplatform.sudoprivacyinteraction.graphql.type.OrganizationCategory
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ProviderType
import com.sudoplatform.sudoprivacyinteraction.graphql.type.RelationshipProvider
import com.sudoplatform.sudoprivacyinteraction.graphql.type.RetentionStyle
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ShareStyle
import com.sudoplatform.sudoprivacyinteraction.graphql.type.SignalValue
import com.sudoplatform.sudoprivacyinteraction.graphql.type.VirtualPresenceState

/**
 * Factory for building GraphQL layer test fixtures.
 */
object DataFactory {
    fun getVirtualPresenceFragment(
        id: String = "mockVirtualPresenceId",
        owner: String = "mockOwner",
        version: Int = 1,
        providerType: ProviderType = ProviderType.EMAIL,
        identifier: String = "test@sudoplatform.com",
        state: VirtualPresenceState = VirtualPresenceState.CONNECTED,
        relationshipProvider: RelationshipProvider? = null,
        lastScannedAtEpochMs: Double = 1.0,
        dataHolderCount: VirtualPresence.DataHolderCount =
            VirtualPresence.DataHolderCount(
                count = 6,
                monitored = 1,
                initiatedActions = 2,
                completedActions = 3,
                archived = 4,
            ),
        createdAtEpochMs: Double = 1.0,
        updatedAtEpochMs: Double = 1.0,
    ): VirtualPresence =
        VirtualPresence(
            id = id,
            providerType = providerType,
            identifier = identifier,
            state = state,
            relationshipProvider = relationshipProvider,
            lastScannedAtEpochMs = lastScannedAtEpochMs,
            dataHolderCount = dataHolderCount,
            owner = owner,
            version = version,
            createdAtEpochMs = createdAtEpochMs,
            updatedAtEpochMs = updatedAtEpochMs,
        )

    fun connectVirtualPresenceMutationResponse(
        virtualPresence: VirtualPresence = getVirtualPresenceFragment(),
    ): GraphQLResponse<ConnectVirtualPresenceMutation.Data> =
        GraphQLResponse(
            ConnectVirtualPresenceMutation.Data(
                ConnectVirtualPresenceMutation.ConnectVirtualPresence(
                    "VirtualPresence",
                    virtualPresence,
                ),
            ),
            null,
        )

    fun disconnectVirtualPresenceMutationResponse(
        virtualPresence: VirtualPresence = getVirtualPresenceFragment(),
    ): GraphQLResponse<DisconnectVirtualPresenceMutation.Data> =
        GraphQLResponse(
            DisconnectVirtualPresenceMutation.Data(
                DisconnectVirtualPresenceMutation.DisconnectVirtualPresence(
                    "VirtualPresence",
                    virtualPresence,
                ),
            ),
            null,
        )

    /**
     * Builds the base-64 encoded, JSON provider configuration payload as returned by the
     * service in the opaque `data` field.
     */
    fun encodedProviderConfiguration(
        providers: List<Triple<String, String, String>> =
            listOf(Triple("google", "EMAIL", "mockClientId")),
    ): String {
        val providersJson =
            providers.joinToString(",") { (name, providerType, clientId) ->
                """{"name":"$name","providerType":"$providerType","clientId":"$clientId"}"""
            }
        val json = """{"providers":[$providersJson]}"""
        return encodeToString(json.toByteArray(Charsets.UTF_8), android.util.Base64.DEFAULT)
    }

    fun getProviderConfigurationQueryResponse(
        data: String = encodedProviderConfiguration(),
    ): GraphQLResponse<GetProviderConfigurationQuery.Data> =
        GraphQLResponse(
            GetProviderConfigurationQuery.Data(
                GetProviderConfigurationQuery.GetProviderConfiguration(data),
            ),
            null,
        )

    fun listVirtualPresencesQueryResponse(
        virtualPresences: List<VirtualPresence> = listOf(getVirtualPresenceFragment()),
        nextToken: String? = null,
    ): GraphQLResponse<ListVirtualPresencesQuery.Data> =
        GraphQLResponse(
            ListVirtualPresencesQuery.Data(
                ListVirtualPresencesQuery.ListVirtualPresences(
                    items =
                        virtualPresences.map {
                            ListVirtualPresencesQuery.Item("VirtualPresence", it)
                        },
                    nextToken = nextToken,
                ),
            ),
            null,
        )

    fun rescanVirtualPresenceMutationResponse(
        virtualPresence: VirtualPresence = getVirtualPresenceFragment(),
    ): GraphQLResponse<RescanVirtualPresenceMutation.Data> =
        GraphQLResponse(
            RescanVirtualPresenceMutation.Data(
                RescanVirtualPresenceMutation.RescanVirtualPresence(
                    "VirtualPresence",
                    virtualPresence,
                ),
            ),
            null,
        )

    fun getDataHolderFragment(
        id: String = "mockDataHolderId",
        owner: String = "mockOwner",
        virtualPresenceId: String = "mockVirtualPresenceId",
        domainName: String = "google.com",
        name: String = "Google",
        protectionState: DataHolderProtectionState = DataHolderProtectionState.MONITORED,
        complianceConcern: Boolean = false,
        mostRecentInteractionEpochMs: Double = 1.0,
        version: Int = 1,
        createdAtEpochMs: Double = 1.0,
        updatedAtEpochMs: Double = 1.0,
    ): DataHolder =
        DataHolder(
            id = id,
            virtualPresenceId = virtualPresenceId,
            domainName = domainName,
            name = name,
            protectionState = protectionState,
            complianceConcern = complianceConcern,
            mostRecentInteractionEpochMs = mostRecentInteractionEpochMs,
            owner = owner,
            version = version,
            createdAtEpochMs = createdAtEpochMs,
            updatedAtEpochMs = updatedAtEpochMs,
        )

    fun archiveDataHolderMutationResponse(
        dataHolder: DataHolder =
            getDataHolderFragment(protectionState = DataHolderProtectionState.ARCHIVED),
    ): GraphQLResponse<ArchiveDataHolderMutation.Data> =
        GraphQLResponse(
            ArchiveDataHolderMutation.Data(
                ArchiveDataHolderMutation.ArchiveDataHolder("DataHolder", dataHolder),
            ),
            null,
        )

    fun getDataHolderScanSummaryFragment(
        dataHolderId: String = "mockDataHolderId",
        owner: String = "mockOwner",
        scannedAtEpochMs: Double = 1.0,
        scanRangeFromEpochMs: Double = 1.0,
        scanRangeToEpochMs: Double = 2.0,
        emailCount: Int = 10,
        readCount: Int = 5,
        readRate: Double = 0.5,
        marketingEmailCount: Int = 4,
        marketingEmailOpened: Int = 1,
        marketingOpenRate: Double = 0.25,
        categoryBreakdown: Any = """{"promotions":3,"updates":2}""",
        uncategorizedCount: Int = 1,
    ): DataHolderScanSummary =
        DataHolderScanSummary(
            dataHolderId = dataHolderId,
            owner = owner,
            scannedAtEpochMs = scannedAtEpochMs,
            scanRangeFromEpochMs = scanRangeFromEpochMs,
            scanRangeToEpochMs = scanRangeToEpochMs,
            emailCount = emailCount,
            readCount = readCount,
            readRate = readRate,
            marketingEmailCount = marketingEmailCount,
            marketingEmailOpened = marketingEmailOpened,
            marketingOpenRate = marketingOpenRate,
            categoryBreakdown = categoryBreakdown,
            uncategorizedCount = uncategorizedCount,
        )

    fun getDataHolderQueryResponse(dataHolder: DataHolder? = getDataHolderFragment()): GraphQLResponse<GetDataHolderQuery.Data> =
        GraphQLResponse(
            GetDataHolderQuery.Data(
                dataHolder?.let {
                    GetDataHolderQuery.GetDataHolder("DataHolder", it)
                },
            ),
            null,
        )

    fun listDataHoldersQueryResponse(
        dataHolders: List<DataHolder> = listOf(getDataHolderFragment()),
        nextToken: String? = null,
    ): GraphQLResponse<ListDataHoldersQuery.Data> =
        GraphQLResponse(
            ListDataHoldersQuery.Data(
                ListDataHoldersQuery.ListDataHolders(
                    items =
                        dataHolders.map {
                            ListDataHoldersQuery.Item("DataHolder", it)
                        },
                    nextToken = nextToken,
                ),
            ),
            null,
        )

    fun listDataHolderScanSummariesQueryResponse(
        scanSummaries: List<DataHolderScanSummary> = listOf(getDataHolderScanSummaryFragment()),
        nextToken: String? = null,
    ): GraphQLResponse<ListDataHolderScanSummariesQuery.Data> =
        GraphQLResponse(
            ListDataHolderScanSummariesQuery.Data(
                ListDataHolderScanSummariesQuery.ListDataHolderScanSummaries(
                    items =
                        scanSummaries.map {
                            ListDataHolderScanSummariesQuery.Item("DataHolderScanSummary", it)
                        },
                    nextToken = nextToken,
                ),
            ),
            null,
        )

    fun getAnalysisResultDataFragment(): AnalysisResultData =
        AnalysisResultData(
            privacyScore =
                AnalysisResultData.PrivacyScore(
                    score = 82.5,
                    breakdown = listOf(AnalysisResultData.Breakdown(aspect = "dataSharing", contribution = -5.0)),
                    coverage = AnalysisResultData.Coverage(evaluated = 6, total = 8),
                ),
            privacySummary =
                AnalysisResultData.PrivacySummary(
                    bulletPoints = listOf("Collects location", "Shares with partners"),
                    sourceUrl = "https://example.com/privacy",
                    sourceLastUpdated = "2026-01-01",
                ),
            categories =
                listOf(
                    AnalysisResultData.Category(
                        category = DataCategory.LOCATION,
                        collected = SignalValue.YES,
                        dataLabels = listOf("GPS"),
                        sharedWithThirdParties = ShareStyle.SHARED_WITH_CONSENT,
                        monetized = SignalValue.NO,
                        retained = SignalValue.YES,
                        retention =
                            AnalysisResultData.Retention(
                                style = RetentionStyle.RELATIVE_TO_ACCOUNT_CLOSURE_TIME,
                                timeInDays = 90,
                                additionalInfo = "Deleted on request",
                            ),
                        userCanOptOut = SignalValue.YES,
                        requiredForService = null,
                        requiredForLaw = null,
                    ),
                ),
            capabilities =
                AnalysisResultData.Capabilities(
                    supportsAccountCreation = SignalValue.YES,
                    supportsAccountDeletion = SignalValue.YES,
                    supportsDataDeletionRequests = SignalValue.YES,
                    supportsDataExport = SignalValue.NO,
                    supportsSubscriptions = SignalValue.NO,
                    supportsOptOutOfSale = SignalValue.NO,
                    usesCookiesOrTracking = SignalValue.YES,
                    supportsTwoFactorAuth = SignalValue.YES,
                ),
            riskIndicators =
                AnalysisResultData.RiskIndicators(
                    dataCollectionBreadth = 3,
                    collectsSensitiveDataForNonEssentialPurposes = SignalValue.NO,
                    sellsPersonalInformation = SignalValue.NO,
                    maxRetentionDays = 365,
                    hasIndefiniteRetention = SignalValue.NO,
                    encryptionPractices = SignalValue.UNKNOWN,
                    breachRisk = SignalValue.UNKNOWN,
                ),
            organizationIdentity =
                AnalysisResultData.OrganizationIdentity(
                    brandName = "Netflix",
                    companyName = "Netflix, Inc.",
                    primaryCategory = OrganizationCategory.Entertainment,
                    categories = listOf(OrganizationCategory.Entertainment, OrganizationCategory.Technology),
                ),
            attribution = listOf("source: privacy-policy"),
        )

    fun getAnalysisResultFragment(
        id: String = "mockAnalysisResultId",
        owner: String = "mockOwner",
        virtualPresenceId: String = "mockVirtualPresenceId",
        dataHolderIdentifier: String = "netflix.com",
        status: AnalysisResultStatus = AnalysisResultStatus.COMPLETE,
        lastAnalyzedAtEpochMs: Double = 1.0,
        data: AnalysisResultData? = getAnalysisResultDataFragment(),
        version: Int = 1,
        createdAtEpochMs: Double = 1.0,
        updatedAtEpochMs: Double = 1.0,
    ): AnalysisResult =
        AnalysisResult(
            id = id,
            virtualPresenceId = virtualPresenceId,
            dataHolderIdentifier = dataHolderIdentifier,
            status = status,
            lastAnalyzedAtEpochMs = lastAnalyzedAtEpochMs,
            data = data?.let { AnalysisResult.Data("AnalysisResultData", it) },
            owner = owner,
            version = version,
            createdAtEpochMs = createdAtEpochMs,
            updatedAtEpochMs = updatedAtEpochMs,
        )

    fun getAnalysisResultQueryResponse(
        analysisResult: AnalysisResult? = getAnalysisResultFragment(),
    ): GraphQLResponse<GetAnalysisResultQuery.Data> =
        GraphQLResponse(
            GetAnalysisResultQuery.Data(
                analysisResult?.let { GetAnalysisResultQuery.GetAnalysisResult("AnalysisResult", it) },
            ),
            null,
        )

    fun listAnalysisResultsQueryResponse(
        analysisResults: List<AnalysisResult> = listOf(getAnalysisResultFragment()),
        nextToken: String? = null,
    ): GraphQLResponse<ListAnalysisResultsQuery.Data> =
        GraphQLResponse(
            ListAnalysisResultsQuery.Data(
                ListAnalysisResultsQuery.ListAnalysisResults(
                    items =
                        analysisResults.map {
                            ListAnalysisResultsQuery.Item("AnalysisResult", it)
                        },
                    nextToken = nextToken,
                ),
            ),
            null,
        )

    fun getOrganizationAnalysisFragment(
        id: String = "netflix.com",
        domain: String = "netflix.com",
        status: AnalysisResultStatus = AnalysisResultStatus.COMPLETE,
        lastAnalyzedAtEpochMs: Double = 1.0,
        data: AnalysisResultData? = getAnalysisResultDataFragment(),
        owner: String = "mockOwner",
        version: Int = 1,
        createdAtEpochMs: Double = 1.0,
        updatedAtEpochMs: Double = 1.0,
    ): OrganizationAnalysis =
        OrganizationAnalysis(
            id = id,
            domain = domain,
            status = status,
            lastAnalyzedAtEpochMs = lastAnalyzedAtEpochMs,
            data = data?.let { OrganizationAnalysis.Data("AnalysisResultData", it) },
            owner = owner,
            version = version,
            createdAtEpochMs = createdAtEpochMs,
            updatedAtEpochMs = updatedAtEpochMs,
        )

    fun getOrganizationAnalysisQueryResponse(
        organizationAnalysis: OrganizationAnalysis? = getOrganizationAnalysisFragment(),
    ): GraphQLResponse<GetOrganizationAnalysisQuery.Data> =
        GraphQLResponse(
            GetOrganizationAnalysisQuery.Data(
                organizationAnalysis?.let {
                    GetOrganizationAnalysisQuery.GetOrganizationAnalysis("OrganizationAnalysis", it)
                },
            ),
            null,
        )

    fun getActionFragment(
        id: String = "mockActionId",
        owner: String = "mockOwner",
        virtualPresenceId: String = "mockVirtualPresenceId",
        dataHolderId: String? = "mockDataHolderId",
        intent: ActionIntent = ActionIntent.DELETE_MY_DATA,
        additionalInfo: String? = null,
        fulfilmentMethod: ActionFulfilmentMethod = ActionFulfilmentMethod.ASSISTED,
        status: ActionStatus = ActionStatus.PENDING,
        assistedPayload: Action.AssistedPayload? = getAssistedPayloadFragment(),
        initiatedAtEpochMs: Double? = 1.0,
        completedAtEpochMs: Double? = null,
        version: Int = 1,
        createdAtEpochMs: Double = 1.0,
        updatedAtEpochMs: Double = 1.0,
    ): Action =
        Action(
            id = id,
            virtualPresenceId = virtualPresenceId,
            dataHolderId = dataHolderId,
            intent = intent,
            additionalInfo = additionalInfo,
            fulfilmentMethod = fulfilmentMethod,
            status = status,
            assistedPayload = assistedPayload,
            initiatedAtEpochMs = initiatedAtEpochMs,
            completedAtEpochMs = completedAtEpochMs,
            owner = owner,
            version = version,
            createdAtEpochMs = createdAtEpochMs,
            updatedAtEpochMs = updatedAtEpochMs,
        )

    fun getAssistedPayloadFragment(): Action.AssistedPayload =
        Action.AssistedPayload(
            toAddress = "privacy@netflix.com",
            unsubscribeUrl = "https://netflix.com/unsubscribe",
            unsubscribeMailto = "unsubscribe@netflix.com",
            contact =
                Action.Contact(
                    email = "contact@netflix.com",
                    phone = "+15551234567",
                    address = "100 Winchester Circle, Los Gatos, CA",
                ),
            jurisdiction = "US-CA",
            instructions = "Reply to the email to confirm.",
            emailTemplate =
                Action.EmailTemplate(
                    subject = "Please delete my data",
                    body = "Hello, I would like to request deletion of my data.",
                ),
        )

    fun getAvailableActionFragment(
        intent: ActionIntent = ActionIntent.STOP_CONTACT,
        additionalInfo: String? = null,
        fulfilmentMethod: ActionFulfilmentMethod = ActionFulfilmentMethod.ASSISTED,
        origin: ActionOrigin = ActionOrigin.DISCOVERY,
        title: String? = "Unsubscribe",
        description: String? = "Opt out of marketing emails",
    ): AvailableAction =
        AvailableAction(
            intent = intent,
            additionalInfo = additionalInfo,
            fulfilmentMethod = fulfilmentMethod,
            origin = origin,
            title = title,
            description = description,
        )

    fun listAvailableActionsQueryResponse(
        availableActions: List<AvailableAction> = listOf(getAvailableActionFragment()),
        nextToken: String? = null,
    ): GraphQLResponse<ListAvailableActionsQuery.Data> =
        GraphQLResponse(
            ListAvailableActionsQuery.Data(
                ListAvailableActionsQuery.ListAvailableActions(
                    items = availableActions.map { ListAvailableActionsQuery.Item("AvailableAction", it) },
                    nextToken = nextToken,
                ),
            ),
            null,
        )

    fun initiateActionMutationResponse(action: Action = getActionFragment()): GraphQLResponse<InitiateActionMutation.Data> =
        GraphQLResponse(
            InitiateActionMutation.Data(
                InitiateActionMutation.InitiateAction("Action", action),
            ),
            null,
        )

    fun confirmActionOutcomeMutationResponse(
        action: Action = getActionFragment(status = ActionStatus.COMPLETED_VERIFIED),
    ): GraphQLResponse<ConfirmActionOutcomeMutation.Data> =
        GraphQLResponse(
            ConfirmActionOutcomeMutation.Data(
                ConfirmActionOutcomeMutation.ConfirmActionOutcome("Action", action),
            ),
            null,
        )

    fun getActionQueryResponse(action: Action? = getActionFragment()): GraphQLResponse<GetActionQuery.Data> =
        GraphQLResponse(
            GetActionQuery.Data(
                action?.let { GetActionQuery.GetAction("Action", it) },
            ),
            null,
        )

    fun unarchiveDataHolderMutationResponse(
        dataHolder: DataHolder =
            getDataHolderFragment(protectionState = DataHolderProtectionState.MONITORED),
    ): GraphQLResponse<UnarchiveDataHolderMutation.Data> =
        GraphQLResponse(
            UnarchiveDataHolderMutation.Data(
                UnarchiveDataHolderMutation.UnarchiveDataHolder("DataHolder", dataHolder),
            ),
            null,
        )
}
