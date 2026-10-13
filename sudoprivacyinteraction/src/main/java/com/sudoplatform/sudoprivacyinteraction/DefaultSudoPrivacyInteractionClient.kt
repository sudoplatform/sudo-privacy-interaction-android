/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import android.content.Context
import com.sudoplatform.sudologging.AndroidUtilsLogDriver
import com.sudoplatform.sudologging.LogLevel
import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.internal.data.action.GraphQLActionService
import com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers.ActionFulfilmentMethodTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers.ActionIntentTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers.ActionTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers.AvailableActionTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.GraphQLAnalysisResultService
import com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers.AnalysisResultTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.dataHolder.GraphQLDataHolderService
import com.sudoplatform.sudoprivacyinteraction.internal.data.dataHolder.transformers.DataHolderScanSummaryTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.dataHolder.transformers.DataHolderTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.organizationAnalysis.GraphQLOrganizationAnalysisService
import com.sudoplatform.sudoprivacyinteraction.internal.data.organizationAnalysis.transformers.OrganizationAnalysisModeTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.organizationAnalysis.transformers.OrganizationAnalysisTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.providerConfiguration.GraphQLProviderConfigurationService
import com.sudoplatform.sudoprivacyinteraction.internal.data.providerConfiguration.transformers.ProviderConfigurationTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence.GraphQLVirtualPresenceService
import com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence.transformers.RelationshipProviderTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence.transformers.VirtualPresenceTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.action.ActionService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.analysisResult.AnalysisResultService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.configuration.ProviderConfigurationService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.dataHolder.DataHolderService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.inputs.ScanOptionsEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.organizationAnalysis.OrganizationAnalysisService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.VirtualPresenceService
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.DefaultUseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.UseCaseFactory
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action.InitiateActionUseCaseInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.action.ListAvailableActionsUseCaseInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.analysisResult.ListAnalysisResultsUseCaseInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder.ListDataHolderScanSummariesUseCaseInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.dataHolder.ListDataHoldersUseCaseInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.organizationAnalysis.GetOrganizationAnalysisUseCaseInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence.ConnectVirtualPresenceWithAuthCodeUseCaseInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence.ConnectVirtualPresenceWithRefreshTokenUseCaseInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence.ListVirtualPresencesUseCaseInput
import com.sudoplatform.sudoprivacyinteraction.internal.domain.useCases.virtualPresence.RescanVirtualPresenceUseCaseInput
import com.sudoplatform.sudoprivacyinteraction.logging.LogConstants
import com.sudoplatform.sudoprivacyinteraction.subscription.ActionSubscriber
import com.sudoplatform.sudoprivacyinteraction.subscription.AnalysisResultSubscriber
import com.sudoplatform.sudoprivacyinteraction.subscription.DataHolderSubscriber
import com.sudoplatform.sudoprivacyinteraction.subscription.SubscriptionService
import com.sudoplatform.sudoprivacyinteraction.subscription.VirtualPresenceSubscriber
import com.sudoplatform.sudoprivacyinteraction.types.Action
import com.sudoplatform.sudoprivacyinteraction.types.AnalysisResult
import com.sudoplatform.sudoprivacyinteraction.types.AvailableAction
import com.sudoplatform.sudoprivacyinteraction.types.DataHolder
import com.sudoplatform.sudoprivacyinteraction.types.DataHolderScanSummary
import com.sudoplatform.sudoprivacyinteraction.types.ListOutput
import com.sudoplatform.sudoprivacyinteraction.types.OrganizationAnalysis
import com.sudoplatform.sudoprivacyinteraction.types.ProviderConfiguration
import com.sudoplatform.sudoprivacyinteraction.types.VirtualPresence
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ConnectVirtualPresenceWithAuthCodeInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ConnectVirtualPresenceWithRefreshTokenInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.GetDataHolderOptions
import com.sudoplatform.sudoprivacyinteraction.types.inputs.GetOrganizationAnalysisInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.InitiateActionInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListAnalysisResultsInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListAvailableActionsInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListDataHolderScanSummariesInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListDataHoldersInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.ListVirtualPresencesInput
import com.sudoplatform.sudoprivacyinteraction.types.inputs.RescanVirtualPresenceInput
import com.sudoplatform.sudouser.SignInGuard
import com.sudoplatform.sudouser.SudoPlatformSignInCallback
import com.sudoplatform.sudouser.SudoUserClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Default implementation of the [SudoPrivacyInteractionClient] interface.
 *
 * @property context [Context] Application context.
 * @property apiClient [ApiClient] GraphQL client used to make service requests.
 * @property sudoUserClient [SudoUserClient] Used to determine ownership and sign in state.
 * @property logger [Logger] Errors and diagnostic information are logged here.
 */
