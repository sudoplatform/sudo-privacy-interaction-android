/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.common.transformers

import com.amplifyframework.api.graphql.GraphQLResponse
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient.ActionException
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient.AnalysisResultException
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient.DataHolderException
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient.VirtualPresenceException
import com.sudoplatform.sudoprivacyinteraction.internal.data.common.StringConstants
import com.sudoplatform.sudouser.exceptions.HTTP_STATUS_CODE_KEY
import java.net.HttpURLConnection
import java.util.concurrent.CancellationException

/**
 * Transformer for interpreting and converting errors from various sources into appropriate
 * [SudoPrivacyInteractionClient] exception types.
 */
internal object ErrorTransformer {
    /**
     * Interprets a GraphQL error and converts it to an appropriate [VirtualPresenceException].
     *
     * @param e [GraphQLResponse.Error] The GraphQL error to interpret.
     * @return The corresponding [VirtualPresenceException].
     */
    fun interpretVirtualPresenceError(e: GraphQLResponse.Error): VirtualPresenceException {
        val httpStatusCode = e.extensions?.get(HTTP_STATUS_CODE_KEY) as Int?
        val error = e.extensions?.get(StringConstants.ERROR_TYPE)?.toString() ?: ""

        if (httpStatusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return VirtualPresenceException.AuthenticationException("$e")
        }
        if (httpStatusCode != null && httpStatusCode >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
            return VirtualPresenceException.FailedException("$e")
        }
        if (error.contains(StringConstants.ERROR_INVALID_ARGUMENT)) {
            return VirtualPresenceException.InvalidArgumentException(
                StringConstants.INVALID_ARGUMENT_ERROR_MSG,
            )
        }
        if (error.contains(StringConstants.ERROR_IDENTITY_CONTEXT_MISSING)) {
            return VirtualPresenceException.IdentityContextMissingException(
                StringConstants.IDENTITY_CONTEXT_MISSING_MSG,
            )
        }
        if (error.contains(StringConstants.ERROR_INVALID_AUTH_CODE)) {
            return VirtualPresenceException.InvalidAuthCodeException(
                StringConstants.INVALID_AUTH_CODE_MSG,
            )
        }
        if (error.contains(StringConstants.ERROR_MISSING_REFRESH_TOKEN)) {
            return VirtualPresenceException.MissingRefreshTokenException(
                StringConstants.MISSING_REFRESH_TOKEN_MSG,
            )
        }
        if (error.contains(StringConstants.ERROR_ACCESS_TOKEN_REFRESH_FAILED)) {
            return VirtualPresenceException.AccessTokenRefreshFailedException(
                StringConstants.ACCESS_TOKEN_REFRESH_FAILED_MSG,
            )
        }
        if (error.contains(StringConstants.ERROR_VIRTUAL_PRESENCE_NOT_FOUND)) {
            return VirtualPresenceException.VirtualPresenceNotFoundException(
                StringConstants.VIRTUAL_PRESENCE_NOT_FOUND_MSG,
            )
        }
        return VirtualPresenceException.FailedException(e.toString())
    }

    /**
     * Interprets a throwable exception and converts it to an appropriate [VirtualPresenceException].
     *
     * @param e [Throwable] The throwable to interpret.
     * @return [Throwable] The corresponding exception, which may be the original exception if
     *  already handled.
     */
    fun interpretVirtualPresenceException(e: Throwable): Throwable =
        when (e) {
            is CancellationException,
            is VirtualPresenceException,
            -> e
            else -> VirtualPresenceException.UnknownException(e)
        }

    /**
     * Interprets a GraphQL error and converts it to an appropriate [DataHolderException].
     *
     * @param e [GraphQLResponse.Error] The GraphQL error to interpret.
     * @return The corresponding [SudoPrivacyInteractionClient.DataHolderException].
     */
    fun interpretDataHolderError(e: GraphQLResponse.Error): DataHolderException {
        val httpStatusCode = e.extensions?.get(HTTP_STATUS_CODE_KEY) as Int?
        val error = e.extensions?.get(StringConstants.ERROR_TYPE)?.toString() ?: ""

        if (httpStatusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return DataHolderException.AuthenticationException("$e")
        }
        if (httpStatusCode != null && httpStatusCode >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
            return DataHolderException.FailedException("$e")
        }
        if (error.contains(StringConstants.ERROR_INVALID_ARGUMENT)) {
            return DataHolderException.InvalidArgumentException(
                StringConstants.INVALID_ARGUMENT_ERROR_MSG,
            )
        }
        if (error.contains(StringConstants.ERROR_IDENTITY_CONTEXT_MISSING)) {
            return DataHolderException.IdentityContextMissingException(
                StringConstants.IDENTITY_CONTEXT_MISSING_MSG,
            )
        }
        if (error.contains(StringConstants.ERROR_DATA_HOLDER_NOT_FOUND)) {
            return DataHolderException.DataHolderNotFoundException(
                StringConstants.DATA_HOLDER_NOT_FOUND_MSG,
            )
        }
        return DataHolderException.FailedException(e.toString())
    }

