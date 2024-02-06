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

}