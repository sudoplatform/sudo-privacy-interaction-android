/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers

import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.data.DataFactory
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.OrganizationCategoryEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.SignalValueEntity
import com.sudoplatform.sudoprivacyinteraction.types.OrganizationCategory
import com.sudoplatform.sudoprivacyinteraction.types.SignalValue
import io.kotlintest.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.sudoplatform.sudoprivacyinteraction.graphql.type.OrganizationCategory as OrganizationCategoryGraphQL

/**
 * Tests the analysis result transformers for the newly mirrored service features: the
 * `AdultEntertainment` organization category and the `supportsOptOutOfSale` capability signal.
 */
@RunWith(RobolectricTestRunner::class)
class AnalysisResultCapabilityAndCategoryTransformerTest : BaseTests() {
    @Test
    fun `organization category transformer maps AdultEntertainment in both directions`() {
        OrganizationCategoryTransformer.fromGraphQLToEntity(
            OrganizationCategoryGraphQL.AdultEntertainment,
        ) shouldBe OrganizationCategoryEntity.ADULT_ENTERTAINMENT

        OrganizationCategoryTransformer.fromEntityToApi(
            OrganizationCategoryEntity.ADULT_ENTERTAINMENT,
        ) shouldBe OrganizationCategory.ADULT_ENTERTAINMENT
    }

    @Test
    fun `fromGraphQLToEntity maps supportsOptOutOfSale`() {
        val entity = AnalysisResultDataTransformer.fromGraphQLToEntity(DataFactory.getAnalysisResultDataFragment())

        // The GraphQL fixture supplies NO for the capability signal.
        entity.capabilities.supportsOptOutOfSale shouldBe SignalValueEntity.NO
    }

    @Test
    fun `fromEntityToApi exposes supportsOptOutOfSale and populates the deprecated alias from it`() {
        val entity = EntityDataFactory.getAnalysisResultDataEntity()

        val api = AnalysisResultDataTransformer.fromEntityToApi(entity)

        api.capabilities.supportsOptOutOfSale shouldBe SignalValue.NO

        // The deprecated field is populated from `supportsOptOutOfSale`.
        @Suppress("DEPRECATION")
        api.capabilities.sellsPersonalInformation shouldBe api.capabilities.supportsOptOutOfSale
    }

    @Test
    fun `riskIndicators retains its own sellsPersonalInformation signal`() {
        val entity = EntityDataFactory.getAnalysisResultDataEntity()

        val api = AnalysisResultDataTransformer.fromEntityToApi(entity)

        api.riskIndicators.sellsPersonalInformation shouldBe
            SignalValueTransformer.fromEntityToApi(entity.riskIndicators.sellsPersonalInformation)
    }
}
