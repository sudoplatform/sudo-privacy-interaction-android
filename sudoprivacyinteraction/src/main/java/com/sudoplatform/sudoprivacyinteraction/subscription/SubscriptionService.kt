/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.subscription

import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.GraphQLResponse
import com.sudoplatform.sudologging.AndroidUtilsLogDriver
import com.sudoplatform.sudologging.LogLevel
import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.graphql.OnActionStatusUpdateSubscription
import com.sudoplatform.sudoprivacyinteraction.graphql.OnAnalysisResultUpdateSubscription
import com.sudoplatform.sudoprivacyinteraction.graphql.OnDataHoldersUpdateSubscription
import com.sudoplatform.sudoprivacyinteraction.graphql.OnVirtualPresenceUpdateSubscription
import com.sudoplatform.sudoprivacyinteraction.internal.data.action.transformers.ActionTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.analysisResult.transformers.AnalysisResultUpdateTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.dataHolder.transformers.DataHolderTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence.transformers.VirtualPresenceTransformer
import com.sudoplatform.sudoprivacyinteraction.logging.LogConstants
import com.sudoplatform.sudouser.SudoUserClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Manages the virtual presence, data holder, and analysis result update subscriptions.
 *
 * Each subscription type is backed by its own [SubscriptionManager] which multiplexes a single
 * AppSync watcher to all subscribers registered under that type. The AppSync subscription is
 * established on the first subscriber and torn down when the last subscriber is removed.
 *
 * @property apiClient [ApiClient] The GraphQL API client used to establish subscriptions.
 * @property userClient [SudoUserClient] Used to determine the owner (subject) to subscribe for.
 * @property logger [Logger] Errors and diagnostic information are logged here.
 */
