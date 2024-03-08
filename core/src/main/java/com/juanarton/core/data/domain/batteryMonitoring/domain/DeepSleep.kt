package com.juanarton.core.data.domain.batteryMonitoring.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeepSleep(
    val id: Int,
    val deepSleepInitialValue: Long
): Parcelable
