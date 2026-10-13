/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import android.content.Context
import com.sudoplatform.sudoapiclient.ApiClientManager
import com.sudoplatform.sudologging.AndroidUtilsLogDriver
import com.sudoplatform.sudologging.LogLevel
import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.logging.LogConstants
import com.sudoplatform.sudoprivacyinteraction.subscription.ActionSubscriber
import com.sudoplatform.sudoprivacyinteraction.subscription.AnalysisResultSubscriber
import com.sudoplatform.sudoprivacyinteraction.subscription.DataHolderSubscriber
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
import java.util.Objects

/**
 * Interface encapsulating a library for interacting with the Sudo Platform Privacy Interaction
 * & Mitigation service.
 *
 * @sample com.sudoplatform.sudoprivacyinteraction.samples.Samples.sudoPrivacyInteractionClient
 */
interface SudoPrivacyInteractionClient : AutoCloseable {
    companion object {
        /** Create a [Builder] for [SudoPrivacyInteractionClient]. */
        @JvmStatic
        fun builder() = Builder()

        /** Name of the service stanza in `sudoplatformconfig.json`. */
        internal const val CONFIG_PRIVACY_INTERACTION_SERVICE = "privacyInteractionService"
    }

    /**
     * Builder used to construct the [SudoPrivacyInteractionClient].
     */
    class Builder internal constructor() {
        private var context: Context? = null
        private var sudoUserClient: SudoUserClient? = null
        private var apiClient: ApiClient? = null
        private var signInGuard: SignInGuard? = null
        private var logger: Logger =
            Logger(LogConstants.SUDOLOG_TAG, AndroidUtilsLogDriver(LogLevel.INFO))

        /**
         * Provide the application context (required input).
         */
        fun setContext(context: Context) =
            also {
                this.context = context
            }

        /**
         * Provide the implementation of the [SudoUserClient] used to perform
         * sign in and ownership operations (required input).
         */
        fun setSudoUserClient(sudoUserClient: SudoUserClient) =
            also {
                this.sudoUserClient = sudoUserClient
            }

        /**
         * Provide an [ApiClient] for the [SudoPrivacyInteractionClient] to use
         * (optional input). If this is not supplied, an [ApiClient] will be
         * constructed and used.
         */
        fun setApiClient(apiClient: ApiClient) =
            also {
                this.apiClient = apiClient
            }

        /**
         * Provide the implementation of the [Logger] used for logging errors (optional input).
         * If a value is not supplied a default implementation will be used.
         */
        fun setLogger(logger: Logger) =
            also {
                this.logger = logger
            }

        /**
         * Provide the implementation of the [SignInGuard] used for sign-in verification
         * (optional input). If a value is not supplied a default implementation will be used.
         */
        internal fun setSignInGuard(signInGuard: SignInGuard) =
            also {
                this.signInGuard = signInGuard
            }

        /** A configuration item that is needed is missing. */
        class ConfigurationException(
            message: String,
            cause: Throwable? = null,
        ) : RuntimeException(message, cause)

        /**
         * Construct the [SudoPrivacyInteractionClient]. Will throw a [NullPointerException] if
         * the [context] and [sudoUserClient] have not been provided.
         */
        @Throws(NullPointerException::class, ConfigurationException::class)
        fun build(): SudoPrivacyInteractionClient {
            Objects.requireNonNull(context, "Context must be provided.")
            Objects.requireNonNull(sudoUserClient, "SudoUserClient must be provided.")

            val client =
                apiClient ?: ApiClient(
                    ApiClientManager.getClient(
                        this@Builder.context!!,
                        this@Builder.sudoUserClient!!,
                        CONFIG_PRIVACY_INTERACTION_SERVICE,
                    ),
                    logger,
                )

            return DefaultSudoPrivacyInteractionClient(
                context = context!!,
                apiClient = client,
                sudoUserClient = sudoUserClient!!,
                logger = logger,
                signInGuard = signInGuard,
            )
        }
    }

    /**
     * Defines the exceptions for the virtual presence based methods.
     *
     * @property message [String] Accompanying message for the exception.
     * @property cause [Throwable] The cause for the exception.
     */
    sealed class VirtualPresenceException(
        message: String? = null,
        cause: Throwable? = null,
    ) : RuntimeException(message, cause) {
        class FailedException(
            message: String? = null,
            cause: Throwable? = null,
        ) : VirtualPresenceException(message = message, cause = cause)

        class VirtualPresenceNotFoundException(
            message: String? = null,
            cause: Throwable? = null,
        ) : VirtualPresenceException(message = message, cause = cause)

        class IdentityContextMissingException(
            message: String? = null,
            cause: Throwable? = null,
        ) : VirtualPresenceException(message = message, cause = cause)

        class InvalidAuthCodeException(
            message: String? = null,
            cause: Throwable? = null,
        ) : VirtualPresenceException(message = message, cause = cause)

        class MissingRefreshTokenException(
            message: String? = null,
            cause: Throwable? = null,
        ) : VirtualPresenceException(message = message, cause = cause)

        class AccessTokenRefreshFailedException(
            message: String? = null,
            cause: Throwable? = null,
        ) : VirtualPresenceException(message = message, cause = cause)

        class InvalidArgumentException(
            message: String? = null,
            cause: Throwable? = null,
        ) : VirtualPresenceException(message = message, cause = cause)

        class AuthenticationException(
            message: String? = null,
            cause: Throwable? = null,
        ) : VirtualPresenceException(message = message, cause = cause)

        class UnknownException(
            cause: Throwable,
        ) : VirtualPresenceException(cause = cause)
    }

