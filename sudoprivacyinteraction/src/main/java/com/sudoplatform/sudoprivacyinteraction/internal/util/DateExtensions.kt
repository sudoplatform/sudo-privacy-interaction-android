/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.internal.util

import java.util.Date

/**
 * Extensions used by several data transformers to convert epoch millisecond values
 * (represented as [Double] in the GraphQL schema) to [Date].
 */
internal fun Double.toDate(): Date = Date(this.toLong())

internal fun Double?.toDate(): Date? {
    if (this == null) {
        return null
    }
    return Date(this.toLong())
}
