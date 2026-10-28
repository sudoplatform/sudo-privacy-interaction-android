/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.data

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionFulfilmentMethodEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionIntentEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionOriginEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionStatusEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.AssistedEmailTemplateEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.AssistedPayloadEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.AvailableActionEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultDataEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultStatusEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.CapabilitySignalsEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.CategorySignalsEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.DataCategoryEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.OrganizationCategoryEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.OrganizationIdentityEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.PrivacyScoreEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.PrivacySummaryEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.RetentionInfoEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.RetentionStyleEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.RiskIndicatorsEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ScoreContributionEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ScoreCoverageEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ShareStyleEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.SignalValueEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.configuration.ProviderConfigurationEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderProtectionStateEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderScanSummaryEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.DataHolderCountEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ProviderTypeEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.RelationshipProviderEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceStateEntity
import java.util.Date

/**
 * Factory for building entity-layer test fixtures.
 */
internal object EntityDataFactory {
    fun getVirtualPresenceEntity(
        id: String = "mockVirtualPresenceId",
        owner: String = "mockOwner",
        version: Int = 1,
        providerType: ProviderTypeEntity = ProviderTypeEntity.EMAIL,
        identifier: String = "test@sudoplatform.com",
        state: VirtualPresenceStateEntity = VirtualPresenceStateEntity.CONNECTED,
        relationshipProvider: RelationshipProviderEntity? = null,
        lastScannedAt: Date = Date(1L),
        lastScanFailureReason: String? = null,
        createdAt: Date = Date(1L),
        updatedAt: Date = Date(1L),
        dataHolderCount: DataHolderCountEntity =
            DataHolderCountEntity(
                count = 6,
                monitored = 1,
                initiatedActions = 2,
                completedActions = 3,
                archived = 4,
            ),
    ): VirtualPresenceEntity =
        VirtualPresenceEntity(
            id = id,
            owner = owner,
            version = version,
            providerType = providerType,
            identifier = identifier,
            state = state,
            relationshipProvider = relationshipProvider,
            lastScannedAt = lastScannedAt,
            lastScanFailureReason = lastScanFailureReason,
            dataHolderCount = dataHolderCount,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    fun getProviderConfigurationEntity(
        name: String = "google",
        providerType: ProviderTypeEntity = ProviderTypeEntity.EMAIL,
        clientId: String = "mockClientId",
    ): ProviderConfigurationEntity =
        ProviderConfigurationEntity(
            name = name,
            providerType = providerType,
            clientId = clientId,
        )

    fun getDataHolderEntity(
        id: String = "mockDataHolderId",
        owner: String = "mockOwner",
        virtualPresenceId: String = "mockVirtualPresenceId",
        domainName: String = "google.com",
        name: String = "Google",
        protectionState: DataHolderProtectionStateEntity = DataHolderProtectionStateEntity.MONITORED,
        complianceConcern: Boolean = false,
        mostRecentInteractionAt: Date = Date(1L),
        version: Int = 1,
        createdAt: Date = Date(1L),
        updatedAt: Date = Date(1L),
    ): DataHolderEntity =
        DataHolderEntity(
            id = id,
            owner = owner,
            virtualPresenceId = virtualPresenceId,
            domainName = domainName,
            name = name,
            protectionState = protectionState,
            complianceConcern = complianceConcern,
            mostRecentInteractionAt = mostRecentInteractionAt,
            version = version,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    fun getDataHolderScanSummaryEntity(
        dataHolderId: String = "mockDataHolderId",
        owner: String = "mockOwner",
        scannedAt: Date = Date(1L),
        scanRangeFrom: Date = Date(1L),
        scanRangeTo: Date = Date(2L),
        emailCount: Int = 10,
        readCount: Int = 5,
        readRate: Double = 0.5,
        marketingEmailCount: Int = 4,
        marketingEmailOpened: Int = 1,
        marketingOpenRate: Double = 0.25,
        categoryBreakdown: Map<String, Int> = mapOf("promotions" to 3, "updates" to 2),
        uncategorizedCount: Int = 1,
    ): DataHolderScanSummaryEntity =
        DataHolderScanSummaryEntity(
            dataHolderId = dataHolderId,
            owner = owner,
            scannedAt = scannedAt,
            scanRangeFrom = scanRangeFrom,
            scanRangeTo = scanRangeTo,
            emailCount = emailCount,
            readCount = readCount,
            readRate = readRate,
            marketingEmailCount = marketingEmailCount,
            marketingEmailOpened = marketingEmailOpened,
            marketingOpenRate = marketingOpenRate,
            categoryBreakdown = categoryBreakdown,
            uncategorizedCount = uncategorizedCount,
        )

    fun getAnalysisResultDataEntity(): AnalysisResultDataEntity =
        AnalysisResultDataEntity(
            privacyScore =
                PrivacyScoreEntity(
                    score = 82.5,
                    breakdown = listOf(ScoreContributionEntity(aspect = "dataSharing", contribution = -5.0)),
                    coverage = ScoreCoverageEntity(evaluated = 6, total = 8),
                ),
            privacySummary =
                PrivacySummaryEntity(
                    bulletPoints = listOf("Collects location", "Shares with partners"),
                    sourceUrl = "https://example.com/privacy",
                    sourceLastUpdated = "2026-01-01",
                ),
            categories =
                listOf(
                    CategorySignalsEntity(
                        category = DataCategoryEntity.LOCATION,
                        collected = SignalValueEntity.YES,
                        dataLabels = listOf("GPS"),
                        sharedWithThirdParties = ShareStyleEntity.SHARED_WITH_CONSENT,
                        monetized = SignalValueEntity.NO,
                        retained = SignalValueEntity.YES,
                        retention =
                            RetentionInfoEntity(
                                style = RetentionStyleEntity.RELATIVE_TO_ACCOUNT_CLOSURE_TIME,
                                timeInDays = 90,
                                additionalInfo = "Deleted on request",
                            ),
                        userCanOptOut = SignalValueEntity.YES,
                        requiredForService = SignalValueEntity.NO,
                        requiredForLaw = SignalValueEntity.NO,
                    ),
                ),
            capabilities =
                CapabilitySignalsEntity(
                    supportsAccountCreation = SignalValueEntity.YES,
                    supportsAccountDeletion = SignalValueEntity.YES,
                    supportsDataDeletionRequests = SignalValueEntity.YES,
                    supportsDataExport = SignalValueEntity.NO,
                    supportsSubscriptions = SignalValueEntity.NO,
                    supportsOptOutOfSale = SignalValueEntity.NO,
                    usesCookiesOrTracking = SignalValueEntity.YES,
                    supportsTwoFactorAuth = SignalValueEntity.YES,
                ),
            riskIndicators =
                RiskIndicatorsEntity(
                    dataCollectionBreadth = 3,
                    collectsSensitiveDataForNonEssentialPurposes = SignalValueEntity.NO,
                    sellsPersonalInformation = SignalValueEntity.NO,
                    maxRetentionDays = 365,
                    hasIndefiniteRetention = SignalValueEntity.NO,
                    encryptionPractices = SignalValueEntity.UNKNOWN,
                    breachRisk = SignalValueEntity.UNKNOWN,
                ),
            organizationIdentity =
                OrganizationIdentityEntity(
                    brandName = "Netflix",
                    companyName = "Netflix, Inc.",
                    primaryCategory = OrganizationCategoryEntity.ENTERTAINMENT,
                    categories = listOf(OrganizationCategoryEntity.ENTERTAINMENT, OrganizationCategoryEntity.TECHNOLOGY),
                ),
            attribution = listOf("source: privacy-policy"),
        )

    fun getAnalysisResultEntity(
        id: String = "mockAnalysisResultId",
        owner: String = "mockOwner",
        virtualPresenceId: String = "mockVirtualPresenceId",
        dataHolderIdentifier: String = "netflix.com",
        status: AnalysisResultStatusEntity = AnalysisResultStatusEntity.COMPLETE,
        lastAnalyzedAt: Date = Date(1L),
        data: AnalysisResultDataEntity? = getAnalysisResultDataEntity(),
        version: Int = 1,
        createdAt: Date = Date(1L),
        updatedAt: Date = Date(1L),
    ): AnalysisResultEntity =
        AnalysisResultEntity(
            id = id,
            owner = owner,
            virtualPresenceId = virtualPresenceId,
            dataHolderIdentifier = dataHolderIdentifier,
            status = status,
            lastAnalyzedAt = lastAnalyzedAt,
            data = data,
            version = version,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    fun getOrganizationAnalysisEntity(
        id: String = "netflix.com",
        domain: String = "netflix.com",
        status: AnalysisResultStatusEntity = AnalysisResultStatusEntity.COMPLETE,
        lastAnalyzedAt: Date = Date(1L),
        data: AnalysisResultDataEntity? = getAnalysisResultDataEntity(),
        owner: String = "mockOwner",
        version: Int = 1,
        createdAt: Date = Date(1L),
        updatedAt: Date = Date(1L),
    ): OrganizationAnalysisEntity =
        OrganizationAnalysisEntity(
            id = id,
            domain = domain,
            status = status,
            lastAnalyzedAt = lastAnalyzedAt,
            data = data,
            owner = owner,
            version = version,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    fun getAssistedPayloadEntity(): AssistedPayloadEntity =
        AssistedPayloadEntity(
            toAddress = "privacy@netflix.com",
            unsubscribeUrl = "https://netflix.com/unsubscribe",
            unsubscribeMailto = "unsubscribe@netflix.com",
            contact =
                com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionContactEntity(
                    email = "contact@netflix.com",
                    phone = "+15551234567",
                    address = "100 Winchester Circle, Los Gatos, CA",
                ),
            jurisdiction = "US-CA",
            instructions = "Reply to the email to confirm.",
            emailTemplate =
                AssistedEmailTemplateEntity(
                    subject = "Please delete my data",
                    body = "Hello, I would like to request deletion of my data.",
                ),
        )

    fun getActionEntity(
        id: String = "mockActionId",
        owner: String = "mockOwner",
        virtualPresenceId: String = "mockVirtualPresenceId",
        dataHolderId: String? = "mockDataHolderId",
        intent: ActionIntentEntity = ActionIntentEntity.DELETE_MY_DATA,
        additionalInfo: String? = null,
        fulfilmentMethod: ActionFulfilmentMethodEntity = ActionFulfilmentMethodEntity.ASSISTED,
        status: ActionStatusEntity = ActionStatusEntity.PENDING,
        assistedPayload: AssistedPayloadEntity? = getAssistedPayloadEntity(),
        initiatedAt: Date? = Date(1L),
        completedAt: Date? = null,
        version: Int = 1,
        createdAt: Date = Date(1L),
        updatedAt: Date = Date(1L),
    ): ActionEntity =
        ActionEntity(
            id = id,
            owner = owner,
            virtualPresenceId = virtualPresenceId,
            dataHolderId = dataHolderId,
            intent = intent,
            additionalInfo = additionalInfo,
            fulfilmentMethod = fulfilmentMethod,
            status = status,
            assistedPayload = assistedPayload,
            initiatedAt = initiatedAt,
            completedAt = completedAt,
            version = version,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    fun getAvailableActionEntity(
        intent: ActionIntentEntity = ActionIntentEntity.STOP_CONTACT,
        additionalInfo: String? = null,
        fulfilmentMethod: ActionFulfilmentMethodEntity = ActionFulfilmentMethodEntity.ASSISTED,
        origin: ActionOriginEntity = ActionOriginEntity.DISCOVERY,
        title: String? = "Unsubscribe",
        description: String? = "Opt out of marketing emails",
    ): AvailableActionEntity =
        AvailableActionEntity(
            intent = intent,
            additionalInfo = additionalInfo,
            fulfilmentMethod = fulfilmentMethod,
            origin = origin,
            title = title,
            description = description,
        )
}
