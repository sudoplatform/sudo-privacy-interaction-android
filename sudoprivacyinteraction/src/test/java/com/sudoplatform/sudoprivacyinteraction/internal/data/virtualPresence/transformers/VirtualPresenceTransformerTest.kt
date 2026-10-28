/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence.transformers

import com.sudoplatform.sudoprivacyinteraction.BaseTests
import com.sudoplatform.sudoprivacyinteraction.data.DataFactory
import com.sudoplatform.sudoprivacyinteraction.data.EntityDataFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.DataHolderCountEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.RelationshipProviderEntity
import com.sudoplatform.sudoprivacyinteraction.types.DataHolderCount
import com.sudoplatform.sudoprivacyinteraction.types.RelationshipProvider
import io.kotlintest.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.sudoplatform.sudoprivacyinteraction.graphql.fragment.VirtualPresence as VirtualPresenceFragment
import com.sudoplatform.sudoprivacyinteraction.graphql.type.RelationshipProvider as RelationshipProviderGraphQL

/**
 * Tests the [VirtualPresenceTransformer], covering the `relationshipProvider` and
 * `dataHolderCount` read paths (GraphQL fragment -> entity -> public type).
 */
@RunWith(RobolectricTestRunner::class)
class VirtualPresenceTransformerTest : BaseTests() {
    @Test
    fun `fromGraphQLToEntity maps GMAIL_PROVIDER relationshipProvider`() {
        val fragment =
            DataFactory.getVirtualPresenceFragment(
                relationshipProvider = RelationshipProviderGraphQL.GMAIL_PROVIDER,
            )

        val entity = VirtualPresenceTransformer.fromGraphQLToEntity(fragment)

        entity.relationshipProvider shouldBe RelationshipProviderEntity.GMAIL_PROVIDER
    }

    @Test
    fun `fromGraphQLToEntity maps a null relationshipProvider to null`() {
        val fragment = DataFactory.getVirtualPresenceFragment(relationshipProvider = null)

        val entity = VirtualPresenceTransformer.fromGraphQLToEntity(fragment)

        entity.relationshipProvider shouldBe null
    }

    @Test
    fun `fromEntityToApi surfaces GMAIL_PROVIDER relationshipProvider to consumers`() {
        val entity =
            EntityDataFactory.getVirtualPresenceEntity(
                relationshipProvider = RelationshipProviderEntity.GMAIL_PROVIDER,
            )

        val api = VirtualPresenceTransformer.fromEntityToApi(entity)

        api.relationshipProvider shouldBe RelationshipProvider.GMAIL_PROVIDER
    }

    @Test
    fun `full read path preserves GMAIL_PROVIDER from GraphQL fragment to public type`() {
        val fragment =
            DataFactory.getVirtualPresenceFragment(
                relationshipProvider = RelationshipProviderGraphQL.GMAIL_PROVIDER,
            )

        val api =
            VirtualPresenceTransformer.fromEntityToApi(
                VirtualPresenceTransformer.fromGraphQLToEntity(fragment),
            )

        api.relationshipProvider shouldBe RelationshipProvider.GMAIL_PROVIDER
    }

    @Test
    fun `fromGraphQLToEntity maps dataHolderCount`() {
        val fragment =
            DataFactory.getVirtualPresenceFragment(
                dataHolderCount =
                    VirtualPresenceFragment.DataHolderCount(
                        count = 9,
                        monitored = 2,
                        initiatedActions = 3,
                        completedActions = 4,
                        archived = 5,
                    ),
            )

        val entity = VirtualPresenceTransformer.fromGraphQLToEntity(fragment)

        entity.dataHolderCount shouldBe
            DataHolderCountEntity(
                count = 9,
                monitored = 2,
                initiatedActions = 3,
                completedActions = 4,
                archived = 5,
            )
    }

    @Test
    fun `fromEntityToApi maps dataHolderCount`() {
        val entity =
            EntityDataFactory.getVirtualPresenceEntity(
                dataHolderCount =
                    DataHolderCountEntity(
                        count = 7,
                        monitored = 1,
                        initiatedActions = 2,
                        completedActions = 4,
                        archived = 8,
                    ),
            )

        val api = VirtualPresenceTransformer.fromEntityToApi(entity)

        api.dataHolderCount shouldBe
            DataHolderCount(
                count = 7,
                monitored = 1,
                initiatedActions = 2,
                completedActions = 4,
                archived = 8,
            )
    }

    @Test
    fun `full read path preserves dataHolderCount from GraphQL fragment to public type`() {
        val fragment =
            DataFactory.getVirtualPresenceFragment(
                dataHolderCount =
                    VirtualPresenceFragment.DataHolderCount(
                        count = 9,
                        monitored = 2,
                        initiatedActions = 3,
                        completedActions = 4,
                        archived = 5,
                    ),
            )

        val api =
            VirtualPresenceTransformer.fromEntityToApi(
                VirtualPresenceTransformer.fromGraphQLToEntity(fragment),
            )

        api.dataHolderCount shouldBe
            DataHolderCount(
                count = 9,
                monitored = 2,
                initiatedActions = 3,
                completedActions = 4,
                archived = 5,
            )
    }
}
