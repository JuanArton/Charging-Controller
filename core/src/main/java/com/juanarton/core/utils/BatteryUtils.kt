package com.juanarton.core.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.SystemClock

object BatteryUtils {
    fun calculateDeepSleep(deepSleepInitialValue: Long): Long {
        return ((SystemClock.elapsedRealtime() - SystemClock.uptimeMillis()) / 1000 - deepSleepInitialValue)
    }

    fun getBatteryLevel(context: Context): Int {
        val batteryStatusIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, batteryStatusIntentFilter)
        val level = batteryStatus?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            (level.toFloat() / scale.toFloat() * 100).toInt()
        }

        return  level!!
    }
}