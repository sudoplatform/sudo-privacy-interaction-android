/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.types.inputs

import com.sudoplatform.sudoprivacyinteraction.types.RelationshipProvider

/**
 * Input object containing the properties required to connect a virtual presence using a
 * pre-obtained refresh token.
 *
 * @property refreshToken [String] The refresh token value.
 * @property providerIdentity [String] The identity associated with the provider.
 * @property scopes [List] Optional OAuth scopes associated with the token.
 * @property expiresInEpochMs [Double] Optional relative expiry: the number of milliseconds after
 *  the request at which the credential expires. Ignored when [expiresAtEpochMs] is provided.
 * @property relationshipProvider [RelationshipProvider] The relationship provider to associate
 *  with the connection.
 * @property expiresAtEpochMs [Double] Optional absolute expiry of the credential, in milliseconds
 *  since the Unix epoch. Takes precedence over [expiresInEpochMs] when both are provided. Defaults
 *  to one year after the request when neither is provided.
 */
data class ConnectVirtualPresenceWithRefreshTokenInput(
    val refreshToken: String,
    val providerIdentity: String,
    val scopes: List<String>? = null,
    @Deprecated(
        message = "Use expiresAtEpochMs instead.",
        replaceWith = ReplaceWith("expiresAtEpochMs"),
    )
    val expiresInEpochMs: Double? = null,
    val relationshipProvider: RelationshipProvider? = null,
    val expiresAtEpochMs: Double? = null,
)
