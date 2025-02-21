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
    val screenOn: Long?,
    val screenOff: Long?,
    val screenOnDrain: Int?,
    val screenOffDrain: Int?,
    val screenOffDrainPerHr: Double?,
    val screenOnDrainPerHr: Double?,
    val deepSleepPercentage: Double?,
    val awakePercentage: Double?,
    val awakeDuration: Long?,
    val sleepDuration: Long?,
    val awakeSpeed: Double?,
    val sleepSpeed: Double?,
    val chargingSpeed: Double?,
    val isCharging: Boolean
): Parcelable
