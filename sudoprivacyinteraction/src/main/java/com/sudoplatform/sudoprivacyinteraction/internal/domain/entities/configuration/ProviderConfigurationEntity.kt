package com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.configuration

import com.sudoplatform.sudoprivacyinteraction.internal.domain.entities.ProviderTypeEntity

/**
 * Core entity representation of a provider configuration.
 *
 * Contains the configuration required by consumers to support the provider OAuth flow. This is
 * the decoded form of the opaque configuration data returned by the service.
 *
 * @property name [String] Human-readable provider name (e.g. `google`).
 * @property providerType [ProviderTypeEntity] The kind of virtual presence this provider backs
 *  (e.g. `EMAIL`).
 * @property clientId [String] OAuth client id.
 */
internal data class ProviderConfigurationEntity(
    val name: String,
    val providerType: ProviderTypeEntity,
    val clientId: String,
)
