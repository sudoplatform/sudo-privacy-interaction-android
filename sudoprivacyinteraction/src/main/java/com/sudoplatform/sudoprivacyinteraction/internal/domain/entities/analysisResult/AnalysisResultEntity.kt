/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult

import java.util.Date

/**
 * Core entity representation of an analysis result business rule.
 */
internal data class AnalysisResultEntity(
    val id: String,
    val owner: String,
    val virtualPresenceId: String,
    val dataHolderIdentifier: String,
    val status: AnalysisResultStatusEntity,
    val lastAnalyzedAt: Date,
    val data: AnalysisResultDataEntity? = null,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date,
)

/**
 * The structured data payload for an analysis result.
 */
internal data class AnalysisResultDataEntity(
    val privacyScore: PrivacyScoreEntity? = null,
    val privacySummary: PrivacySummaryEntity,
    val categories: List<CategorySignalsEntity>,
    val capabilities: CapabilitySignalsEntity,
    val riskIndicators: RiskIndicatorsEntity,
    val organizationIdentity: OrganizationIdentityEntity? = null,
    val attribution: List<String>,
)

internal data class PrivacyScoreEntity(
    val score: Double,
    val breakdown: List<ScoreContributionEntity>,
    val coverage: ScoreCoverageEntity,
)

internal data class ScoreContributionEntity(
    val aspect: String,
    val contribution: Double,
)

internal data class ScoreCoverageEntity(
    val evaluated: Int,
    val total: Int,
)

internal data class PrivacySummaryEntity(
    val bulletPoints: List<String>,
    val sourceUrl: String,
    val sourceLastUpdated: String? = null,
)

internal data class CategorySignalsEntity(
    val category: DataCategoryEntity,
    val collected: SignalValueEntity,
    val dataLabels: List<String>,
    val sharedWithThirdParties: ShareStyleEntity,
    val monetized: SignalValueEntity,
    val retained: SignalValueEntity,
    val retention: RetentionInfoEntity? = null,
    val userCanOptOut: SignalValueEntity,
    val requiredForService: SignalValueEntity,
    val requiredForLaw: SignalValueEntity,
)

internal data class RetentionInfoEntity(
    val style: RetentionStyleEntity,
    val timeInDays: Int? = null,
    val additionalInfo: String? = null,
)

internal data class CapabilitySignalsEntity(
    val supportsAccountCreation: SignalValueEntity,
    val supportsAccountDeletion: SignalValueEntity,
    val supportsDataDeletionRequests: SignalValueEntity,
    val supportsDataExport: SignalValueEntity,
    val supportsSubscriptions: SignalValueEntity,
    val supportsOptOutOfSale: SignalValueEntity,
    val usesCookiesOrTracking: SignalValueEntity,
    val supportsTwoFactorAuth: SignalValueEntity,
)

internal data class RiskIndicatorsEntity(
    val dataCollectionBreadth: Int,
    val collectsSensitiveDataForNonEssentialPurposes: SignalValueEntity,
    val sellsPersonalInformation: SignalValueEntity,
    val maxRetentionDays: Int? = null,
    val hasIndefiniteRetention: SignalValueEntity,
    val encryptionPractices: SignalValueEntity,
    val breachRisk: SignalValueEntity,
)

internal data class OrganizationIdentityEntity(
    val brandName: String,
    val companyName: String,
    val primaryCategory: OrganizationCategoryEntity,
    val categories: List<OrganizationCategoryEntity>,
)

internal enum class AnalysisResultStatusEntity {
    PENDING,
    COMPLETE,
    PARTIAL,
    FAILED,
    UNAVAILABLE,
    UNKNOWN,
}

internal enum class SignalValueEntity {
    YES,
    NO,
    UNKNOWN,
}

internal enum class ShareStyleEntity {
    SHARED,
    SHARED_WITH_CONSENT,
    INFERENCE_SHARED,
    NONE,
    UNKNOWN,
}

internal enum class DataCategoryEntity {
    ACCOUNT_PROFILE,
    AUTHENTICATION,
    BACKGROUND,
    BIOMETRIC,
    COMMUNICATION,
    CONTACTS,
    DEMOGRAPHIC,
    DEVICE_IDENTIFIER,
    FINANCIAL,
    GOVERNMENT_ISSUED,
    HEALTH,
    INFERENCES,
    LOCATION,
    OTHER,
    PII,
    THIRD_PARTY_DATA,
    USAGE,
    UNKNOWN,
}

internal enum class RetentionStyleEntity {
    INDEFINITE,
    RELATIVE_TO_ACCOUNT_CLOSURE_TIME,
    RELATIVE_TO_DATA_STORAGE_TIME,
    OTHER,
    UNKNOWN,
}

internal enum class OrganizationCategoryEntity {
    AD_TECH_AND_MARKETING,
    ADULT_ENTERTAINMENT,
    AUTOMOTIVE,
    COMMUNICATIONS,
    COMMUNITY_AND_CHARITY,
    CRYPTOCURRENCY,
    DATA_BROKER,
    DATING,
    EDUCATION,
    EMPLOYMENT,
    ENERGY_AND_UTILITIES,
    ENTERTAINMENT,
    FINANCE,
    FOOD_AND_DINING,
    GAMBLING,
    GOVERNMENT,
    HEALTH,
    INSURANCE,
    MILITARY,
    NEWS,
    OTHER,
    PERSONAL_EMAIL,
    PRIVACY_AND_SECURITY,
    REAL_ESTATE,
    SHIPPING_AND_DELIVERY,
    SHOPPING,
    SOCIAL_MEDIA,
    SPORTS_AND_FITNESS,
    TECHNOLOGY,
    TRANSPORTATION,
    TRAVEL,
    VIDEO_GAMES,
    UNKNOWN,
}
