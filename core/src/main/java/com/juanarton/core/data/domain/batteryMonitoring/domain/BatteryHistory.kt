package com.juanarton.core.data.domain.batteryMonitoring.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BatteryHistory(
    val timestamp: Long,

    val level: Int,

    val current: Int,

    val temperature: Int,

    val power: Float,

    val voltage: Float,
) : Parcelable