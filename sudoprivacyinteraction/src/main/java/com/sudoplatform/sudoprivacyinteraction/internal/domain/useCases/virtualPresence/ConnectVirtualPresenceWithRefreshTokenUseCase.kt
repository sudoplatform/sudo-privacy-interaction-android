/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.RelationshipProviderEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.VirtualPresenceEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ConnectVirtualPresenceInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.RefreshTokenInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService

/**
 * Input for [ConnectVirtualPresenceWithRefreshTokenUseCase].
 */
internal data class ConnectVirtualPresenceWithRefreshTokenUseCaseInput(
    val refreshToken: String,
    val providerIdentity: String,
    val scopes: List<String>? = null,
    val expiresInEpochMs: Double? = null,
    val relationshipProvider: RelationshipProviderEntity? = null,
)

/**
 * Application business logic for connecting a virtual presence with a pre-obtained refresh token.
 */
internal class ConnectVirtualPresenceWithRefreshTokenUseCase(
    private val virtualPresenceService: VirtualPresenceService,
    private val logger: Logger,
) {
    suspend fun execute(input: ConnectVirtualPresenceWithRefreshTokenUseCaseInput): VirtualPresenceEntity {
        logger.debug(this::class.java.simpleName)
        return virtualPresenceService.connect(
            ConnectVirtualPresenceInput(
                refreshToken =
                    RefreshTokenInput(
                        refreshToken = input.refreshToken,
                        providerIdentity = input.providerIdentity,
                        scopes = input.scopes,
                        expiresInEpochMs = input.expiresInEpochMs,
                    ),
                relationshipProvider = input.relationshipProvider,
            ),
        )
    }
}
