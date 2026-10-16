/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.data.providerConfiguration.transformers

import android.util.Base64
import com.sudoplatform.sudoprivacyinteraction.SudoPrivacyInteractionClient
import com.sudoplatform.sudoprivacyinteraction.internal.data.virtualPresence.transformers.ProviderTypeTransformer
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.configuration.ProviderConfigurationEntity
import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.virtualPresence.ProviderTypeEntity
import com.sudoplatform.sudoprivacyinteraction.types.ProviderConfiguration
import org.json.JSONObject
import com.sudoplatform.sudoprivacyinteraction.graphql.type.ProviderType as ProviderTypeGraphQL

/**
 * Transformer responsible for decoding the opaque, base-64 encoded provider configuration data
 * returned by the service into a list of [ProviderConfigurationEntity].
 *
 * The decoded payload has the shape:
 * ```
 * { "providers": [ { "name": "...", "providerType": "EMAIL", "clientId": "..." } ] }
 * ```
 */
internal object ProviderConfigurationTransformer {
    private const val DECODE_ERROR_MSG = "provider configuration cannot be decoded"

    /**
     * Decodes the opaque, base-64 encoded provider configuration [data] into a list of domain
     * entities.
     *
     * @param data [String] The base-64 encoded provider configuration data.
     * @return The decoded list of [ProviderConfigurationEntity].
     */
    fun fromGraphQLToEntity(data: String): List<ProviderConfigurationEntity> {
        val decodedString =
            try {
                String(Base64.decode(data, Base64.DEFAULT), Charsets.UTF_8)
            } catch (e: Throwable) {
                throw SudoPrivacyInteractionClient.VirtualPresenceException.FailedException(
                    "$DECODE_ERROR_MSG: Base64 decoding failed: ${e.message}",
                    e,
                )
            }

        val providersArray =
            try {
                JSONObject(decodedString).getJSONArray("providers")
            } catch (e: Throwable) {
                throw SudoPrivacyInteractionClient.VirtualPresenceException.FailedException(
                    "$DECODE_ERROR_MSG: JSON parsing failed: ${e.message}",
                    e,
                )
            }

        return (0 until providersArray.length()).map { index ->
            val provider = providersArray.getJSONObject(index)
            ProviderConfigurationEntity(
                name = provider.getString("name"),
                providerType = providerTypeFromString(provider.getString("providerType")),
                clientId = provider.getString("clientId"),
            )
        }
    }

    private fun providerTypeFromString(value: String): ProviderTypeEntity {
        val graphQLType =
            try {
                ProviderTypeGraphQL.valueOf(value)
            } catch (e: IllegalArgumentException) {
                ProviderTypeGraphQL.UNKNOWN__
            }
        return ProviderTypeTransformer.fromGraphQLToEntity(graphQLType)
    }

    /**
     * Transform a [ProviderConfigurationEntity] to its public [ProviderConfiguration] type.
     */
    fun fromEntityToApi(entity: ProviderConfigurationEntity): ProviderConfiguration =
        ProviderConfiguration(
            name = entity.name,
            providerType = ProviderTypeTransformer.fromEntityToApi(entity.providerType),
            clientId = entity.clientId,
        )
}