    /**
     * Interprets a throwable exception and converts it to an appropriate [DataHolderException].
     *
     * @param e [Throwable] The throwable to interpret.
     * @return [Throwable] The corresponding exception, which may be the original exception if
     *  already handled.
     */
    fun interpretDataHolderException(e: Throwable): Throwable =
        when (e) {
            is CancellationException,
            is DataHolderException,
            -> e
            else -> DataHolderException.UnknownException(e)
        }

    /**
     * Interprets a GraphQL error and converts it to an appropriate [AnalysisResultException].
     *
     * @param e [GraphQLResponse.Error] The GraphQL error to interpret.
     * @return The corresponding [AnalysisResultException].
     */
    fun interpretAnalysisResultError(e: GraphQLResponse.Error): AnalysisResultException {
        val httpStatusCode = e.extensions?.get(HTTP_STATUS_CODE_KEY) as Int?
        val error = e.extensions?.get(StringConstants.ERROR_TYPE)?.toString() ?: ""

        if (httpStatusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return AnalysisResultException.AuthenticationException("$e")
        }
        if (httpStatusCode != null && httpStatusCode >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
            return AnalysisResultException.FailedException("$e")
        }
        if (error.contains(StringConstants.ERROR_INVALID_ARGUMENT)) {
            return AnalysisResultException.InvalidArgumentException(
                StringConstants.INVALID_ARGUMENT_ERROR_MSG,
            )
        }
        if (error.contains(StringConstants.ERROR_IDENTITY_CONTEXT_MISSING)) {
            return AnalysisResultException.IdentityContextMissingException(
                StringConstants.IDENTITY_CONTEXT_MISSING_MSG,
            )
        }
        if (error.contains(StringConstants.ERROR_ANALYSIS_RESULT_NOT_FOUND)) {
            return AnalysisResultException.AnalysisResultNotFoundException(
                StringConstants.ANALYSIS_RESULT_NOT_FOUND_MSG,
            )
        }
        return AnalysisResultException.FailedException(e.toString())
    }

    /**
     * Interprets a throwable exception and converts it to an appropriate [AnalysisResultException].
     *
     * @param e [Throwable] The throwable to interpret.
     * @return [Throwable] The corresponding exception, which may be the original exception if
     *  already handled.
     */
    fun interpretAnalysisResultException(e: Throwable): Throwable =
        when (e) {
            is CancellationException,
            is AnalysisResultException,
            -> e
            else -> AnalysisResultException.UnknownException(e)
        }

    /**
     * Interprets a GraphQL error and converts it to an appropriate [ActionException].
     *
     * @param e [GraphQLResponse.Error] The GraphQL error to interpret.
     * @return The corresponding [SudoPrivacyInteractionClient.ActionException].
     */
    fun interpretActionError(e: GraphQLResponse.Error): ActionException {
        val httpStatusCode = e.extensions?.get(HTTP_STATUS_CODE_KEY) as Int?
        val error = e.extensions?.get(StringConstants.ERROR_TYPE)?.toString() ?: ""

        if (httpStatusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return ActionException.AuthenticationException("$e")
        }
        if (httpStatusCode != null && httpStatusCode >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
            return ActionException.FailedException("$e")
        }
        if (error.contains(StringConstants.ERROR_INVALID_ARGUMENT)) {
            return ActionException.InvalidArgumentException(
                StringConstants.INVALID_ARGUMENT_ERROR_MSG,
            )
        }
        if (error.contains(StringConstants.ERROR_IDENTITY_CONTEXT_MISSING)) {
            return ActionException.IdentityContextMissingException(
                StringConstants.IDENTITY_CONTEXT_MISSING_MSG,
            )
        }
        if (error.contains(StringConstants.ERROR_ACTION_NOT_FOUND)) {
            return ActionException.ActionNotFoundException(
                StringConstants.ACTION_NOT_FOUND_MSG,
            )
        }
        if (error.contains(StringConstants.ERROR_DATA_HOLDER_NOT_FOUND)) {
            return ActionException.DataHolderNotFoundException(
                StringConstants.DATA_HOLDER_NOT_FOUND_FOR_ACTION_MSG,
            )
        }
        return ActionException.FailedException(e.toString())
    }

    /**
     * Interprets a throwable exception and converts it to an appropriate [ActionException].
     *
     * @param e [Throwable] The throwable to interpret.
     * @return [Throwable] The corresponding exception, which may be the original exception if
     *  already handled.
     */
    fun interpretActionException(e: Throwable): Throwable =
        when (e) {
            is CancellationException,
            is ActionException,
            -> e
            else -> ActionException.UnknownException(e)
        }
}
