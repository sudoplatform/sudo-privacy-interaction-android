/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ListVirtualPresencesInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ListVirtualPresencesOutput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService

/**
 * Input for [ListVirtualPresencesUseCase].
 */
internal data class ListVirtualPresencesUseCaseInput(
    val limit: Int? = null,
    val nextToken: String? = null,
)

/**
 * Application business logic for listing all virtual presences for this client.
 */
internal class ListVirtualPresencesUseCase(
    private val virtualPresenceService: VirtualPresenceService,
    private val logger: Logger,
) {
    suspend fun execute(input: ListVirtualPresencesUseCaseInput): ListVirtualPresencesOutput {
        logger.debug(this::class.java.simpleName)
        return virtualPresenceService.list(
            ListVirtualPresencesInput(
                limit = input.limit,
                nextToken = input.nextToken,
            ),
        )
    }
}
