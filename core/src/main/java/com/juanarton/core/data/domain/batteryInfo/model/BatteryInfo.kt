package com.juanarton.core.data.domain.batteryInfo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BatteryInfo (
    val status: Int = 0,
    val acCharge: Boolean = false,
    val usbCharge: Boolean = false,
    val level: Int = 0,
    val voltage: Float = 0.0f,
    val currentNow: Int = 0,
    val power: Float = 0.0f,
    val temperature: Int = 0
): Parcelable
