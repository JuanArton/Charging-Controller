package com.juanarton.core.utils

import android.os.SystemClock

object BatteryUtils {
    fun calculateDeepSleep(deepSleepInitialValue: Long): Long {
        return ((SystemClock.elapsedRealtime() - SystemClock.uptimeMillis()) / 1000 - deepSleepInitialValue)
    }
}