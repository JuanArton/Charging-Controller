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
        batteryInfo: BatteryInfo, deepSleep: Long, screenOnTime: Long, screenOffTime: Long,
        cpuAwake: Long, screenOnDrainPerHr: Double, screenOffDrainPerHr: Double, screenOnDrain:Int, screenOffDrain: Int
    ): String {
        val deepSleepPercentage = (deepSleep.toDouble()/screenOffTime.toDouble())*100
        val cpuAwakePercentage = 100.0 - deepSleepPercentage
        return buildString {
            append("Power: ${String.format("%.2f", abs(batteryInfo.power))}W\n")
            append("Screen on: ${formatTime(screenOnTime)}  ·  $screenOnDrain%\n")
            append("Screen off: ${formatTime(screenOffTime)}  ·  $screenOffDrain%\n")
            append("Deep sleep: ${formatTime(deepSleep)}  ·  ${String.format("%.2f", deepSleepPercentage)}%\n")
            append("CPU awake: ${formatTime(cpuAwake)}  ·  ${String.format("%.2f", cpuAwakePercentage)}%\n")
            append("Active drain: ${String.format("%.2f", screenOnDrainPerHr)}% /h · Idle drain: ${String.format("%.2f", screenOffDrainPerHr)}% /h")
        }
    }

    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return when {
            hours > 0 -> String.format("%1dh %1dm %1ds", hours, minutes, remainingSeconds)
            minutes > 0 -> String.format("%1dm %1ds", minutes, remainingSeconds)
            else -> String.format("%1ds", remainingSeconds)
        }
    }

    fun calculateTimeInterval(firstDate: Date, secondDate: Date): Long {
        return (firstDate.time - secondDate.time) / 1000
    }
}