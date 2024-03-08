package com.juanarton.core.data.domain.batteryInfo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Config (
    val current: Float = 500F,
    val chargingSwitch: Boolean = false,
    val limitSwitch: Boolean = false,
    val chargingLimitTriggered:Boolean = false,
    val maxCapacity: Float = 50F,
): Parcelable