    /**
     * Defines the exceptions for the data holder based methods.
     *
     * @property message [String] Accompanying message for the exception.
     * @property cause [Throwable] The cause for the exception.
     */
    sealed class DataHolderException(
        message: String? = null,
        cause: Throwable? = null,
    ) : RuntimeException(message, cause) {
        class FailedException(
            message: String? = null,
            cause: Throwable? = null,
        ) : DataHolderException(message = message, cause = cause)

        class DataHolderNotFoundException(
            message: String? = null,
            cause: Throwable? = null,
        ) : DataHolderException(message = message, cause = cause)

        class IdentityContextMissingException(
            message: String? = null,
            cause: Throwable? = null,
        ) : DataHolderException(message = message, cause = cause)

        class InvalidArgumentException(
            message: String? = null,
            cause: Throwable? = null,
        ) : DataHolderException(message = message, cause = cause)

        class AuthenticationException(
            message: String? = null,
            cause: Throwable? = null,
        ) : DataHolderException(message = message, cause = cause)

        class UnknownException(
            cause: Throwable,
        ) : DataHolderException(cause = cause)
    }

    /**
     * Defines the exceptions for the analysis result based methods.
     *
     * @property message [String] Accompanying message for the exception.
     * @property cause [Throwable] The cause for the exception.
     */
    sealed class AnalysisResultException(
        message: String? = null,
        cause: Throwable? = null,
    ) : RuntimeException(message, cause) {
        class FailedException(
            message: String? = null,
            cause: Throwable? = null,
        ) : AnalysisResultException(message = message, cause = cause)

        class AnalysisResultNotFoundException(
            message: String? = null,
            cause: Throwable? = null,
        ) : AnalysisResultException(message = message, cause = cause)

        class IdentityContextMissingException(
            message: String? = null,
            cause: Throwable? = null,
        ) : AnalysisResultException(message = message, cause = cause)

        class InvalidArgumentException(
            message: String? = null,
            cause: Throwable? = null,
        ) : AnalysisResultException(message = message, cause = cause)

        class AuthenticationException(
            message: String? = null,
            cause: Throwable? = null,
        ) : AnalysisResultException(message = message, cause = cause)

        class UnknownException(
            cause: Throwable,
        ) : AnalysisResultException(cause = cause)
    }

    /**
     * Defines the exceptions for the action based methods.
     *
     * @property message [String] Accompanying message for the exception.
     * @property cause [Throwable] The cause for the exception.
     */
    sealed class ActionException(
        message: String? = null,
        cause: Throwable? = null,
    ) : RuntimeException(message, cause) {
        class FailedException(
            message: String? = null,
            cause: Throwable? = null,
        ) : ActionException(message = message, cause = cause)

        class ActionNotFoundException(
            message: String? = null,
            cause: Throwable? = null,
        ) : ActionException(message = message, cause = cause)

        class DataHolderNotFoundException(
            message: String? = null,
            cause: Throwable? = null,
        ) : ActionException(message = message, cause = cause)

        class IdentityContextMissingException(
            message: String? = null,
            cause: Throwable? = null,
        ) : ActionException(message = message, cause = cause)

        class InvalidArgumentException(
            message: String? = null,
            cause: Throwable? = null,
        ) : ActionException(message = message, cause = cause)

        class AuthenticationException(
            message: String? = null,
            cause: Throwable? = null,
        ) : ActionException(message = message, cause = cause)

        class UnknownException(
            cause: Throwable,
        ) : ActionException(cause = cause)
    }

    /**
     * Set an optional callback to be invoked when an operation is attempted while the user is not signed in.
     *
     * When set, all operations (except subscriptions and initialization) check the sign-in status
     * and invoke this callback if the user is not signed in. Setting the callback to `null`
     * disables automatic sign-in checking, restoring the default behavior where operations proceed
     * without sign-in checks. If the callback throws an exception, that exception is propagated to
     * the caller and the original operation is not executed.
     *
     * @param callback An optional [SudoPlatformSignInCallback] A callback to handle sign-in, or
     *  `null` to disable automatic sign-in checking.
     */
    suspend fun setSignInCallback(callback: SudoPlatformSignInCallback?)

