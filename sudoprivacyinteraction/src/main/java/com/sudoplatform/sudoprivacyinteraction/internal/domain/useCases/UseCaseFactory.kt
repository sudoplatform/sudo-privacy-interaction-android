/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases

import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.configuration.ProviderConfigurationService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action.ConfirmActionOutcomeUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action.GetActionUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action.InitiateActionUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action.ListAvailableActionsUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.analysisResult.GetAnalysisResultUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.analysisResult.ListAnalysisResultsUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.configuration.GetProviderConfigurationUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder.ArchiveDataHolderUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder.GetDataHolderUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder.ListDataHolderScanSummariesUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder.ListDataHoldersUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder.UnarchiveDataHolderUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.organizationAnalysis.GetOrganizationAnalysisUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence.ConnectVirtualPresenceWithAuthCodeUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence.ConnectVirtualPresenceWithRefreshTokenUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence.DisconnectVirtualPresenceUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence.ListVirtualPresencesUseCase
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence.RescanVirtualPresenceUseCase

/**
 * Factory responsible for constructing the client's use cases, wiring each one with the
 * domain services it depends on.
 */
internal interface UseCaseFactory {
    fun createGetProviderConfigurationUseCase(): GetProviderConfigurationUseCase

    fun createConnectVirtualPresenceWithAuthCodeUseCase(): ConnectVirtualPresenceWithAuthCodeUseCase

    fun createConnectVirtualPresenceWithRefreshTokenUseCase(): ConnectVirtualPresenceWithRefreshTokenUseCase

    fun createDisconnectVirtualPresenceUseCase(): DisconnectVirtualPresenceUseCase

    fun createRescanVirtualPresenceUseCase(): RescanVirtualPresenceUseCase

    fun createListVirtualPresencesUseCase(): ListVirtualPresencesUseCase

    fun createArchiveDataHolderUseCase(): ArchiveDataHolderUseCase

    fun createUnarchiveDataHolderUseCase(): UnarchiveDataHolderUseCase

    fun createGetDataHolderUseCase(): GetDataHolderUseCase

    fun createListDataHoldersUseCase(): ListDataHoldersUseCase

    fun createListDataHolderScanSummariesUseCase(): ListDataHolderScanSummariesUseCase

    fun createGetAnalysisResultUseCase(): GetAnalysisResultUseCase

    fun createListAnalysisResultsUseCase(): ListAnalysisResultsUseCase

    fun createGetOrganizationAnalysisUseCase(): GetOrganizationAnalysisUseCase

    fun createListAvailableActionsUseCase(): ListAvailableActionsUseCase

    fun createInitiateActionUseCase(): InitiateActionUseCase

    fun createConfirmActionOutcomeUseCase(): ConfirmActionOutcomeUseCase

    fun createGetActionUseCase(): GetActionUseCase
}

internal class DefaultUseCaseFactory(
    private val virtualPresenceService: VirtualPresenceService,
    private val providerConfigurationService: ProviderConfigurationService,
    private val dataHolderService: DataHolderService,
    private val analysisResultService: AnalysisResultService,
    private val organizationAnalysisService: OrganizationAnalysisService,
    private val actionService: ActionService,
    private val logger: Logger,
) : UseCaseFactory {
    override fun createGetProviderConfigurationUseCase(): GetProviderConfigurationUseCase =
        GetProviderConfigurationUseCase(
            providerConfigurationService = providerConfigurationService,
            logger = logger,
        )

    override fun createConnectVirtualPresenceWithAuthCodeUseCase(): ConnectVirtualPresenceWithAuthCodeUseCase =
        ConnectVirtualPresenceWithAuthCodeUseCase(
            virtualPresenceService = virtualPresenceService,
            logger = logger,
        )

    override fun createConnectVirtualPresenceWithRefreshTokenUseCase(): ConnectVirtualPresenceWithRefreshTokenUseCase =
        ConnectVirtualPresenceWithRefreshTokenUseCase(
            virtualPresenceService = virtualPresenceService,
            logger = logger,
        )

    override fun createDisconnectVirtualPresenceUseCase(): DisconnectVirtualPresenceUseCase =
        DisconnectVirtualPresenceUseCase(
            virtualPresenceService = virtualPresenceService,
            logger = logger,
        )

    override fun createListVirtualPresencesUseCase(): ListVirtualPresencesUseCase =
        ListVirtualPresencesUseCase(
            virtualPresenceService = virtualPresenceService,
            logger = logger,
        )

    override fun createRescanVirtualPresenceUseCase(): RescanVirtualPresenceUseCase =
        RescanVirtualPresenceUseCase(
            virtualPresenceService = virtualPresenceService,
            logger = logger,
        )

    override fun createArchiveDataHolderUseCase(): ArchiveDataHolderUseCase =
        ArchiveDataHolderUseCase(
            dataHolderService = dataHolderService,
            logger = logger,
        )

    override fun createUnarchiveDataHolderUseCase(): UnarchiveDataHolderUseCase =
        UnarchiveDataHolderUseCase(
            dataHolderService = dataHolderService,
            logger = logger,
        )

    override fun createGetDataHolderUseCase(): GetDataHolderUseCase =
        GetDataHolderUseCase(
            dataHolderService = dataHolderService,
            logger = logger,
        )

    override fun createListDataHoldersUseCase(): ListDataHoldersUseCase =
        ListDataHoldersUseCase(
            dataHolderService = dataHolderService,
            logger = logger,
        )

    override fun createListDataHolderScanSummariesUseCase(): ListDataHolderScanSummariesUseCase =
        ListDataHolderScanSummariesUseCase(
            dataHolderService = dataHolderService,
            logger = logger,
        )

    override fun createGetAnalysisResultUseCase(): GetAnalysisResultUseCase =
        GetAnalysisResultUseCase(
            analysisResultService = analysisResultService,
            logger = logger,
        )

    override fun createListAnalysisResultsUseCase(): ListAnalysisResultsUseCase =
        ListAnalysisResultsUseCase(
            analysisResultService = analysisResultService,
            logger = logger,
        )

    override fun createGetOrganizationAnalysisUseCase(): GetOrganizationAnalysisUseCase =
        GetOrganizationAnalysisUseCase(
            organizationAnalysisService = organizationAnalysisService,
            logger = logger,
        )

    override fun createListAvailableActionsUseCase(): ListAvailableActionsUseCase =
        ListAvailableActionsUseCase(
            actionService = actionService,
            logger = logger,
        )

    override fun createInitiateActionUseCase(): InitiateActionUseCase =
        InitiateActionUseCase(
            actionService = actionService,
            logger = logger,
        )

    override fun createConfirmActionOutcomeUseCase(): ConfirmActionOutcomeUseCase =
        ConfirmActionOutcomeUseCase(
            actionService = actionService,
            logger = logger,
        )

    override fun createGetActionUseCase(): GetActionUseCase =
        GetActionUseCase(
            actionService = actionService,
            logger = logger,
        )
}
