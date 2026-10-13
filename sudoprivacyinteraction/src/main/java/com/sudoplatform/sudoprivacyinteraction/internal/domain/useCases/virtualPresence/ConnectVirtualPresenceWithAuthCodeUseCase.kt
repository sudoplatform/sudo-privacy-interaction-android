/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.RelationshipProviderEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.VirtualPresenceEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.AuthCodeInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ConnectVirtualPresenceInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService

/**
 * Input for [ConnectVirtualPresenceWithAuthCodeUseCase].
 */
internal data class ConnectVirtualPresenceWithAuthCodeUseCaseInput(
    val authCode: String,
    val redirectUri: String? = null,
    val relationshipProvider: RelationshipProviderEntity? = null,
)

/**
 * Application business logic for connecting a virtual presence using an authorization code.
 */
internal class ConnectVirtualPresenceWithAuthCodeUseCase(
    private val virtualPresenceService: VirtualPresenceService,
    private val logger: Logger,
) {
    suspend fun execute(input: ConnectVirtualPresenceWithAuthCodeUseCaseInput): VirtualPresenceEntity {
        logger.debug(this::class.java.simpleName)
        return virtualPresenceService.connect(
            ConnectVirtualPresenceInput(
                authCode =
                    AuthCodeInput(
                        authCode = input.authCode,
                        redirectUri = input.redirectUri,
                    ),
                relationshipProvider = input.relationshipProvider,
            ),
        )
    }
}