    /**
     * Retrieve the provider configurations required by consumers to support the provider OAuth
     * flow.
     *
     * @return The list of [ProviderConfiguration] supported by the service.
     */
    @Throws(VirtualPresenceException::class)
    suspend fun getProviderConfiguration(): List<ProviderConfiguration>

    /**
     * Connect a virtual presence using an authorization code obtained from an OAuth flow.
     *
     * @param input [ConnectVirtualPresenceWithAuthCodeInput] Parameters used to connect a virtual
     *  presence with an authorization code input.
     * @return The connected [VirtualPresence].
     */
    @Throws(VirtualPresenceException::class)
    suspend fun connectVirtualPresenceWithAuthCode(input: ConnectVirtualPresenceWithAuthCodeInput): VirtualPresence

    /**
     * Connect a virtual presence using a pre-obtained refresh token credential.
     *
     * @param input [ConnectVirtualPresenceWithRefreshTokenInput] Parameters used to connect a
     *  virtual presence with a refresh token input.
     * @return The connected [VirtualPresence].
     */
    @Throws(VirtualPresenceException::class)
    suspend fun connectVirtualPresenceWithRefreshToken(input: ConnectVirtualPresenceWithRefreshTokenInput): VirtualPresence

    /**
     * Disconnect a virtual presence.
     *
     * @param id [String] The unique identifier of the virtual presence to disconnect.
     * @return The disconnected [VirtualPresence].
     */
    @Throws(VirtualPresenceException::class)
    suspend fun disconnectVirtualPresence(id: String): VirtualPresence

    /**
     * Rescan a virtual presence.
     *
     * @param input [RescanVirtualPresenceInput] Parameters used to rescan a virtual presence.
     * @return The rescanned [VirtualPresence].
     */
    @Throws(VirtualPresenceException::class)
    suspend fun rescanVirtualPresence(input: RescanVirtualPresenceInput): VirtualPresence

    /**
     * List all virtual presences for the signed-in user.
     *
     * @param input [ListVirtualPresencesInput] Parameters used to retrieve a list of virtual
     *  presences.
     * @return A [ListOutput] of all virtual presences matching the search criteria.
     */
    @Throws(VirtualPresenceException::class)
    suspend fun listVirtualPresences(input: ListVirtualPresencesInput = ListVirtualPresencesInput()): ListOutput<VirtualPresence>

    /**
     * Subscribe to virtual presence state events. The subscriber is notified when a virtual
     * presence changes state (e.g. to NEEDS_REAUTH or SCANNING).
     *
     * @param subscriptionId [String] A unique identifier for this subscription.
     * @param subscriber [VirtualPresenceSubscriber] Callback invoked when a virtual presence event
     *  occurs.
     */
    @Throws(VirtualPresenceException::class)
    suspend fun subscribeToVirtualPresence(
        subscriptionId: String,
        subscriber: VirtualPresenceSubscriber,
    )

    /**
     * Unsubscribe the specified subscriber from virtual presence state events.
     *
     * @param subscriptionId [String] The subscription identifier to unsubscribe.
     */
    fun unsubscribeFromVirtualPresence(subscriptionId: String)

    /**
     * Unsubscribe all subscribers from virtual presence state events.
     */
    fun unsubscribeAllFromVirtualPresence()

    /**
     * Retrieve a single data holder by ID.
     *
     * @param id [String] The unique identifier of the data holder.
     * @param options [GetDataHolderOptions] Optional settings controlling what is returned. By
     *  default, the most recent scan summary is fetched concurrently and attached as
     *  [DataHolder.latestScanSummary].
     * @return The [DataHolder], or `null` if not found.
     */
    @Throws(DataHolderException::class)
    suspend fun getDataHolder(
        id: String,
        options: GetDataHolderOptions = GetDataHolderOptions(),
    ): DataHolder?

    /**
     * List data holders associated with a virtual presence.
     *
     * @param input [ListDataHoldersInput] Parameters used to retrieve a list of data holders.
     * @return A [ListOutput] of data holders matching the search criteria.
     */
    @Throws(DataHolderException::class)
    suspend fun listDataHolders(input: ListDataHoldersInput): ListOutput<DataHolder>

    /**
     * List scan summaries associated with a data holder, most recent first.
     *
     * @param input [ListDataHolderScanSummariesInput] Parameters used to retrieve a list of scan
     *  summaries.
     * @return A [ListOutput] of scan summaries matching the search criteria.
     */
    @Throws(DataHolderException::class)
    suspend fun listDataHolderScanSummaries(input: ListDataHolderScanSummariesInput): ListOutput<DataHolderScanSummary>

