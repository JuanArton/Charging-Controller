package com.juanarton.batterysense.utils

import android.content.Context
import com.juanarton.batterysense.R
import com.juanarton.batterysense.utils.Utils.calculateCpuAwakePercentage
import com.juanarton.batterysense.utils.Utils.calculateDeepSleepPercentage
import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.utils.Utils
import com.juanarton.core.utils.Utils.formatTime
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
        cpuAwake: Long, screenOnDrainPerHr: Double, screenOffDrainPerHr: Double, screenOnDrain: Int, screenOffDrain: Int
    ): String {
        val deepSleepPercentage = calculateDeepSleepPercentage(
            deepSleep.toDouble(), screenOffTime.toDouble()
        )
        val cpuAwakePercentage = calculateCpuAwakePercentage(deepSleepPercentage)

        val screenOffDrainPerHrTmp = if (screenOffDrainPerHr.isNaN()) 0.0 else screenOffDrainPerHr
        val screenOnDrainPerHrTmp = if (screenOnDrainPerHr.isNaN()) 0.0 else screenOnDrainPerHr

        return buildString {
            append("Power: ${String.format("%.2f", abs(batteryInfo.power))}W\n")
            append("Screen on: ${formatTime(screenOnTime)}  ·  $screenOnDrain%\n")
            append("Screen off: ${formatTime(screenOffTime)}  ·  $screenOffDrain%\n")
            append("Deep sleep: ${formatTime(deepSleep)}  ·  ${String.format("%.2f", deepSleepPercentage)}%\n")
            append("CPU awake: ${formatTime(cpuAwake)}  ·  ${String.format("%.2f", cpuAwakePercentage)}%\n")
            append("Active drain: ${String.format("%.2f", screenOnDrainPerHrTmp)}% /h · Idle drain: ${String.format("%.2f", screenOffDrainPerHrTmp)}% /h")
        }
    }

    fun calculateTimeInterval(firstDate: Date, secondDate: Date): Long {
        return (firstDate.time - secondDate.time) / 1000
    }
}