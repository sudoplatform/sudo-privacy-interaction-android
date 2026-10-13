/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers

import com.sudoplatform.sudoprivacyinteraction.graphql.OnAnalysisResultUpdateSubscription
import com.sudoplatform.sudoprivacyinteraction.internal.util.toDate
import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResultUpdate

/**
 * Transformer responsible for transforming the lightweight analysis result update subscription
 * payload into the public [AnalysisResultUpdate] type.
 */
internal object AnalysisResultUpdateTransformer {
    /**
     * Transform the [OnAnalysisResultUpdateSubscription.OnAnalysisResultUpdate] GraphQL type to its
     * public [AnalysisResultUpdate] type.
     */
    fun fromGraphQLToApi(data: OnAnalysisResultUpdateSubscription.OnAnalysisResultUpdate): AnalysisResultUpdate =
        AnalysisResultUpdate(
            id = data.id,
            owner = data.owner,
            virtualPresenceId = data.virtualPresenceId,
            dataHolderIdentifier = data.dataHolderIdentifier,
            status =
                AnalysisResultStatusTransformer.fromEntityToApi(
                    AnalysisResultStatusTransformer.fromGraphQLToEntity(data.status),
                ),
            lastAnalyzedAt = data.lastAnalyzedAtEpochMs.toDate(),
            version = data.version,
            createdAt = data.createdAtEpochMs.toDate(),
            updatedAt = data.updatedAtEpochMs.toDate(),
        )
}
