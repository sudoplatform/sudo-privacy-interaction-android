/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.organizationAnalysis.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisDomainResolutionEntity
import com.sudoplatform.sudoprivacyinteraction.types.OrganizationAnalysisDomainResolution
import io.kotlintest.shouldBe
import org.junit.Test
import com.sudoplatform.sudoprivacyinteraction.graphql.type.OrganizationAnalysisDomainResolution as OrganizationAnalysisDomainResolutionGraphQL

/**
 * Test the correct operation of [OrganizationAnalysisDomainResolutionTransformer].
 */
class OrganizationAnalysisDomainResolutionTransformerTest {
    @Test
    fun `fromApiToEntity() should transform every value`() {
        OrganizationAnalysisDomainResolutionTransformer.fromApiToEntity(
            OrganizationAnalysisDomainResolution.REGISTRABLE,
        ) shouldBe OrganizationAnalysisDomainResolutionEntity.REGISTRABLE

        OrganizationAnalysisDomainResolutionTransformer.fromApiToEntity(
            OrganizationAnalysisDomainResolution.EXACT,
        ) shouldBe OrganizationAnalysisDomainResolutionEntity.EXACT
    }

    @Test
    fun `fromEntityToGraphQL() should transform every value`() {
        OrganizationAnalysisDomainResolutionTransformer.fromEntityToGraphQL(
            OrganizationAnalysisDomainResolutionEntity.REGISTRABLE,
        ) shouldBe OrganizationAnalysisDomainResolutionGraphQL.REGISTRABLE

        OrganizationAnalysisDomainResolutionTransformer.fromEntityToGraphQL(
            OrganizationAnalysisDomainResolutionEntity.EXACT,
        ) shouldBe OrganizationAnalysisDomainResolutionGraphQL.EXACT
    }
}
