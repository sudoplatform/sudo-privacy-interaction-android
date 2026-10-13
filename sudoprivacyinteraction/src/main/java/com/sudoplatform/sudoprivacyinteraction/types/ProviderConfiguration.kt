/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * The Sudo Platform SDK representation of a provider client configuration.
 *
 * Contains the configuration required by consumers to support the provider OAuth flow. This is
 * the decoded form of the opaque configuration data returned by the service.
 *
 * @property name [String] Human-readable provider name (e.g. `google`).
 * @property providerType [ProviderType] The kind of virtual presence this provider backs
 *  (e.g. `EMAIL`).
 * @property clientId [String] OAuth client id.
 */
@Parcelize
data class ProviderConfiguration(
    val name: String,
    val providerType: ProviderType,
    val clientId: String,
) : Parcelable
