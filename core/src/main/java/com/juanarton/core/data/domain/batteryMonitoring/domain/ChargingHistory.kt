package com.juanarton.core.data.domain.batteryMonitoring.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChargingHistory(
    val id: Int?,
    val startTime: Long,
    val endTime: Long,
    val startLevel: Int,
    val endLevel: Int,
    val levelDifference: Int,
    val isCharging: Boolean
): Parcelable