internal class DefaultSudoPrivacyInteractionClient(
    private val context: Context,
    private val apiClient: ApiClient,
    private val sudoUserClient: SudoUserClient,
    private val logger: Logger =
        Logger(LogConstants.SUDOLOG_TAG, AndroidUtilsLogDriver(LogLevel.INFO)),
    private val virtualPresenceService: VirtualPresenceService =
        GraphQLVirtualPresenceService(apiClient, logger),
    private val providerConfigurationService: ProviderConfigurationService =
        GraphQLProviderConfigurationService(apiClient, logger),
    private val dataHolderService: DataHolderService =
        GraphQLDataHolderService(apiClient, logger),
    private val analysisResultService: AnalysisResultService =
        GraphQLAnalysisResultService(apiClient, logger),
    private val organizationAnalysisService: OrganizationAnalysisService =
        GraphQLOrganizationAnalysisService(apiClient, logger),
    private val actionService: ActionService =
        GraphQLActionService(apiClient, logger),
    private val subscriptionService: SubscriptionService =
        SubscriptionService(apiClient, sudoUserClient, logger),
    private val useCaseFactory: UseCaseFactory =
        DefaultUseCaseFactory(
            virtualPresenceService = virtualPresenceService,
            providerConfigurationService = providerConfigurationService,
            dataHolderService = dataHolderService,
            analysisResultService = analysisResultService,
            organizationAnalysisService = organizationAnalysisService,
            actionService = actionService,
            logger = logger,
        ),
    signInGuard: SignInGuard? = null,
) : SudoPrivacyInteractionClient {
    /**
     * Mutex for thread-safe access to the sign-in callback.
     */
    private val callbackMutex = Mutex()

    /**
     * Threadsafe container for optional callback to be invoked when operations are attempted while not signed in.
     */
    private var signInGuard = signInGuard ?: SignInGuard(sudoUserClient)

    /**
     * Checksum's for each file are generated and are used to create a checksum that is used when
     * publishing to maven central. In order to retry a failed publish without needing to change any
     * functionality, we need a way to generate a different checksum for the source code. We can
     * change the value of this property which will generate a different checksum for publishing
     * and allow us to retry. The value of `version` doesn't need to be kept up-to-date with the
     * version of the code.
     */
    private val version: String = "1.0.0"

    override suspend fun setSignInCallback(callback: SudoPlatformSignInCallback?) {
        callbackMutex.withLock {
            signInGuard.setCallback(callback)
        }
    }

    /**
     * Checks if the user is signed in and invokes the callback if needed.
     * Only performs the check if a callback is configured.
     *
     * This method implements the automatic sign-in checking mechanism:
     * If no callback is set, takes no action
     * If callback is set, checks if user is signed in using sudoUserClient.getSubject()
     * If user is not signed in, invokes the callback
     * Any exceptions from the callback are propagated to the caller
     *
     * @throws Exception Any exception thrown by the sign-in callback is propagated.
     */
    private suspend fun ensureSignedIn() {
        signInGuard.ensureSignedIn()
    }

    override suspend fun getProviderConfiguration(): List<ProviderConfiguration> {
        logger.debug("getProviderConfiguration")
        ensureSignedIn()
        val useCase = useCaseFactory.createGetProviderConfigurationUseCase()
        val result = useCase.execute()
        return result.map { ProviderConfigurationTransformer.fromEntityToApi(it) }
    }

    override suspend fun connectVirtualPresenceWithAuthCode(input: ConnectVirtualPresenceWithAuthCodeInput): VirtualPresence {
        logger.debug("connectVirtualPresenceWithAuthCode")
        ensureSignedIn()
        val useCase = useCaseFactory.createConnectVirtualPresenceWithAuthCodeUseCase()
        val relationshipProvider =
            input.relationshipProvider?.let {
                RelationshipProviderTransformer.fromApiToEntity(it)
            }
        val input =
            ConnectVirtualPresenceWithAuthCodeUseCaseInput(
                authCode = input.authCode,
                redirectUri = input.redirectUri,
                relationshipProvider = relationshipProvider,
            )
        val result = useCase.execute(input)
        return VirtualPresenceTransformer.fromEntityToApi(result)
    }

    override suspend fun connectVirtualPresenceWithRefreshToken(input: ConnectVirtualPresenceWithRefreshTokenInput): VirtualPresence {
        logger.debug("connectVirtualPresenceWithRefreshToken")
        ensureSignedIn()
        val useCase = useCaseFactory.createConnectVirtualPresenceWithRefreshTokenUseCase()
        val relationshipProvider =
            input.relationshipProvider?.let {
                RelationshipProviderTransformer.fromApiToEntity(it)
            }
        val input =
            ConnectVirtualPresenceWithRefreshTokenUseCaseInput(
                refreshToken = input.refreshToken,
                providerIdentity = input.providerIdentity,
                scopes = input.scopes,
                expiresInEpochMs = input.expiresInEpochMs,
                relationshipProvider = relationshipProvider,
            )
        val result = useCase.execute(input)
        return VirtualPresenceTransformer.fromEntityToApi(result)
    }

    override suspend fun disconnectVirtualPresence(id: String): VirtualPresence {
        logger.debug("disconnectVirtualPresence")
        ensureSignedIn()
        val useCase = useCaseFactory.createDisconnectVirtualPresenceUseCase()
        val result = useCase.execute(id)
        return VirtualPresenceTransformer.fromEntityToApi(result)
    }

    override suspend fun rescanVirtualPresence(input: RescanVirtualPresenceInput): VirtualPresence {
        logger.debug("rescanVirtualPresence")
        ensureSignedIn()
        val useCase = useCaseFactory.createRescanVirtualPresenceUseCase()
        val result =
            useCase.execute(
                RescanVirtualPresenceUseCaseInput(
                    id = input.id,
                    options =
                        input.options?.let {
                            ScanOptionsEntity(
                                maximumItemsProcessed = it.maximumItemsProcessed,
                                earliestScanDate = it.earliestScanDate,
                                latestScanDate = it.latestScanDate,
                                excludeDomains = it.excludeDomains,
                                excludeCategories = it.excludeCategories,
                            )
                        },
                ),
            )
        return VirtualPresenceTransformer.fromEntityToApi(result)
    }

    override suspend fun listVirtualPresences(input: ListVirtualPresencesInput): ListOutput<VirtualPresence> {
        logger.debug("listVirtualPresences")
        ensureSignedIn()
        val useCase = useCaseFactory.createListVirtualPresencesUseCase()
        val result =
            useCase.execute(
                ListVirtualPresencesUseCaseInput(
                    limit = input.limit,
                    nextToken = input.nextToken,
                ),
            )
        return ListOutput(
            items = result.items.map { VirtualPresenceTransformer.fromEntityToApi(it) },
            nextToken = result.nextToken,
        )
    }

    override suspend fun subscribeToVirtualPresence(
        subscriptionId: String,
        subscriber: VirtualPresenceSubscriber,
    ) {
        logger.debug("subscribeToVirtualPresence")
        subscriptionService.subscribeToVirtualPresence(subscriptionId, subscriber)
    }

    override fun unsubscribeFromVirtualPresence(subscriptionId: String) {
        logger.debug("unsubscribeFromVirtualPresence")
        subscriptionService.unsubscribeFromVirtualPresence(subscriptionId)
    }

    override fun unsubscribeAllFromVirtualPresence() {
        logger.debug("unsubscribeAllFromVirtualPresence")
        subscriptionService.unsubscribeAllFromVirtualPresence()
    }

    override suspend fun getDataHolder(
        id: String,
        options: GetDataHolderOptions,
    ): DataHolder? =
        coroutineScope {
            logger.debug("getDataHolder")
            ensureSignedIn()
            val getDataHolderUseCase = useCaseFactory.createGetDataHolderUseCase()

            // Fetch the data holder and (optionally) its latest scan summary concurrently to
            // minimise latency. The scan summary is a best-effort convenience: if it fails, the
            // data holder is still returned without a `latestScanSummary`.
            val dataHolderDeferred = async { getDataHolderUseCase.execute(id) }
            val scanSummaryDeferred: Deferred<DataHolderScanSummary?>? =
                if (options.includeScanSummary) {
                    async {
                        try {
                            useCaseFactory
                                .createListDataHolderScanSummariesUseCase()
                                .execute(
                                    ListDataHolderScanSummariesUseCaseInput(
                                        dataHolderId = id,
                                        limit = 1,
                                    ),
                                ).items
                                .firstOrNull()
                                ?.let { DataHolderScanSummaryTransformer.fromEntityToApi(it) }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            logger.error("Failed to fetch latest scan summary for data holder $id: $e")
                            null
                        }
                    }
                } else {
                    null
                }

            // The data holder is the primary result; propagate its error to the caller.
            val dataHolderEntity = dataHolderDeferred.await() ?: return@coroutineScope null

            val latestScanSummary = scanSummaryDeferred?.await()

            DataHolderTransformer.fromEntityToApi(dataHolderEntity, latestScanSummary)
        }

    override suspend fun listDataHolders(input: ListDataHoldersInput): ListOutput<DataHolder> {
        logger.debug("listDataHolders")
        ensureSignedIn()
        val useCase = useCaseFactory.createListDataHoldersUseCase()
        val result =
            useCase.execute(
                ListDataHoldersUseCaseInput(
                    virtualPresenceId = input.virtualPresenceId,
                    limit = input.limit,
                    nextToken = input.nextToken,
                ),
            )
        return ListOutput(
            items = result.items.map { DataHolderTransformer.fromEntityToApi(it) },
            nextToken = result.nextToken,
        )
    }

    override suspend fun listDataHolderScanSummaries(input: ListDataHolderScanSummariesInput): ListOutput<DataHolderScanSummary> {
        logger.debug("listDataHolderScanSummaries")
        ensureSignedIn()
        val useCase = useCaseFactory.createListDataHolderScanSummariesUseCase()
        val result =
            useCase.execute(
                ListDataHolderScanSummariesUseCaseInput(
                    dataHolderId = input.dataHolderId,
                    limit = input.limit,
                    nextToken = input.nextToken,
                ),
            )
        return ListOutput(
            items = result.items.map { DataHolderScanSummaryTransformer.fromEntityToApi(it) },
            nextToken = result.nextToken,
        )
    }

    override suspend fun subscribeToDataHolders(
        subscriptionId: String,
        subscriber: DataHolderSubscriber,
    ) {
        logger.debug("subscribeToDataHolders")
        subscriptionService.subscribeToDataHolders(subscriptionId, subscriber)
    }

    override fun unsubscribeFromDataHolders(subscriptionId: String) {
        logger.debug("unsubscribeFromDataHolders")
        subscriptionService.unsubscribeFromDataHolders(subscriptionId)
    }

    override fun unsubscribeAllFromDataHolders() {
        logger.debug("unsubscribeAllFromDataHolders")
        subscriptionService.unsubscribeAllFromDataHolders()
    }

    override suspend fun getAnalysisResult(id: String): AnalysisResult? {
        logger.debug("getAnalysisResult")
        ensureSignedIn()
        val useCase = useCaseFactory.createGetAnalysisResultUseCase()
        val result = useCase.execute(id) ?: return null
        return AnalysisResultTransformer.fromEntityToApi(result)
    }

    override suspend fun listAnalysisResults(input: ListAnalysisResultsInput): ListOutput<AnalysisResult> {
        logger.debug("listAnalysisResults")
        ensureSignedIn()
        val useCase = useCaseFactory.createListAnalysisResultsUseCase()
        val result =
            useCase.execute(
                ListAnalysisResultsUseCaseInput(
                    virtualPresenceId = input.virtualPresenceId,
                    limit = input.limit,
                    nextToken = input.nextToken,
                ),
            )
        return ListOutput(
            items = result.items.map { AnalysisResultTransformer.fromEntityToApi(it) },
            nextToken = result.nextToken,
        )
    }

    override suspend fun subscribeToAnalysisResult(
        subscriptionId: String,
        subscriber: AnalysisResultSubscriber,
    ) {
        logger.debug("subscribeToAnalysisResult")
        subscriptionService.subscribeToAnalysisResult(subscriptionId, subscriber)
    }

    override fun unsubscribeFromAnalysisResult(subscriptionId: String) {
        logger.debug("unsubscribeFromAnalysisResult")
        subscriptionService.unsubscribeFromAnalysisResult(subscriptionId)
    }

    override fun unsubscribeAllFromAnalysisResult() {
        logger.debug("unsubscribeAllFromAnalysisResult")
        subscriptionService.unsubscribeAllFromAnalysisResult()
    }

    override suspend fun getOrganizationAnalysis(input: GetOrganizationAnalysisInput): OrganizationAnalysis? {
        logger.debug("getOrganizationAnalysis")
        ensureSignedIn()
        val useCase = useCaseFactory.createGetOrganizationAnalysisUseCase()
        val result =
            useCase.execute(
                GetOrganizationAnalysisUseCaseInput(
                    domain = input.domain,
                    mode = input.mode?.let { OrganizationAnalysisModeTransformer.fromApiToEntity(it) },
                ),
            ) ?: return null
        return OrganizationAnalysisTransformer.fromEntityToApi(result)
    }

    override suspend fun initiateAction(input: InitiateActionInput): Action {
        logger.debug("initiateAction")
        ensureSignedIn()
        val useCase = useCaseFactory.createInitiateActionUseCase()
        val result =
            useCase.execute(
                InitiateActionUseCaseInput(
                    dataHolderId = input.dataHolderId,
                    intent = ActionIntentTransformer.fromApiToEntity(input.intent),
                    fulfilmentMethod = ActionFulfilmentMethodTransformer.fromApiToEntity(input.fulfilmentMethod),
                ),
            )
        return ActionTransformer.fromEntityToApi(result)
    }

    override suspend fun confirmActionOutcome(id: String): Action {
        logger.debug("confirmActionOutcome")
        ensureSignedIn()
        val useCase = useCaseFactory.createConfirmActionOutcomeUseCase()
        val result = useCase.execute(id)
        return ActionTransformer.fromEntityToApi(result)
    }

    override suspend fun listAvailableActions(input: ListAvailableActionsInput): ListOutput<AvailableAction> {
        logger.debug("listAvailableActions")
        ensureSignedIn()
        val useCase = useCaseFactory.createListAvailableActionsUseCase()
        val result = useCase.execute(ListAvailableActionsUseCaseInput(dataHolderId = input.dataHolderId))
        return ListOutput(
            items = result.items.map { AvailableActionTransformer.fromEntityToApi(it) },
            nextToken = result.nextToken,
        )
    }

    override suspend fun subscribeToActions(
        subscriptionId: String,
        subscriber: ActionSubscriber,
    ) {
        logger.debug("subscribeToActions")
        subscriptionService.subscribeToActions(subscriptionId, subscriber)
    }

    override fun unsubscribeFromActions(subscriptionId: String) {
        logger.debug("unsubscribeFromActions")
        subscriptionService.unsubscribeFromActions(subscriptionId)
    }

    override fun unsubscribeAllFromActions() {
        logger.debug("unsubscribeAllFromActions")
        subscriptionService.unsubscribeAllFromActions()
    }

    override fun close() {
        subscriptionService.close()
    }

    override fun reset() {
        close()
    }
}
