package com.juanarton.core.data.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Result (
    val message: String,
    val success: Boolean
): Parcelable