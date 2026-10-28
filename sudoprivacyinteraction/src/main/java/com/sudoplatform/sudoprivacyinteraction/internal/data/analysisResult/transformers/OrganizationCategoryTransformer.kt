/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.OrganizationCategoryEntity
import com.sudoplatform.sudoprivacyinteraction.types.OrganizationCategory
import com.sudoplatform.sudoprivacyinteraction.graphql.type.OrganizationCategory as OrganizationCategoryGraphQL

/**
 * Transformer between the [OrganizationCategoryGraphQL] GraphQL enum (PascalCase values), the
 * [OrganizationCategoryEntity] entity enum, and the public [OrganizationCategory] type.
 */
internal object OrganizationCategoryTransformer {
    fun fromGraphQLToEntity(data: OrganizationCategoryGraphQL): OrganizationCategoryEntity =
        when (data) {
            OrganizationCategoryGraphQL.AdTechAndMarketing -> OrganizationCategoryEntity.AD_TECH_AND_MARKETING
            OrganizationCategoryGraphQL.AdultEntertainment -> OrganizationCategoryEntity.ADULT_ENTERTAINMENT
            OrganizationCategoryGraphQL.Automotive -> OrganizationCategoryEntity.AUTOMOTIVE
            OrganizationCategoryGraphQL.Communications -> OrganizationCategoryEntity.COMMUNICATIONS
            OrganizationCategoryGraphQL.CommunityAndCharity -> OrganizationCategoryEntity.COMMUNITY_AND_CHARITY
            OrganizationCategoryGraphQL.Cryptocurrency -> OrganizationCategoryEntity.CRYPTOCURRENCY
            OrganizationCategoryGraphQL.DataBroker -> OrganizationCategoryEntity.DATA_BROKER
            OrganizationCategoryGraphQL.Dating -> OrganizationCategoryEntity.DATING
            OrganizationCategoryGraphQL.Education -> OrganizationCategoryEntity.EDUCATION
            OrganizationCategoryGraphQL.Employment -> OrganizationCategoryEntity.EMPLOYMENT
            OrganizationCategoryGraphQL.EnergyAndUtilities -> OrganizationCategoryEntity.ENERGY_AND_UTILITIES
            OrganizationCategoryGraphQL.Entertainment -> OrganizationCategoryEntity.ENTERTAINMENT
            OrganizationCategoryGraphQL.Finance -> OrganizationCategoryEntity.FINANCE
            OrganizationCategoryGraphQL.FoodAndDining -> OrganizationCategoryEntity.FOOD_AND_DINING
            OrganizationCategoryGraphQL.Gambling -> OrganizationCategoryEntity.GAMBLING
            OrganizationCategoryGraphQL.Government -> OrganizationCategoryEntity.GOVERNMENT
            OrganizationCategoryGraphQL.Health -> OrganizationCategoryEntity.HEALTH
            OrganizationCategoryGraphQL.Insurance -> OrganizationCategoryEntity.INSURANCE
            OrganizationCategoryGraphQL.Military -> OrganizationCategoryEntity.MILITARY
            OrganizationCategoryGraphQL.News -> OrganizationCategoryEntity.NEWS
            OrganizationCategoryGraphQL.Other -> OrganizationCategoryEntity.OTHER
            OrganizationCategoryGraphQL.PersonalEmail -> OrganizationCategoryEntity.PERSONAL_EMAIL
            OrganizationCategoryGraphQL.PrivacyAndSecurity -> OrganizationCategoryEntity.PRIVACY_AND_SECURITY
            OrganizationCategoryGraphQL.RealEstate -> OrganizationCategoryEntity.REAL_ESTATE
            OrganizationCategoryGraphQL.ShippingAndDelivery -> OrganizationCategoryEntity.SHIPPING_AND_DELIVERY
            OrganizationCategoryGraphQL.Shopping -> OrganizationCategoryEntity.SHOPPING
            OrganizationCategoryGraphQL.SocialMedia -> OrganizationCategoryEntity.SOCIAL_MEDIA
            OrganizationCategoryGraphQL.SportsAndFitness -> OrganizationCategoryEntity.SPORTS_AND_FITNESS
            OrganizationCategoryGraphQL.Technology -> OrganizationCategoryEntity.TECHNOLOGY
            OrganizationCategoryGraphQL.Transportation -> OrganizationCategoryEntity.TRANSPORTATION
            OrganizationCategoryGraphQL.Travel -> OrganizationCategoryEntity.TRAVEL
            OrganizationCategoryGraphQL.VideoGames -> OrganizationCategoryEntity.VIDEO_GAMES
            else -> OrganizationCategoryEntity.OTHER
        }

