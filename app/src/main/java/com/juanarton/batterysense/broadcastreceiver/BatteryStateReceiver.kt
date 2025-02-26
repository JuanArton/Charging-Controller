package com.juanarton.batterysense.broadcastreceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.juanarton.batterysense.R
import com.juanarton.batterysense.batterymonitorservice.BatteryMonitorService
import com.juanarton.batterysense.batterymonitorservice.BatteryMonitorService.Companion.isFullyCharged
import com.juanarton.batterysense.utils.ChargingDataHolder.getChargedLevel
import com.juanarton.batterysense.utils.ChargingDataHolder.getChargingDuration
import com.juanarton.batterysense.utils.ChargingDataHolder.setChargedLevel
import com.juanarton.batterysense.utils.ChargingDataHolder.setChargingDuration
import com.juanarton.batterysense.utils.ChargingDataHolder.setChargingPerHr
import com.juanarton.batterysense.utils.ServiceUtil.createNotificationHigh
import com.juanarton.core.data.domain.batteryInfo.repository.IAppConfigRepository
import com.juanarton.core.data.domain.batteryMonitoring.repository.IBatteryMonitoringRepository
import com.juanarton.core.utils.BatteryUtils.getCurrentTimeMillis
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BatteryStateReceiver : BroadcastReceiver(){

    @Inject
    lateinit var iBatteryMonitoringRepository: IBatteryMonitoringRepository

    @Inject
    lateinit var iAppConfigRepository: IAppConfigRepository

    private var batteryUsed = 0
    private var temperature: Int = 0
    private var batteryTmp: Int = 0
    private var isNotified = false

    companion object {
        const val RECEIVER_NOTIF_CHANNEL_ID = "BatteryAlarmChannel"
    }
    override fun onReceive(context: Context, intent: Intent) {
        if (temperature == 0) {
            temperature = intent.getIntExtra("temperature", -1)/10
            batteryTmp = intent.getIntExtra("level", -1)
        }

        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            val batteryLevel = iBatteryMonitoringRepository.getBatteryLevel(context)
            val newLevel = intent.getIntExtra("level", -1)
            val newTemperature = intent.getIntExtra("temperature", -1)/10


            if (newLevel < batteryLevel) {
                batteryUsed = batteryLevel - newLevel

                when(BatteryMonitorService.screenOn) {
                    true -> {
                        val drain = iBatteryMonitoringRepository.getScreenOnDrain()
                        iBatteryMonitoringRepository.insertScreenOnDrain(drain + batteryUsed)
                    }
                    false -> {
                        val drain = iBatteryMonitoringRepository.getScreenOffDrain()
                        iBatteryMonitoringRepository.insertScreenOffDrain(drain + batteryUsed)
                    }
                }
                iBatteryMonitoringRepository.insertBatteryLevel(newLevel)
                batteryUsed = 0
            }
            else if (newLevel > batteryLevel) {
                if (!isFullyCharged) {
                    setChargingDuration((getCurrentTimeMillis() - iBatteryMonitoringRepository.getLastPlugged().first) / 1000 )
                    setChargedLevel(newLevel - iBatteryMonitoringRepository.getLastPlugged().second)
                    setChargingPerHr(((getChargedLevel().toDouble() / getChargingDuration()) * 3600))
                }
            }

            if (newLevel > iAppConfigRepository.getBatteryLevelThreshold().first &&
                newLevel < iAppConfigRepository.getBatteryLevelThreshold().second) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(2)
                isNotified = false
            }

            if (newLevel != batteryTmp) {
                checkBatteryLevelAlarm(context, newLevel)
                batteryTmp = newLevel
            }

            if (newTemperature != temperature) {
                checkBatteryTemperatureAlarm(context, newTemperature)
                temperature = newTemperature
            }
        }
    }

    private fun checkBatteryLevelAlarm(context: Context, level: Int) {
        if (iAppConfigRepository.getBatteryLevelAlarmStatus() && !isNotified) {
            if (level <= iAppConfigRepository.getBatteryLevelThreshold().first) {
                val message = buildString {
                    append("$level% - ${context.getString(R.string.batteryLevelMinReached)}")
                }
                createNotificationHigh(
                    R.string.batteryLevelAlarmNotifTitle,
                    context,
                    message,
                    2,
                    R.string.bs_battery_alarm,
                    R.string.battery_state_alarm
                )
                if (iAppConfigRepository.getOneTimeAlarmStatus()) { isNotified = true }
            } else if (level >= iAppConfigRepository.getBatteryLevelThreshold().second) {
                val message = buildString {
                    append("$level% - ${context.getString(R.string.batteryLevelMaxReached)}")
                }
                createNotificationHigh(
                    R.string.batteryLevelAlarmNotifTitle,
                    context,
                    message,
                    2,
                    R.string.bs_battery_alarm,
                    R.string.battery_state_alarm
                )
                if (iAppConfigRepository.getOneTimeAlarmStatus()) { isNotified = true }
            }
        }
    }

    private fun checkBatteryTemperatureAlarm(context: Context, temp: Int) {
        if (iAppConfigRepository.getBatteryTemperatureAlarmStatus()) {
            if (temp >= iAppConfigRepository.getBatteryTemperatureThreshold()) {
                val message = buildString {
                    append(
                        "$temp${context.getString(R.string.degree_symbol)} - ${context.getString(R.string.batteryTemperatureReached)}"
                    )
                }
                createNotificationHigh(
                    R.string.batteryLevelAlarmNotifTitle,
                    context,
                    message,
                    3,
                    R.string.bs_battery_alarm,
                    R.string.battery_state_alarm
                )
            }
        }
    }
}