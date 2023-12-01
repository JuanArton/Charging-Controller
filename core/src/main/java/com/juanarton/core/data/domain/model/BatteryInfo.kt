package com.juanarton.core.data.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BatteryInfo (
    val status: Int,
    val acCharge: Boolean,
    val usbCharge: Boolean,
    val level: Int,
    val voltage: Float,
    val currentNow: Int,
    val power: Float,
    val temperature: Int,
): Parcelable