/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction

import android.content.Context
import com.sudoplatform.sudologging.LogDriverInterface
import com.sudoplatform.sudologging.LogLevel
import com.sudoplatform.sudologging.Logger
import com.sudoplatform.sudoprivacyinteraction.api.ApiClient
import com.sudoplatform.sudoprivacyinteraction.rules.ActualPropertyResetter
import com.sudoplatform.sudoprivacyinteraction.rules.PropertyResetRule
import com.sudoplatform.sudoprivacyinteraction.rules.PropertyResetter
import com.sudoplatform.sudoprivacyinteraction.rules.TimberLogRule
import com.sudoplatform.sudouser.SudoUserClient
import org.junit.Rule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verifyNoMoreInteractions

/**
 * Base class that sets up:
 * - [TimberLogRule]
 * - [PropertyResetRule]
 *
 * And provides convenient access to the [PropertyResetRule.before] via [PropertyResetter.before].
 */
abstract class BaseTests : PropertyResetter by ActualPropertyResetter() {
    @Rule
    @JvmField
    val timberLogRule = TimberLogRule()

    private val mockLogDriver by before {
        mock<LogDriverInterface>().stub {
            on { logLevel } doReturn LogLevel.VERBOSE
        }
    }

    protected val mockLogger by before {
        Logger("mock", mockLogDriver)
    }

    protected val mockContext by before {
        mock<Context>()
    }

    protected open val mockUserClient by before {
        mock<SudoUserClient>()
    }

    protected open val mockApiClient by before {
        mock<ApiClient>()
    }

    // Common mock IDs and values used across tests.
    protected val mockVirtualPresenceId = "mockVirtualPresenceId"
    protected val mockOwner = "mockOwner"
    protected val mockIdentifier = "test@sudoplatform.com"
    protected val mockAuthCode = "mockAuthCode"
    protected val mockRedirectUri = "https://sudoplatform.com/redirect"
    protected val mockRefreshToken = "mockRefreshToken"
    protected val mockProviderIdentity = "mockProviderIdentity"
    protected val mockScopes = listOf("scope1", "scope2")
    protected val mockExpiresInEpochMs = 1000.0
}
