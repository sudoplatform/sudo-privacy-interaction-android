/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.types.inputs

/**
 * Options to control what `getDataHolder` returns.
 *
 * @property includeScanSummary [Boolean] Whether to also fetch and attach the most recent scan
 *  summary to the returned data holder. Defaults to `true`. When `true`, the data holder and its
 *  latest scan summary are fetched concurrently.
 */
data class GetDataHolderOptions(
    val includeScanSummary: Boolean = true,
)
