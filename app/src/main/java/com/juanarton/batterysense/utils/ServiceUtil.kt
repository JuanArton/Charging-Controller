package com.juanarton.batterysense.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.juanarton.batterysense.R
import com.juanarton.batterysense.broadcastreceiver.BatteryStateReceiver.Companion.RECEIVER_NOTIF_CHANNEL_ID
import com.juanarton.batterysense.utils.Utils.calculateCpuAwakePercentage
import com.juanarton.batterysense.utils.Utils.calculateDeepSleepPercentage
import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.utils.Utils
import com.juanarton.core.utils.Utils.formatTime
import java.util.Locale
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
        batteryInfo: BatteryInfo, deepSleep: Long, screenOnTime: Long, screenOffTime: Long, cpuAwake: Long,
        screenOnDrainPerHr: Double, screenOffDrainPerHr: Double, screenOnDrain: Int, screenOffDrain: Int,
        chargedLevel: Int, chargingPerHr: Double, chargingDuration: Long, isCharging: Boolean
    ): String {
        val content = if (!isCharging) {
            val deepSleepPercentage = calculateDeepSleepPercentage(
                deepSleep.toDouble(), screenOffTime.toDouble()
            )
            val cpuAwakePercentage = calculateCpuAwakePercentage(deepSleepPercentage)

            val screenOffDrainPerHrTmp = if (screenOffDrainPerHr.isNaN()) 0.0 else screenOffDrainPerHr
            val screenOnDrainPerHrTmp = if (screenOnDrainPerHr.isNaN()) 0.0 else screenOnDrainPerHr

            buildString {
                append("Power: ${String.format(
                    Locale.getDefault(), "%.2f", abs(batteryInfo.power)
                )}W\n")
                append("Screen on: ${formatTime(screenOnTime)}  ·  $screenOnDrain%\n")
                append("Screen off: ${formatTime(screenOffTime)}  ·  $screenOffDrain%\n")

                append("Deep sleep: ${formatTime(deepSleep)}  ·  ${String.format(
                    Locale.getDefault(), "%.1f", deepSleepPercentage
                )}%\n")

                append("CPU awake: ${formatTime(cpuAwake)}  ·  ${
                    String.format(Locale.getDefault(), "%.1f", cpuAwakePercentage)
                }%\n")

                append(
                    "Active drain: ${
                        String.format(Locale.getDefault(), "%.1f", screenOnDrainPerHrTmp)
                    }% /h · Idle drain: ${
                        String.format(Locale.getDefault(), "%.1f", screenOffDrainPerHrTmp)
                    }% /h")
            }
        } else {
            buildString {
                append("Power: ${
                    String.format(Locale.getDefault(), "%.2f", abs(batteryInfo.power))
                }W\n")

                append("Charged for: $chargedLevel%\n")

                append("Charging speed: ${
                    String.format(Locale.getDefault(), "%.1f", chargingPerHr)
                }% /h\n")

                append("Charging duration: ${formatTime(chargingDuration)}")
            }
        }

        return content
    }

    fun calculateTimeInterval(firstDate: Long, secondDate: Long): Long {
        return (firstDate - secondDate)/1000
    }

    fun createNotificationHigh(
        title: Int, context: Context, message: String, id: Int, name: Int, description: Int
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(RECEIVER_NOTIF_CHANNEL_ID, context.getString(name), NotificationManager.IMPORTANCE_HIGH)
            channel.description = context.getString(description)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, RECEIVER_NOTIF_CHANNEL_ID)
            .setSmallIcon(R.drawable.power)
            .setContentTitle(context.getString(title))
            .setContentText(message)
            .setShowWhen(false)

        notificationManager.notify(id, builder.build())
    }
}