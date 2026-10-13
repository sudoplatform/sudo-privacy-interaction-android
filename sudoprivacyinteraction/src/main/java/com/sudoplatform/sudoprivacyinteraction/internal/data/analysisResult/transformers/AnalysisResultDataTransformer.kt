/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultDataEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.CapabilitySignalsEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.CategorySignalsEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.OrganizationCategoryEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.OrganizationIdentityEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.PrivacyScoreEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.PrivacySummaryEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.RetentionInfoEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.RiskIndicatorsEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ScoreContributionEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.ScoreCoverageEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.SignalValueEntity
import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResultData
import com.sudoplatform.sudoprivacyinteraction.types.CapabilitySignals
import com.sudoplatform.sudoprivacyinteraction.types.CategorySignals
import com.sudoplatform.sudoprivacyinteraction.types.OrganizationIdentity
import com.sudoplatform.sudoprivacyinteraction.types.PrivacyScore
import com.sudoplatform.sudoprivacyinteraction.types.PrivacySummary
import com.sudoplatform.sudoprivacyinteraction.types.RetentionInfo
import com.sudoplatform.sudoprivacyinteraction.types.RiskIndicators
import com.sudoplatform.sudoprivacyinteraction.types.ScoreContribution
import com.sudoplatform.sudoprivacyinteraction.types.ScoreCoverage
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.AnalysisResultData as AnalysisResultDataFragment

/**
 * Transformer responsible for transforming the structured [AnalysisResultData] payload between the
 * GraphQL fragment, the entity graph used in business logic, and the public type.
 */
internal object AnalysisResultDataTransformer {
    fun fromGraphQLToEntity(data: AnalysisResultDataFragment): AnalysisResultDataEntity =
        AnalysisResultDataEntity(
            privacyScore =
                data.privacyScore?.let { score ->
                    PrivacyScoreEntity(
                        score = score.score,
                        breakdown = score.breakdown.map { ScoreContributionEntity(it.aspect, it.contribution) },
                        coverage = ScoreCoverageEntity(score.coverage.evaluated, score.coverage.total),
                    )
                },
            privacySummary =
                PrivacySummaryEntity(
                    bulletPoints = data.privacySummary.bulletPoints.toList(),
                    sourceUrl = data.privacySummary.sourceUrl,
                    sourceLastUpdated = data.privacySummary.sourceLastUpdated,
                ),
            categories =
                data.categories.map { category ->
                    CategorySignalsEntity(
                        category = DataCategoryTransformer.fromGraphQLToEntity(category.category),
                        collected = SignalValueTransformer.fromGraphQLToEntity(category.collected),
                        dataLabels = category.dataLabels.toList(),
                        sharedWithThirdParties = ShareStyleTransformer.fromGraphQLToEntity(category.sharedWithThirdParties),
                        monetized = SignalValueTransformer.fromGraphQLToEntity(category.monetized),
                        retained = SignalValueTransformer.fromGraphQLToEntity(category.retained),
                        retention =
                            category.retention?.let {
                                RetentionInfoEntity(
                                    style = RetentionStyleTransformer.fromGraphQLToEntity(it.style),
                                    timeInDays = it.timeInDays,
                                    additionalInfo = it.additionalInfo,
                                )
                            },
                        userCanOptOut = SignalValueTransformer.fromGraphQLToEntity(category.userCanOptOut),
                        requiredForService =
                            category.requiredForService?.let { SignalValueTransformer.fromGraphQLToEntity(it) }
                                ?: SignalValueEntity.NO,
                        requiredForLaw =
                            category.requiredForLaw?.let { SignalValueTransformer.fromGraphQLToEntity(it) }
                                ?: SignalValueEntity.NO,
                    )
                },
            capabilities =
                data.capabilities.let { c ->
                    CapabilitySignalsEntity(
                        supportsAccountCreation = SignalValueTransformer.fromGraphQLToEntity(c.supportsAccountCreation),
                        supportsAccountDeletion = SignalValueTransformer.fromGraphQLToEntity(c.supportsAccountDeletion),
                        supportsDataDeletionRequests = SignalValueTransformer.fromGraphQLToEntity(c.supportsDataDeletionRequests),
                        supportsDataExport = SignalValueTransformer.fromGraphQLToEntity(c.supportsDataExport),
                        supportsSubscriptions = SignalValueTransformer.fromGraphQLToEntity(c.supportsSubscriptions),
                        sellsPersonalInformation = SignalValueTransformer.fromGraphQLToEntity(c.sellsPersonalInformation),
                        usesCookiesOrTracking = SignalValueTransformer.fromGraphQLToEntity(c.usesCookiesOrTracking),
                        supportsTwoFactorAuth = SignalValueTransformer.fromGraphQLToEntity(c.supportsTwoFactorAuth),
                    )
                },
            riskIndicators =
                data.riskIndicators.let { r ->
                    RiskIndicatorsEntity(
                        dataCollectionBreadth = r.dataCollectionBreadth,
                        collectsSensitiveDataForNonEssentialPurposes =
                            SignalValueTransformer.fromGraphQLToEntity(r.collectsSensitiveDataForNonEssentialPurposes),
                        sellsPersonalInformation = SignalValueTransformer.fromGraphQLToEntity(r.sellsPersonalInformation),
                        maxRetentionDays = r.maxRetentionDays,
                        hasIndefiniteRetention = SignalValueTransformer.fromGraphQLToEntity(r.hasIndefiniteRetention),
                        encryptionPractices = SignalValueTransformer.fromGraphQLToEntity(r.encryptionPractices),
                        breachRisk = SignalValueTransformer.fromGraphQLToEntity(r.breachRisk),
                    )
                },
            organizationIdentity =
                data.organizationIdentity?.let { identity ->
                    OrganizationIdentityEntity(
                        brandName = identity.brandName ?: "",
                        companyName = identity.companyName ?: "",
                        primaryCategory =
                            identity.primaryCategory?.let { OrganizationCategoryTransformer.fromGraphQLToEntity(it) }
                                ?: OrganizationCategoryEntity.OTHER,
                        categories = identity.categories.map { OrganizationCategoryTransformer.fromGraphQLToEntity(it) },
                    )
                } ?: OrganizationIdentityEntity(
                    brandName = "",
                    companyName = "",
                    primaryCategory = OrganizationCategoryEntity.OTHER,
                    categories = emptyList(),
                ),
            attribution = data.attribution.toList(),
        )

