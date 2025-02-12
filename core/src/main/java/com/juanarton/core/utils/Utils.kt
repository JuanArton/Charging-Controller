package com.juanarton.core.utils

import android.content.Context
import com.juanarton.core.R
import com.juanarton.core.data.source.local.appConfig.LAppConfigDataSource.Companion.PATH
import com.topjohnwu.superuser.Shell
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Utils {
    fun getValue(config: String): Shell.Result {
        val command =
            "grep '$config' ${PATH}/3C.conf | awk -F '=' '{print \$2}' | tr -d ' '"

        return Shell.cmd(command).exec()
    }

    fun mapBatteryStatus(status: Int, context: Context): String {
        return when (status){
            1 -> context.getString(R.string.unknown)
            2 -> context.getString(R.string.charging)
            3 -> context.getString(R.string.discharging)
            4 -> context.getString(R.string.not_charging)
            else -> context.getString(R.string.full)
        }
    }

    fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return when {
            hours > 0 -> String.format(Locale.getDefault(), "%1dh %1dm %1ds", hours, minutes, remainingSeconds)
            minutes > 0 -> String.format(Locale.getDefault(), "%1dm %1ds", minutes, remainingSeconds)
            else -> String.format(Locale.getDefault(), "%1ds", remainingSeconds)
        }
    }

    fun convertMillisToDateTime(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return sdf.format(calendar.time)
    }

    fun convertMillisToDateTimeSecond(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMM HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return sdf.format(calendar.time)
    }

    fun convertMillisToHourTime(millis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return sdf.format(calendar.time)
    }
}