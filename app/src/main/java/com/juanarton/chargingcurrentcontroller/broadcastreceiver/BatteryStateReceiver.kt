package com.juanarton.chargingcurrentcontroller.broadcastreceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.juanarton.chargingcurrentcontroller.R
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.BatteryMonitorService
import com.juanarton.core.data.domain.batteryInfo.repository.BatteryInfoRepositoryInterface
import com.juanarton.core.data.domain.batteryMonitoring.repository.BatteryMonitoringRepoInterface
import com.juanarton.core.data.repository.BatteryInfoRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BatteryStateReceiver : BroadcastReceiver(){

    @Inject
    lateinit var batteryMonitoringRepoInterface: BatteryMonitoringRepoInterface

    @Inject
    lateinit var batteryInfoRepository: BatteryInfoRepositoryInterface

    private var batteryUsed = 0

    companion object {
        const val RECEIVER_NOTIF_CHANNEL_ID = "BatteryAlarmChannel"
        const val RECEIVER_NOTIFICATION_ID = 2
    }
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            val batteryLevel = batteryMonitoringRepoInterface.getBatteryLevel(context)
            val newLevel = intent.getIntExtra("level", -1)
            val temperature = intent.getIntExtra("temperature", -1)/10

            if (newLevel < batteryLevel) {
                batteryUsed = batteryLevel - newLevel

                when(BatteryMonitorService.screenOn) {
                    true -> {
                        val drain = batteryMonitoringRepoInterface.getScreenOnDrain()
                        batteryMonitoringRepoInterface.insertScreenOnDrain(drain + batteryUsed)
                    }
                    false -> {
                        val drain = batteryMonitoringRepoInterface.getScreenOffDrain()
                        batteryMonitoringRepoInterface.insertScreenOffDrain(drain + batteryUsed)
                    }
                }
                batteryMonitoringRepoInterface.insertBatteryLevel(newLevel)
                batteryUsed = 0
            }

            checkBatteryLevelAlarm(context, newLevel)
            checkBatteryTemperatureAlarm(context, temperature)

        }
    }

    private fun checkBatteryLevelAlarm(context: Context, level: Int) {
        if (batteryInfoRepository.getBatteryLevelAlarmStatus()) {
            if (level <= batteryInfoRepository.getBatteryLevelThreshold().first) {
                val message = buildString {
                    append("$level - ${context.getString(R.string.batteryLevelMinReached)}")
                }
                createNotification(
                    context,
                    message
                )
            } else if (level >= batteryInfoRepository.getBatteryLevelThreshold().second) {
                val message = buildString {
                    append("$level - ${context.getString(R.string.batteryLevelMaxReached)}")
                }
                createNotification(
                    context,
                    message
                )
            }
        }
    }

    private fun checkBatteryTemperatureAlarm(context: Context, temp: Int) {
        if (batteryInfoRepository.getBatteryTemperatureAlarmStatus()) {
            Log.d("test", temp.toString())
            if (temp >= batteryInfoRepository.getBatteryTemperatureThreshold()) {
                val message = buildString {
                    append("$temp - ${context.getString(R.string.batteryTemperatureReached)}")
                }
                createNotification(
                    context,
                    message
                )
            }
        }
    }

    private fun createNotification(context: Context, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(RECEIVER_NOTIF_CHANNEL_ID, "3C Battery Alarm", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Battery State Alarm"
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, RECEIVER_NOTIF_CHANNEL_ID)
            .setSmallIcon(R.drawable.power)
            .setContentTitle(context.getString(R.string.batteryLevelAlarmNotifTitle))
            .setContentText(message)
            .setShowWhen(false)

        notificationManager.notify(RECEIVER_NOTIFICATION_ID, builder.build())
    }

}