    fun fromEntityToApi(entity: OrganizationCategoryEntity): OrganizationCategory =
        when (entity) {
            OrganizationCategoryEntity.AD_TECH_AND_MARKETING -> OrganizationCategory.AD_TECH_AND_MARKETING
            OrganizationCategoryEntity.ADULT_ENTERTAINMENT -> OrganizationCategory.ADULT_ENTERTAINMENT
            OrganizationCategoryEntity.AUTOMOTIVE -> OrganizationCategory.AUTOMOTIVE
            OrganizationCategoryEntity.COMMUNICATIONS -> OrganizationCategory.COMMUNICATIONS
            OrganizationCategoryEntity.COMMUNITY_AND_CHARITY -> OrganizationCategory.COMMUNITY_AND_CHARITY
            OrganizationCategoryEntity.CRYPTOCURRENCY -> OrganizationCategory.CRYPTOCURRENCY
            OrganizationCategoryEntity.DATA_BROKER -> OrganizationCategory.DATA_BROKER
            OrganizationCategoryEntity.DATING -> OrganizationCategory.DATING
            OrganizationCategoryEntity.EDUCATION -> OrganizationCategory.EDUCATION
            OrganizationCategoryEntity.EMPLOYMENT -> OrganizationCategory.EMPLOYMENT
            OrganizationCategoryEntity.ENERGY_AND_UTILITIES -> OrganizationCategory.ENERGY_AND_UTILITIES
            OrganizationCategoryEntity.ENTERTAINMENT -> OrganizationCategory.ENTERTAINMENT
            OrganizationCategoryEntity.FINANCE -> OrganizationCategory.FINANCE
            OrganizationCategoryEntity.FOOD_AND_DINING -> OrganizationCategory.FOOD_AND_DINING
            OrganizationCategoryEntity.GAMBLING -> OrganizationCategory.GAMBLING
            OrganizationCategoryEntity.GOVERNMENT -> OrganizationCategory.GOVERNMENT
            OrganizationCategoryEntity.HEALTH -> OrganizationCategory.HEALTH
            OrganizationCategoryEntity.INSURANCE -> OrganizationCategory.INSURANCE
            OrganizationCategoryEntity.MILITARY -> OrganizationCategory.MILITARY
            OrganizationCategoryEntity.NEWS -> OrganizationCategory.NEWS
            OrganizationCategoryEntity.OTHER -> OrganizationCategory.OTHER
            OrganizationCategoryEntity.PERSONAL_EMAIL -> OrganizationCategory.PERSONAL_EMAIL
            OrganizationCategoryEntity.PRIVACY_AND_SECURITY -> OrganizationCategory.PRIVACY_AND_SECURITY
            OrganizationCategoryEntity.REAL_ESTATE -> OrganizationCategory.REAL_ESTATE
            OrganizationCategoryEntity.SHIPPING_AND_DELIVERY -> OrganizationCategory.SHIPPING_AND_DELIVERY
            OrganizationCategoryEntity.SHOPPING -> OrganizationCategory.SHOPPING
            OrganizationCategoryEntity.SOCIAL_MEDIA -> OrganizationCategory.SOCIAL_MEDIA
            OrganizationCategoryEntity.SPORTS_AND_FITNESS -> OrganizationCategory.SPORTS_AND_FITNESS
            OrganizationCategoryEntity.TECHNOLOGY -> OrganizationCategory.TECHNOLOGY
            OrganizationCategoryEntity.TRANSPORTATION -> OrganizationCategory.TRANSPORTATION
            OrganizationCategoryEntity.TRAVEL -> OrganizationCategory.TRAVEL
            OrganizationCategoryEntity.VIDEO_GAMES -> OrganizationCategory.VIDEO_GAMES
            OrganizationCategoryEntity.UNKNOWN -> OrganizationCategory.UNKNOWN
        }
}
