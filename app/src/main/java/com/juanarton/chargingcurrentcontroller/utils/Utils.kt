package com.juanarton.chargingcurrentcontroller.utils

object Utils {
    fun formatUsagePercentage(value1: Int, value2: Int): String {
        return buildString {
            append(value1)
            append("% of ")
            append(value2)
            append("%")
        }
    }

    fun formatUsagePerHour(value1: Double): String {
        return buildString {
            append(String.format("%.2f", value1))
            append("% /h")
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
            append("${String.format("%.2f", value)}% ")
            append("of screen off time")
        }
    }


}