/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * The Sudo Platform SDK representation of an analysis result.
 *
 * @property id [String] Unique identifier associated with the analysis result.
 * @property owner [String] Unique identifier of the user associated with the analysis result.
 * @property virtualPresenceId [String] The virtual presence this analysis is associated with.
 * @property dataHolderIdentifier [String] The data holder domain that this analysis is about.
 * @property status [AnalysisResultStatus] Current status of the analysis.
 * @property statusReason [AnalysisResultStatusReason] Reason associated with the current [status].
 *  `null` when no reason is provided.
 * @property lastAnalyzedAt [Date] Date for when the analysis was last performed.
 * @property data [AnalysisResultData] Structured analysis data. Populated when status is COMPLETE
 *  or PARTIAL, otherwise `null`.
 * @property version [Int] Version of this entity. Increments on update.
 * @property createdAt [Date] Date for when the analysis result was created.
 * @property updatedAt [Date] Date for when the analysis result was last updated.
 */
@Parcelize
data class AnalysisResult(
    val id: String,
    val owner: String,
    val virtualPresenceId: String,
    val dataHolderIdentifier: String,
    val status: AnalysisResultStatus,
    val statusReason: AnalysisResultStatusReason? = null,
    val lastAnalyzedAt: Date,
    val data: AnalysisResultData? = null,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date,
) : Parcelable

/**
 * The status of an [AnalysisResult].
 */
enum class AnalysisResultStatus {
    /** The analysis is queued but has not yet started. */
    PENDING,

    /** The analysis has completed successfully with full data. */
    COMPLETE,

    /** The analysis has completed with partial data available. */
    PARTIAL,

    /** The analysis encountered an error and could not complete. */
    FAILED,

    /** The analysis data is not available for this data holder. */
    UNAVAILABLE,
    UNKNOWN,
}

/**
 * The reason associated with an [AnalysisResultStatus].
 */
enum class AnalysisResultStatusReason {
    /** The requested domain was not a valid domain. */
    INVALID_DOMAIN,

    /** No privacy policy was available to analyze for the domain. */
    POLICY_UNAVAILABLE,

    /** The domain was identified as malicious. */
    MALICIOUS,

    /** Analysis was not attempted for the domain. */
    NOT_ATTEMPTED,

    /** A reason that does not fit the other categories. */
    OTHER,
    UNKNOWN,
}

/**
 * The structured data payload for an [AnalysisResult].
 *
 * @property privacyScore [PrivacyScore] Overall privacy score for the organization. `null` when
 *  scoring is disabled.
 * @property privacySummary [PrivacySummary] Concise human-readable summary of privacy findings.
 * @property categories [List] Per-category signal breakdown.
 * @property capabilities [CapabilitySignals] Action-derived capability signals.
 * @property riskIndicators [RiskIndicators] Aggregated risk indicators.
 * @property organizationIdentity [OrganizationIdentity] Organization identity (brand, company,
 *  business categories). `null` when unavailable.
 * @property attribution [List] Attribution strings crediting each data source that contributed
 *  to this result.
 */
@Parcelize
data class AnalysisResultData(
    val privacyScore: PrivacyScore? = null,
    val privacySummary: PrivacySummary,
    val categories: List<CategorySignals>,
    val capabilities: CapabilitySignals,
    val riskIndicators: RiskIndicators,
    val organizationIdentity: OrganizationIdentity? = null,
    val attribution: List<String>,
) : Parcelable

/**
 * Overall privacy score for an organization.
 *
 * @property score [Double] Numeric score (0-100). Higher is better.
 * @property breakdown [List] Breakdown of how each aspect contributed to the score.
 * @property coverage [ScoreCoverage] How much of the scoring-relevant input had a known value.
 */
@Parcelize
data class PrivacyScore(
    val score: Double,
    val breakdown: List<ScoreContribution>,
    val coverage: ScoreCoverage,
) : Parcelable

/**
 * A single scoring aspect and its contribution to the overall score.
 *
 * @property aspect [String] Identifier for the scoring aspect (e.g. 'dataCollection').
 * @property contribution [Double] Numeric contribution to the score (negative = penalty).
 */
@Parcelize
data class ScoreContribution(
    val aspect: String,
    val contribution: Double,
) : Parcelable

/**
 * How much of the scoring-relevant input had a known value. A low ratio means the score rests on
 * few known signals and should be treated as lower-confidence.
 *
 * @property evaluated [Int] Scoring aspects with a known value.
 * @property total [Int] Total scoring aspects the calculator considers.
 */
@Parcelize
data class ScoreCoverage(
    val evaluated: Int,
    val total: Int,
) : Parcelable

/**
 * Concise human-readable summary of privacy-related findings.
 *
 * @property bulletPoints [List] Up to 5 key takeaways, deterministically generated.
 * @property sourceUrl [String] URL of the source material that was analyzed.
 * @property sourceLastUpdated [String] When the source material was last updated (as reported by
 *  the organization). `null` when unknown.
 */
@Parcelize
data class PrivacySummary(
    val bulletPoints: List<String>,
    val sourceUrl: String,
    val sourceLastUpdated: String? = null,
) : Parcelable