    fun fromEntityToApi(entity: AnalysisResultDataEntity): AnalysisResultData =
        AnalysisResultData(
            privacyScore =
                entity.privacyScore?.let { score ->
                    PrivacyScore(
                        score = score.score,
                        breakdown = score.breakdown.map { ScoreContribution(it.aspect, it.contribution) },
                        coverage = ScoreCoverage(score.coverage.evaluated, score.coverage.total),
                    )
                },
            privacySummary =
                PrivacySummary(
                    bulletPoints = entity.privacySummary.bulletPoints.toList(),
                    sourceUrl = entity.privacySummary.sourceUrl,
                    sourceLastUpdated = entity.privacySummary.sourceLastUpdated,
                ),
            categories =
                entity.categories.map { category ->
                    CategorySignals(
                        category = DataCategoryTransformer.fromEntityToApi(category.category),
                        collected = SignalValueTransformer.fromEntityToApi(category.collected),
                        dataLabels = category.dataLabels.toList(),
                        sharedWithThirdParties = ShareStyleTransformer.fromEntityToApi(category.sharedWithThirdParties),
                        monetized = SignalValueTransformer.fromEntityToApi(category.monetized),
                        retained = SignalValueTransformer.fromEntityToApi(category.retained),
                        retention =
                            category.retention?.let {
                                RetentionInfo(
                                    style = RetentionStyleTransformer.fromEntityToApi(it.style),
                                    timeInDays = it.timeInDays,
                                    additionalInfo = it.additionalInfo,
                                )
                            },
                        userCanOptOut = SignalValueTransformer.fromEntityToApi(category.userCanOptOut),
                        requiredForService = SignalValueTransformer.fromEntityToApi(category.requiredForService),
                        requiredForLaw = SignalValueTransformer.fromEntityToApi(category.requiredForLaw),
                    )
                },
            capabilities =
                entity.capabilities.let { c ->
                    CapabilitySignals(
                        supportsAccountCreation = SignalValueTransformer.fromEntityToApi(c.supportsAccountCreation),
                        supportsAccountDeletion = SignalValueTransformer.fromEntityToApi(c.supportsAccountDeletion),
                        supportsDataDeletionRequests = SignalValueTransformer.fromEntityToApi(c.supportsDataDeletionRequests),
                        supportsDataExport = SignalValueTransformer.fromEntityToApi(c.supportsDataExport),
                        supportsSubscriptions = SignalValueTransformer.fromEntityToApi(c.supportsSubscriptions),
                        sellsPersonalInformation = SignalValueTransformer.fromEntityToApi(c.sellsPersonalInformation),
                        usesCookiesOrTracking = SignalValueTransformer.fromEntityToApi(c.usesCookiesOrTracking),
                        supportsTwoFactorAuth = SignalValueTransformer.fromEntityToApi(c.supportsTwoFactorAuth),
                    )
                },
            riskIndicators =
                entity.riskIndicators.let { r ->
                    RiskIndicators(
                        dataCollectionBreadth = r.dataCollectionBreadth,
                        collectsSensitiveDataForNonEssentialPurposes =
                            SignalValueTransformer.fromEntityToApi(r.collectsSensitiveDataForNonEssentialPurposes),
                        sellsPersonalInformation = SignalValueTransformer.fromEntityToApi(r.sellsPersonalInformation),
                        maxRetentionDays = r.maxRetentionDays,
                        hasIndefiniteRetention = SignalValueTransformer.fromEntityToApi(r.hasIndefiniteRetention),
                        encryptionPractices = SignalValueTransformer.fromEntityToApi(r.encryptionPractices),
                        breachRisk = SignalValueTransformer.fromEntityToApi(r.breachRisk),
                    )
                },
            organizationIdentity =
                entity.organizationIdentity?.let { identity ->
                    OrganizationIdentity(
                        brandName = identity.brandName,
                        companyName = identity.companyName,
                        primaryCategory = OrganizationCategoryTransformer.fromEntityToApi(identity.primaryCategory),
                        categories = identity.categories.map { OrganizationCategoryTransformer.fromEntityToApi(it) },
                    )
                },
            attribution = entity.attribution.toList(),
        )
}
