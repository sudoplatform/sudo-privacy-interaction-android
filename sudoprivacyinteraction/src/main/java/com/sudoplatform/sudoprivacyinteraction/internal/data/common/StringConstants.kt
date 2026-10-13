/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.common

/**
 * Constants used across the data layer, primarily for interpreting service errors.
 */
internal object StringConstants {
    /** Key under which the service error type is provided in a GraphQL error's extensions. */
    const val ERROR_TYPE = "errorType"

    /** Errors returned from the service */
    const val ERROR_INVALID_ARGUMENT = "InvalidArgument"
    const val ERROR_IDENTITY_CONTEXT_MISSING = "IdentityContextMissing"
    const val ERROR_INVALID_AUTH_CODE = "InvalidAuthCode"
    const val ERROR_MISSING_REFRESH_TOKEN = "MissingRefreshToken"
    const val ERROR_ACCESS_TOKEN_REFRESH_FAILED = "AccessTokenRefreshFailed"
    const val ERROR_VIRTUAL_PRESENCE_NOT_FOUND = "VirtualPresenceNotFound"
    const val ERROR_DATA_HOLDER_NOT_FOUND = "DataHolderNotFound"
    const val ERROR_ANALYSIS_RESULT_NOT_FOUND = "AnalysisResultNotFound"
    const val ERROR_ACTION_NOT_FOUND = "ActionNotFound"

    /** Exception messages */
    const val INVALID_ARGUMENT_ERROR_MSG = "Invalid input"
    const val IDENTITY_CONTEXT_MISSING_MSG = "Identity context is missing"
    const val INVALID_AUTH_CODE_MSG = "The provided authorization code is invalid"
    const val MISSING_REFRESH_TOKEN_MSG = "A refresh token is required but was not provided"
    const val ACCESS_TOKEN_REFRESH_FAILED_MSG = "Failed to refresh the access token"
    const val VIRTUAL_PRESENCE_NOT_FOUND_MSG = "Virtual presence not found"
    const val NO_VIRTUAL_PRESENCE_ERROR_MSG = "No virtual presence returned."
    const val NO_PROVIDER_CONFIGURATION_ERROR_MSG = "No provider configuration returned."
    const val DATA_HOLDER_NOT_FOUND_MSG = "Data holder not found"
    const val ANALYSIS_RESULT_NOT_FOUND_MSG = "Analysis result not found"
    const val ACTION_NOT_FOUND_MSG = "Action not found"
    const val DATA_HOLDER_NOT_FOUND_FOR_ACTION_MSG = "Data holder not found"
    const val NO_ACTION_ERROR_MSG = "No action returned."
}
