/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.inputs.ScanOptionsEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.RescanVirtualPresenceInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService

/**
 * Input for [RescanVirtualPresenceUseCase].
 */
internal data class RescanVirtualPresenceUseCaseInput(
    val id: String,
    val options: ScanOptionsEntity? = null,
)

/**
 * Application business logic for rescanning a virtual presence.
 */
internal class RescanVirtualPresenceUseCase(
    private val virtualPresenceService: VirtualPresenceService,
    private val logger: Logger,
) {
    suspend fun execute(input: RescanVirtualPresenceUseCaseInput): VirtualPresenceEntity {
        logger.debug(this::class.java.simpleName)
        return virtualPresenceService.rescan(
            RescanVirtualPresenceInput(
                id = input.id,
                options = input.options,
            ),
        )
    }
}
