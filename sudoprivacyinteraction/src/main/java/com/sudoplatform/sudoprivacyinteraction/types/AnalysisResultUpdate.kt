/*
 * Copyright © 2026 Anonyome Labs, Inc. All rights reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sudoplatform.sudoprivacyinteraction.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * The Sudo Platform SDK representation of a lightweight notification of an analysis result update.
 *
 * @property id [String] Unique identifier of the analysis result.
 * @property owner [String] Unique identifier of the user associated with the analysis result.
 * @property virtualPresenceId [String] The virtual presence this analysis is associated with.
 * @property dataHolderIdentifier [String] The data holder domain that this analysis is about.
 * @property status [AnalysisResultStatus] Current status of the analysis.
 * @property lastAnalyzedAt [Date] Date for when the analysis was last performed.
 * @property version [Int] Version of this entity. Increments on update.
 * @property createdAt [Date] Date for when the analysis result was created.
 * @property updatedAt [Date] Date for when the analysis result was last updated.
 */
@Parcelize
data class AnalysisResultUpdate(
    val id: String,
    val owner: String,
    val virtualPresenceId: String,
    val dataHolderIdentifier: String,
    val status: AnalysisResultStatus,
    val lastAnalyzedAt: Date,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date,
) : Parcelable
