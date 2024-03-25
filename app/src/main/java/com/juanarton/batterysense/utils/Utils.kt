package com.juanarton.batterysense.utils

object Utils {
    fun formatUsagePerHour(value1: Double): String {
        return buildString {
            append(String.format("%.1f", value1))
            append("%/h")
        }
    }

    fun calculateDeepSleepPercentage(deepSleep: Double, screenOffTime: Double): Double {
        val deepSleepPercentage = (deepSleep / screenOffTime) * 100
        return if (deepSleepPercentage.isNaN()) 0.0 else deepSleepPercentage
    }

    fun calculateCpuAwakePercentage(deepSleepPercentage: Double): Double {
        val cpuAwakePercentage = 100.0 - deepSleepPercentage
        return if (cpuAwakePercentage.isNaN()) 0.0 else cpuAwakePercentage
    }

    fun formatDeepSleepAwake(value: Double): String {
        return buildString {
            append("${String.format("%.1f", value)}% ")
            append("of screen off time")
        }
    }

    fun calculateDeepSleepAwakeSpeed(value: Double, screenOffTime: Double): Double {
        return screenOffTime * (value / 100)
    }
}