/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.VirtualPresenceEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService

/**
 * Application business logic for disconnecting a virtual presence.
 */
internal class DisconnectVirtualPresenceUseCase(
    private val virtualPresenceService: VirtualPresenceService,
    private val logger: Logger,
) {
    suspend fun execute(id: String): VirtualPresenceEntity {
        logger.debug(this::class.java.simpleName)
        return virtualPresenceService.disconnect(id)
    }
}
