/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.DataCategoryEntity
import com.sudoplatform.sudoprivacyinteraction.types.DataCategory
import com.sudoplatform.sudoprivacyinteraction.graphql.type.DataCategory as DataCategoryGraphQL

/**
 * Transformer between the [DataCategoryGraphQL] GraphQL enum, the [DataCategoryEntity] entity
 * enum, and the public [DataCategory] type.
 */
internal object DataCategoryTransformer {
    fun fromGraphQLToEntity(data: DataCategoryGraphQL): DataCategoryEntity =
        when (data) {
            DataCategoryGraphQL.ACCOUNT_PROFILE -> DataCategoryEntity.ACCOUNT_PROFILE
            DataCategoryGraphQL.AUTHENTICATION -> DataCategoryEntity.AUTHENTICATION
            DataCategoryGraphQL.BACKGROUND -> DataCategoryEntity.BACKGROUND
            DataCategoryGraphQL.BIOMETRIC -> DataCategoryEntity.BIOMETRIC
            DataCategoryGraphQL.COMMUNICATION -> DataCategoryEntity.COMMUNICATION
            DataCategoryGraphQL.CONTACTS -> DataCategoryEntity.CONTACTS
            DataCategoryGraphQL.DEMOGRAPHIC -> DataCategoryEntity.DEMOGRAPHIC
            DataCategoryGraphQL.DEVICE_IDENTIFIER -> DataCategoryEntity.DEVICE_IDENTIFIER
            DataCategoryGraphQL.FINANCIAL -> DataCategoryEntity.FINANCIAL
            DataCategoryGraphQL.GOVERNMENT_ISSUED -> DataCategoryEntity.GOVERNMENT_ISSUED
            DataCategoryGraphQL.HEALTH -> DataCategoryEntity.HEALTH
            DataCategoryGraphQL.INFERENCES -> DataCategoryEntity.INFERENCES
            DataCategoryGraphQL.LOCATION -> DataCategoryEntity.LOCATION
            DataCategoryGraphQL.OTHER -> DataCategoryEntity.OTHER
            DataCategoryGraphQL.PII -> DataCategoryEntity.PII
            DataCategoryGraphQL.THIRD_PARTY_DATA -> DataCategoryEntity.THIRD_PARTY_DATA
            DataCategoryGraphQL.USAGE -> DataCategoryEntity.USAGE
            else -> DataCategoryEntity.OTHER
        }

    fun fromEntityToApi(entity: DataCategoryEntity): DataCategory =
        when (entity) {
            DataCategoryEntity.ACCOUNT_PROFILE -> DataCategory.ACCOUNT_PROFILE
            DataCategoryEntity.AUTHENTICATION -> DataCategory.AUTHENTICATION
            DataCategoryEntity.BACKGROUND -> DataCategory.BACKGROUND
            DataCategoryEntity.BIOMETRIC -> DataCategory.BIOMETRIC
            DataCategoryEntity.COMMUNICATION -> DataCategory.COMMUNICATION
            DataCategoryEntity.CONTACTS -> DataCategory.CONTACTS
            DataCategoryEntity.DEMOGRAPHIC -> DataCategory.DEMOGRAPHIC
            DataCategoryEntity.DEVICE_IDENTIFIER -> DataCategory.DEVICE_IDENTIFIER
            DataCategoryEntity.FINANCIAL -> DataCategory.FINANCIAL
            DataCategoryEntity.GOVERNMENT_ISSUED -> DataCategory.GOVERNMENT_ISSUED
            DataCategoryEntity.HEALTH -> DataCategory.HEALTH
            DataCategoryEntity.INFERENCES -> DataCategory.INFERENCES
            DataCategoryEntity.LOCATION -> DataCategory.LOCATION
            DataCategoryEntity.OTHER -> DataCategory.OTHER
            DataCategoryEntity.PII -> DataCategory.PII
            DataCategoryEntity.THIRD_PARTY_DATA -> DataCategory.THIRD_PARTY_DATA
            DataCategoryEntity.USAGE -> DataCategory.USAGE
            DataCategoryEntity.UNKNOWN -> DataCategory.UNKNOWN
        }
}