internal class SubscriptionService(
    private val apiClient: ApiClient,
    private val userClient: SudoUserClient,
    private val logger: Logger = Logger(LogConstants.SUDOLOG_TAG, AndroidUtilsLogDriver(LogLevel.INFO)),
) : AutoCloseable {
    companion object {
        private const val ERROR_UNAUTHENTICATED_MSG =
            "User client does not have subject. Is the user authenticated?"
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val virtualPresenceSubscriptionManager =
        VirtualPresenceSubscriptionManager<OnVirtualPresenceUpdateSubscription.Data>()
    private val dataHolderSubscriptionManager =
        DataHolderSubscriptionManager<OnDataHoldersUpdateSubscription.Data>()
    private val analysisResultSubscriptionManager =
        AnalysisResultSubscriptionManager<OnAnalysisResultUpdateSubscription.Data>()
    private val actionSubscriptionManager =
        ActionSubscriptionManager<OnActionStatusUpdateSubscription.Data>()

    suspend fun subscribeToVirtualPresence(
        id: String,
        subscriber: VirtualPresenceSubscriber,
    ) {
        val owner =
            userClient.getSubject()
                ?: throw SudoPrivacyInteractionClient.VirtualPresenceException.AuthenticationException(
                    ERROR_UNAUTHENTICATED_MSG,
                )

        virtualPresenceSubscriptionManager.replaceSubscriber(id, subscriber)

        scope
            .launch {
                if (virtualPresenceSubscriptionManager.watcher == null) {
                    virtualPresenceSubscriptionManager.watcher =
                        apiClient.onVirtualPresenceUpdateSubscription(
                            owner,
                            virtualPresenceCallback.onSubscriptionEstablished,
                            virtualPresenceCallback.onSubscription,
                            virtualPresenceCallback.onSubscriptionCompleted,
                            virtualPresenceCallback.onFailure,
                        )
                }
            }.join()
    }

    fun unsubscribeFromVirtualPresence(id: String) {
        virtualPresenceSubscriptionManager.removeSubscriber(id)
    }

    fun unsubscribeAllFromVirtualPresence() {
        virtualPresenceSubscriptionManager.removeAllSubscribers()
    }

    suspend fun subscribeToDataHolders(
        id: String,
        subscriber: DataHolderSubscriber,
    ) {
        val owner =
            userClient.getSubject()
                ?: throw SudoPrivacyInteractionClient.DataHolderException.AuthenticationException(
                    ERROR_UNAUTHENTICATED_MSG,
                )

        dataHolderSubscriptionManager.replaceSubscriber(id, subscriber)

        scope
            .launch {
                if (dataHolderSubscriptionManager.watcher == null) {
                    dataHolderSubscriptionManager.watcher =
                        apiClient.onDataHoldersUpdateSubscription(
                            owner,
                            dataHolderCallback.onSubscriptionEstablished,
                            dataHolderCallback.onSubscription,
                            dataHolderCallback.onSubscriptionCompleted,
                            dataHolderCallback.onFailure,
                        )
                }
            }.join()
    }

    fun unsubscribeFromDataHolders(id: String) {
        dataHolderSubscriptionManager.removeSubscriber(id)
    }

    fun unsubscribeAllFromDataHolders() {
        dataHolderSubscriptionManager.removeAllSubscribers()
    }

    suspend fun subscribeToAnalysisResult(
        id: String,
        subscriber: AnalysisResultSubscriber,
    ) {
        val owner =
            userClient.getSubject()
                ?: throw SudoPrivacyInteractionClient.AnalysisResultException.AuthenticationException(
                    ERROR_UNAUTHENTICATED_MSG,
                )

        analysisResultSubscriptionManager.replaceSubscriber(id, subscriber)

        scope
            .launch {
                if (analysisResultSubscriptionManager.watcher == null) {
                    analysisResultSubscriptionManager.watcher =
                        apiClient.onAnalysisResultUpdateSubscription(
                            owner,
                            analysisResultCallback.onSubscriptionEstablished,
                            analysisResultCallback.onSubscription,
                            analysisResultCallback.onSubscriptionCompleted,
                            analysisResultCallback.onFailure,
                        )
                }
            }.join()
    }

    fun unsubscribeFromAnalysisResult(id: String) {
        analysisResultSubscriptionManager.removeSubscriber(id)
    }

    fun unsubscribeAllFromAnalysisResult() {
        analysisResultSubscriptionManager.removeAllSubscribers()
    }

    suspend fun subscribeToActions(
        id: String,
        subscriber: ActionSubscriber,
    ) {
        val owner =
            userClient.getSubject()
                ?: throw SudoPrivacyInteractionClient.ActionException.AuthenticationException(
                    ERROR_UNAUTHENTICATED_MSG,
                )

        actionSubscriptionManager.replaceSubscriber(id, subscriber)

        scope
            .launch {
                if (actionSubscriptionManager.watcher == null) {
                    actionSubscriptionManager.watcher =
                        apiClient.onActionStatusUpdateSubscription(
                            owner,
                            actionCallback.onSubscriptionEstablished,
                            actionCallback.onSubscription,
                            actionCallback.onSubscriptionCompleted,
                            actionCallback.onFailure,
                        )
                }
            }.join()
    }

    fun unsubscribeFromActions(id: String) {
        actionSubscriptionManager.removeSubscriber(id)
    }

    fun unsubscribeAllFromActions() {
        actionSubscriptionManager.removeAllSubscribers()
    }

    override fun close() {
        unsubscribeAllFromVirtualPresence()
        unsubscribeAllFromDataHolders()
        unsubscribeAllFromAnalysisResult()
        unsubscribeAllFromActions()
        scope.cancel()
    }

    private val virtualPresenceCallback =
        object {
            val onSubscriptionEstablished: (GraphQLResponse<OnVirtualPresenceUpdateSubscription.Data>) -> Unit = {
                virtualPresenceSubscriptionManager.connectionStatusChanged(
                    Subscriber.ConnectionState.CONNECTED,
                )
            }
            val onSubscription: (GraphQLResponse<OnVirtualPresenceUpdateSubscription.Data>) -> Unit = {
                scope.launch {
                    val update = it.data?.onVirtualPresenceUpdate ?: return@launch
                    val entity = VirtualPresenceTransformer.fromGraphQLToEntity(update.virtualPresence)
                    virtualPresenceSubscriptionManager.virtualPresenceUpdated(
                        VirtualPresenceTransformer.fromEntityToApi(entity),
                    )
                }
            }
            val onSubscriptionCompleted = {
                virtualPresenceSubscriptionManager.connectionStatusChanged(
                    Subscriber.ConnectionState.DISCONNECTED,
                )
            }
            val onFailure: (ApiException) -> Unit = {
                logger.error("Virtual presence subscription error $it")
            }
        }

    private val dataHolderCallback =
        object {
            val onSubscriptionEstablished: (GraphQLResponse<OnDataHoldersUpdateSubscription.Data>) -> Unit = {
                dataHolderSubscriptionManager.connectionStatusChanged(
                    Subscriber.ConnectionState.CONNECTED,
                )
            }
            val onSubscription: (GraphQLResponse<OnDataHoldersUpdateSubscription.Data>) -> Unit = {
                scope.launch {
                    val update = it.data?.onDataHoldersUpdate ?: return@launch
                    val dataHolders =
                        update.items.map { item ->
                            DataHolderTransformer.fromEntityToApi(
                                DataHolderTransformer.fromSubscriptionItemToEntity(item),
                            )
                        }
                    dataHolderSubscriptionManager.dataHoldersUpdated(dataHolders)
                }
            }
            val onSubscriptionCompleted = {
                dataHolderSubscriptionManager.connectionStatusChanged(
                    Subscriber.ConnectionState.DISCONNECTED,
                )
            }
            val onFailure: (ApiException) -> Unit = {
                logger.error("Data holder subscription error $it")
            }
        }

    private val analysisResultCallback =
        object {
            val onSubscriptionEstablished: (GraphQLResponse<OnAnalysisResultUpdateSubscription.Data>) -> Unit = {
                analysisResultSubscriptionManager.connectionStatusChanged(
                    Subscriber.ConnectionState.CONNECTED,
                )
            }
            val onSubscription: (GraphQLResponse<OnAnalysisResultUpdateSubscription.Data>) -> Unit = {
                scope.launch {
                    val update = it.data?.onAnalysisResultUpdate ?: return@launch
                    analysisResultSubscriptionManager.analysisResultUpdated(
                        AnalysisResultUpdateTransformer.fromGraphQLToApi(update),
                    )
                }
            }
            val onSubscriptionCompleted = {
                analysisResultSubscriptionManager.connectionStatusChanged(
                    Subscriber.ConnectionState.DISCONNECTED,
                )
            }
            val onFailure: (ApiException) -> Unit = {
                logger.error("Analysis result subscription error $it")
            }
        }

    private val actionCallback =
        object {
            val onSubscriptionEstablished: (GraphQLResponse<OnActionStatusUpdateSubscription.Data>) -> Unit = {
                actionSubscriptionManager.connectionStatusChanged(
                    Subscriber.ConnectionState.CONNECTED,
                )
            }
            val onSubscription: (GraphQLResponse<OnActionStatusUpdateSubscription.Data>) -> Unit = {
                scope.launch {
                    val update = it.data?.onActionStatusUpdate ?: return@launch
                    actionSubscriptionManager.actionUpdated(
                        ActionTransformer.fromEntityToApi(
                            ActionTransformer.fromGraphQLToEntity(update.action),
                        ),
                    )
                }
            }
            val onSubscriptionCompleted = {
                actionSubscriptionManager.connectionStatusChanged(
                    Subscriber.ConnectionState.DISCONNECTED,
                )
            }
            val onFailure: (ApiException) -> Unit = {
                logger.error("Action subscription error $it")
            }
        }
}
