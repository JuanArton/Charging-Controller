package com.juanarton.chargingcurrentcontroller.utils

import android.content.Context
import com.juanarton.chargingcurrentcontroller.R
import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.utils.Utils
import java.util.Date
import kotlin.math.abs

object ServiceUtil {
    fun buildTitle(batteryInfo: BatteryInfo, context: Context): String {
        return buildString {
            append("${batteryInfo.level}%")
            append(" · ")
            append("${batteryInfo.temperature}${context.applicationContext.getString(R.string.degree_symbol)}")
            append(" · ")
            append("${batteryInfo.currentNow} ${context.applicationContext.getString(R.string.ma)}")
            append(" · ")
            append(Utils.mapBatteryStatus(batteryInfo.status, context))
        }
    }

    fun buildContent(
        batteryInfo: BatteryInfo,
        deepSleep: Long,
        screenOnTime: Long,
        screenOffTime: Long,
        cpuAwake: Long
    ): String {
        return buildString {
            append("Power: ${String.format("%.2f", abs(batteryInfo.power))}W\n")
            append("Screen on: ${formatTime(screenOnTime)}\n")
            append("Screen off: ${formatTime(screenOffTime)}\n")
            append("Deep sleep: ${formatTime(deepSleep)}\n")
            append("CPU awake: ${formatTime(cpuAwake)}\n")
        }
    }

    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return when {
            hours > 0 -> String.format("%2dh %2dm %2ds", hours, minutes, remainingSeconds)
            minutes > 0 -> String.format("%2dm %2ds", minutes, remainingSeconds)
            else -> String.format("%2ds", remainingSeconds)
        }
    }

    fun calculateTimeInterval(firstDate: Date, secondDate: Date): Long {
        return (firstDate.time - secondDate.time) / 1000

    }
}