    /**
     * Subscribe to data holder events. The subscriber is notified with batches of data holders as
     * they are discovered or updated during scanning.
     *
     * @param subscriptionId [String] A unique identifier for this subscription.
     * @param subscriber [DataHolderSubscriber] Callback invoked when data holder events occur.
     */
    @Throws(DataHolderException::class)
    suspend fun subscribeToDataHolders(
        subscriptionId: String,
        subscriber: DataHolderSubscriber,
    )

    /**
     * Unsubscribe the specified subscriber from data holder events.
     *
     * @param subscriptionId [String] The subscription identifier to unsubscribe.
     */
    fun unsubscribeFromDataHolders(subscriptionId: String)

    /**
     * Unsubscribe all subscribers from data holder events.
     */
    fun unsubscribeAllFromDataHolders()

    /**
     * Retrieve a full analysis result by ID.
     *
     * @param id [String] The unique identifier of the analysis result.
     * @return The [AnalysisResult], or `null` if not found.
     */
    @Throws(AnalysisResultException::class)
    suspend fun getAnalysisResult(id: String): AnalysisResult?

    /**
     * List analysis results associated with a virtual presence.
     *
     * @param input [ListAnalysisResultsInput] Parameters used to retrieve a list of analysis
     *  results.
     * @return A [ListOutput] of analysis results matching the search criteria.
     */
    @Throws(AnalysisResultException::class)
    suspend fun listAnalysisResults(input: ListAnalysisResultsInput): ListOutput<AnalysisResult>

    /**
     * Subscribe to analysis result events. The subscriber is notified when an analysis result is
     * created or updated.
     *
     * @param subscriptionId [String] A unique identifier for this subscription.
     * @param subscriber [AnalysisResultSubscriber] Callback invoked when an analysis result event
     *  occurs.
     */
    @Throws(AnalysisResultException::class)
    suspend fun subscribeToAnalysisResult(
        subscriptionId: String,
        subscriber: AnalysisResultSubscriber,
    )

    /**
     * Unsubscribe the specified subscriber from analysis result events.
     *
     * @param subscriptionId [String] The subscription identifier to unsubscribe.
     */
    fun unsubscribeFromAnalysisResult(subscriptionId: String)

    /**
     * Unsubscribe all subscribers from analysis result events.
     */
    fun unsubscribeAllFromAnalysisResult()

    /**
     * Analyze (read-through) and return the shared, organization-generic analysis for a domain.
     *
     * Returns a result with a PENDING status when an asynchronous source is still resolving;
     * re-query for the terminal result. When [GetOrganizationAnalysisInput.mode] is omitted, the
     * backend behaves as [com.sudoplatform.sudoprivacyinteraction.types.OrganizationAnalysisMode.ANALYZE].
     *
     * @param input [GetOrganizationAnalysisInput] Parameters used to obtain the analysis.
     * @return The [OrganizationAnalysis], or `null` if not found.
     */
    @Throws(AnalysisResultException::class)
    suspend fun getOrganizationAnalysis(input: GetOrganizationAnalysisInput): OrganizationAnalysis?

    /**
     * Initiate an action against a data holder relationship. Delegated actions are performed on
     * the user's behalf; assisted actions return structured materials for the client to complete.
     *
     * @param input [InitiateActionInput] Parameters used to initiate the action.
     * @return The initiated [Action].
     */
    @Throws(ActionException::class)
    suspend fun initiateAction(input: InitiateActionInput): Action

    /**
     * Confirm that an assisted or delegated action was completed, transitioning it to a verified
     * completion state.
     *
     * @param id [String] The unique identifier of the action to confirm.
     * @return The updated [Action].
     */
    @Throws(ActionException::class)
    suspend fun confirmActionOutcome(id: String): Action

    /**
     * List the actions currently available for a data holder relationship.
     *
     * @param input [ListAvailableActionsInput] Parameters used to list available actions.
     * @return A [ListOutput] of available actions.
     */
    @Throws(ActionException::class)
    suspend fun listAvailableActions(input: ListAvailableActionsInput): ListOutput<AvailableAction>

    /**
     * Subscribe to action status events. The subscriber is notified when an action's status
     * changes.
     *
     * @param subscriptionId [String] A unique identifier for this subscription.
     * @param subscriber [ActionSubscriber] Callback invoked when an action event occurs.
     */
    @Throws(ActionException::class)
    suspend fun subscribeToActions(
        subscriptionId: String,
        subscriber: ActionSubscriber,
    )

    /**
     * Unsubscribe the specified subscriber from action status events.
     *
     * @param subscriptionId [String] The subscription identifier to unsubscribe.
     */
    fun unsubscribeFromActions(subscriptionId: String)

    /**
     * Unsubscribe all subscribers from action status events.
     */
    fun unsubscribeAllFromActions()

    /**
     * Reset any internal state and cached content.
     */
    fun reset()
}
