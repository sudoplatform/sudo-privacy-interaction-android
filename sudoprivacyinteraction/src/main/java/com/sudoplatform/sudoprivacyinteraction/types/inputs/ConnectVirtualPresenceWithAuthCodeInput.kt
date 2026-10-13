/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.types.inputs

import com.sudoplatform.sudoprivacyinteraction.types.RelationshipProvider

/**
 * Input object containing the properties required to connect a virtual presence using an
 * authorization code obtained from an OAuth flow.
 *
 * @property authCode [String] An authorization code obtained from the OAuth flow.
 * @property redirectUri [String] The redirect URI that was used to obtain the authorization code.
 * @property relationshipProvider [RelationshipProvider] The relationship provider to associate
 *  with the connection.
 */
data class ConnectVirtualPresenceWithAuthCodeInput(
    val authCode: String,
    val redirectUri: String? = null,
    val relationshipProvider: RelationshipProvider? = null,
)
