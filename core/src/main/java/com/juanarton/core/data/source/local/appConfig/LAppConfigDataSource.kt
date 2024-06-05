package com.juanarton.core.data.source.local.appConfig

import android.content.Context
import com.juanarton.core.data.domain.batteryInfo.model.Config
import com.juanarton.core.data.domain.batteryInfo.model.Result
import com.juanarton.core.utils.Utils
import com.topjohnwu.superuser.Shell
import javax.inject.Singleton

@Singleton
class LAppConfigDataSource {

    companion object {
        const val PATH = "/data/adb/modules/3C"
        const val BATTERY_MIN_LEVEL = "levelMin"
        const val BATTERY_MAX_LEVEL = "levelMax"
        const val BATTERY_LEVEL_ALARM_KEY = "levelAlarm"
        const val BATTERY_ALARM_PREF = "BatteryAlarmPref"
        const val BATTERY_TEMPERATURE_ALARM_KEY = "temperatureAlarm"
        const val BATTERY_MAX_TEMP = "tempMax"
        const val ONE_TIME_ALARM_KEY = "oneTimeAlarm"
    }

    fun getConfig(): Config {
        val targetCurrentCommand =
            Utils.getValue("chargingCurrent").out[0]

        val chargingSwitchCommand =
            Utils.getValue("enableCharging").out[0] == "0"

        val enableLimitCharging =
            Utils.getValue("enableLimitCharging").out[0] == "1"

        val chargingLimitTriggered =
            Utils.getValue("chargingLimitTriggered").out[0] == "1"

        val maxCapacity = Utils.getValue("maxCapacity").out[0]

        return Config(
            targetCurrentCommand.toFloat(),
            chargingSwitchCommand,
            enableLimitCharging,
            chargingLimitTriggered,
            maxCapacity.toFloat()
        )
    }

    fun setTargetCurrent(targetCurrent: String): Result {
        val command = "${PATH}/3c.sh setValue chargingCurrent $targetCurrent"
        val result = Shell.cmd(command).exec()

        val readResult = Utils.getValue("chargingCurrent")
        val value = readResult.out[0]

        return if (result.isSuccess) {
            if (value == targetCurrent) {
                Shell.cmd("${PATH}/3c.sh restartCurrentController").exec()
                Result("Success", true)
            } else {
                Result("Failed to set new target Current", false)
            }
        } else {
            Result("Error: ${result.err}", true)
        }
    }

    fun setChargingSwitchStatus(switchStat: Boolean): Result {
        val stat = if (switchStat) "0" else "1"

        val command = "${PATH}/3c.sh setValue enableCharging $stat"
        val result = Shell.cmd(command).exec()

        val readResult = Utils.getValue("enableCharging")
        val value = readResult.out[0] == "1"

        return if (result.isSuccess) {
            if(value != switchStat) {
                Shell.cmd("${PATH}/3c.sh applyChargingSwitch").exec()
                Result("Success", true)
            } else {
                Result("Failed to switch charging", false)
            }
        } else {
            Result("Error: ${result.err}", false)
        }
    }

    fun setChargingLimitStatus(switchStat: Boolean): Result {
        val stat = if (switchStat) "1" else "0"

        val command = "${PATH}/3c.sh setValue enableLimitCharging $stat"
        val result = Shell.cmd(command).exec()

        val readStatus = Utils.getValue("enableLimitCharging")
        val statusValue = readStatus.out[0] == "1"

        return if (result.isSuccess) {
            if(statusValue == switchStat) {
                if(statusValue) {
                    Shell.cmd("${PATH}/3c.sh restartBatteryMonitor").exec()
                } else {
                    Shell.cmd("${PATH}/3c.sh killBateryMonitor").exec()
                }
                Result("Success", true)
            } else {
                Result("Failed to switch charging limit", false)
            }
        } else {
            Result("Error: ${result.err}", false)
        }
    }

    fun setMaximumCapacity(maxCapacity: String): Result {
        val command = "${PATH}/3c.sh setValue maxCapacity ${maxCapacity.toInt()}"
        val result = Shell.cmd(command).exec()

        val capacityValue = Utils.getValue("maxCapacity").out[0]

        return if (result.isSuccess) {
            if(capacityValue == maxCapacity.toInt().toString()) {
                Shell.cmd("${PATH}/3c.sh restartBatteryMonitor").exec()
                Result("Success", true)
            } else {
                Result("Failed to switch charging limit", false)
            }
        } else {
            Result("Error: ${result.err}", false)
        }
    }

    fun setBatteryLevelThreshold(min: Int, max: Int, context: Context): Boolean {
        return try {
            val sharedPreferences = context.getSharedPreferences(BATTERY_ALARM_PREF, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt(BATTERY_MIN_LEVEL, min)
            editor.putInt(BATTERY_MAX_LEVEL, max)
            editor.apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getBatteryLevelThreshold(context: Context): Pair<Int, Int> {
        val sharedPreferences = context.getSharedPreferences(BATTERY_ALARM_PREF, Context.MODE_PRIVATE)

        val min = sharedPreferences.getInt(BATTERY_MIN_LEVEL, 20)

        val max = sharedPreferences.getInt(BATTERY_MAX_LEVEL, 80)

        return Pair(min, max)
    }

    fun setBatteryLevelAlarmStatus(value: Boolean, context: Context): Boolean {
        return try {
            val sharedPreferences = context.getSharedPreferences("AlarmStatus", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(BATTERY_LEVEL_ALARM_KEY, value).apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getBatteryLevelAlarmStatus(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("AlarmStatus", Context.MODE_PRIVATE)

        return sharedPreferences.getBoolean(BATTERY_LEVEL_ALARM_KEY, false)
    }

    fun setBatteryTemperatureThreshold(temperature: Int, context: Context): Boolean {
        return try {
            val sharedPreferences = context.getSharedPreferences(BATTERY_ALARM_PREF, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt(BATTERY_MAX_TEMP, temperature)
            editor.apply()

            true
        } catch (e: Exception) {
            false
        }
    }

    fun getBatteryTemperatureThreshold(context: Context): Int {
        val sharedPreferences =
            context.getSharedPreferences(BATTERY_ALARM_PREF, Context.MODE_PRIVATE)

        return sharedPreferences.getInt(BATTERY_MAX_TEMP, 38)
    }

    fun setBatteryTemperatureAlarmStatus(value: Boolean, context: Context): Boolean {
        return try {
            val sharedPreferences = context.getSharedPreferences("AlarmStatus", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(BATTERY_TEMPERATURE_ALARM_KEY, value).apply()

            true
        } catch (e: Exception) {
            false
        }
    }

    fun getBatteryTemperatureAlarmStatus(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("AlarmStatus", Context.MODE_PRIVATE)

        return sharedPreferences.getBoolean(BATTERY_TEMPERATURE_ALARM_KEY, false)
    }

    fun setOneTimeAlarmStatus(value: Boolean, context: Context): Boolean {
        return try {
            val sharedPreferences = context.getSharedPreferences("AlarmStatus", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(ONE_TIME_ALARM_KEY, value).apply()

            true
        } catch (e: Exception) {
            false
        }
    }

    fun getOneTimeAlarmStatus(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("AlarmStatus", Context.MODE_PRIVATE)

        return sharedPreferences.getBoolean(ONE_TIME_ALARM_KEY, false)
    }
}