/**
 * Privacy signals specific to a single data category.
 *
 * @property category [DataCategory] The data category these signals apply to.
 * @property collected [SignalValue] Whether this category of data is collected.
 * @property dataLabels [List] Descriptive labels for the collected data within this category.
 * @property sharedWithThirdParties [ShareStyle] How this category is shared with third parties.
 * @property monetized [SignalValue] Whether this category is used for monetization.
 * @property retained [SignalValue] Whether this category is retained.
 * @property retention [RetentionInfo] Retention details when data is retained. `null` otherwise.
 * @property userCanOptOut [SignalValue] Whether the user can opt out of this category's collection.
 * @property requiredForService [SignalValue] Whether this data is required for the core service.
 * @property requiredForLaw [SignalValue] Whether this data is collected to comply with the law.
 */
@Parcelize
data class CategorySignals(
    val category: DataCategory,
    val collected: SignalValue,
    val dataLabels: List<String>,
    val sharedWithThirdParties: ShareStyle,
    val monetized: SignalValue,
    val retained: SignalValue,
    val retention: RetentionInfo? = null,
    val userCanOptOut: SignalValue,
    val requiredForService: SignalValue,
    val requiredForLaw: SignalValue,
) : Parcelable

/**
 * Retention details for a specific data category.
 *
 * @property style [RetentionStyle] How the retention period is expressed.
 * @property timeInDays [Int] Retention period in days, when specified. `null` otherwise.
 * @property additionalInfo [String] Free-text clarification from the policy. `null` otherwise.
 */
@Parcelize
data class RetentionInfo(
    val style: RetentionStyle,
    val timeInDays: Int? = null,
    val additionalInfo: String? = null,
) : Parcelable

/**
 * Signals derived from privacy actions and capabilities.
 *
 * @property supportsOptOutOfSale [SignalValue] Whether the organization exposes an
 *  opt-out-of-sale control to users.
 * @property sellsPersonalInformation [SignalValue] Whether the organization sells personal
 *  information. Populated from [supportsOptOutOfSale].
 */
@Parcelize
data class CapabilitySignals(
    val supportsAccountCreation: SignalValue,
    val supportsAccountDeletion: SignalValue,
    val supportsDataDeletionRequests: SignalValue,
    val supportsDataExport: SignalValue,
    val supportsSubscriptions: SignalValue,
    val supportsOptOutOfSale: SignalValue,
    @Deprecated(
        message = "Use supportsOptOutOfSale instead. Populated from the supportsOptOutOfSale value.",
        replaceWith = ReplaceWith("supportsOptOutOfSale"),
    )
    val sellsPersonalInformation: SignalValue,
    val usesCookiesOrTracking: SignalValue,
    val supportsTwoFactorAuth: SignalValue,
) : Parcelable

/**
 * Aggregated risk indicators derived from categories and capabilities.
 *
 * @property dataCollectionBreadth [Int] Total number of data categories collected.
 * @property collectsSensitiveDataForNonEssentialPurposes [SignalValue] Whether sensitive
 *  categories are collected for non-essential purposes.
 * @property sellsPersonalInformation [SignalValue] Whether data selling is indicated.
 * @property maxRetentionDays [Int] Longest known retention period in days across all categories.
 *  `null` when unknown.
 * @property hasIndefiniteRetention [SignalValue] Whether any category has indefinite retention.
 * @property encryptionPractices [SignalValue] Whether strong encryption practices are in place.
 * @property breachRisk [SignalValue] Whether there is an elevated risk of data breach.
 */
@Parcelize
data class RiskIndicators(
    val dataCollectionBreadth: Int,
    val collectsSensitiveDataForNonEssentialPurposes: SignalValue,
    val sellsPersonalInformation: SignalValue,
    val maxRetentionDays: Int? = null,
    val hasIndefiniteRetention: SignalValue,
    val encryptionPractices: SignalValue,
    val breachRisk: SignalValue,
) : Parcelable

/**
 * Identifies the organization behind a data holder.
 *
 * @property brandName [String] The consumer-facing brand name of the organization.
 * @property companyName [String] The legal or corporate name of the organization.
 * @property primaryCategory [OrganizationCategory] The primary business category.
 * @property categories [List] All business categories the organization belongs to.
 */
@Parcelize
data class OrganizationIdentity(
    val brandName: String,
    val companyName: String,
    val primaryCategory: OrganizationCategory,
    val categories: List<OrganizationCategory>,
) : Parcelable

/**
 * Tri-state signal value representing confidence in a determination.
 */
enum class SignalValue {
    YES,
    NO,
    UNKNOWN,
}

/**
 * Describes how data in a category is shared with third parties.
 */
enum class ShareStyle {
    /** Data is shared with third parties. */
    SHARED,

    /** Data is shared with third parties only with user consent. */
    SHARED_WITH_CONSENT,

    /** Inferred data is shared with third parties. */
    INFERENCE_SHARED,

    /** Data is not shared with third parties. */
    NONE,
    UNKNOWN,
}

/**
 * Categories of personal data as identified in a privacy policy.
 */
enum class DataCategory {
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

/**
 * Enumeration depicting how the data retention period is measured.
 */
enum class RetentionStyle {
    /** Data is retained indefinitely with no defined end date. */
    INDEFINITE,

    /** Retention period starts when the account is closed. */
    RELATIVE_TO_ACCOUNT_CLOSURE_TIME,

    /** Retention period starts when the data is stored. */
    RELATIVE_TO_DATA_STORAGE_TIME,

    /** Retention policy does not fit standard categories. */
    OTHER,
    UNKNOWN,
}

/**
 * The business category of an organization as classified by the analysis.
 */
enum class OrganizationCategory {
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
