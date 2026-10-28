/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.dataHolder.transformers

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderProtectionStateEntity
import com.sudoplatform.sudoprivacyinteraction.types.DataHolderProtectionState
import io.kotlintest.shouldBe
import org.junit.Test
import com.sudoplatform.sudoprivacyinteraction.graphql.type.DataHolderProtectionState as DataHolderProtectionStateGraphQL

/**
 * Test the correct operation of [DataHolderProtectionStateTransformer].
 */
class DataHolderProtectionStateTransformerTest {
    @Test
    fun `fromGraphQLToEntity() should transform ARCHIVED`() {
        DataHolderProtectionStateTransformer.fromGraphQLToEntity(
            DataHolderProtectionStateGraphQL.ARCHIVED,
        ) shouldBe DataHolderProtectionStateEntity.ARCHIVED
    }

    @Test
    fun `fromEntityToApi() should transform ARCHIVED`() {
        DataHolderProtectionStateTransformer.fromEntityToApi(
            DataHolderProtectionStateEntity.ARCHIVED,
        ) shouldBe DataHolderProtectionState.ARCHIVED
    }
}
