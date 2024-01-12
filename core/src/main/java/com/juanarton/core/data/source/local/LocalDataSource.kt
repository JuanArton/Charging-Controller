package com.juanarton.core.data.source.local

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.juanarton.core.utils.BatteryUtils
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(
        "batteryMonitoringData", Context.MODE_PRIVATE
    )
    private val editor = sharedPreferences.edit()

    fun getDeepSleepInitialValue(): Long =
        sharedPreferences.getLong("deepSleepInitialValue", 0)

    fun insertDeepSleepInitialValue(deepSleepInitialVale: Long) {
        editor.putLong("deepSleepInitialValue", deepSleepInitialVale)
        editor.apply()
    }

    fun getStartTime(): String =
        sharedPreferences.getString("startTime", null) ?: Date().toString()

    fun insertStartTime(startTime: String) {
        editor.putString("startTime", startTime)
        editor.apply()
    }

    fun getScreenOnTime(): Long =
        sharedPreferences.getLong("screenOnTime", 0)

    fun insertScreenOnTime(screenOnTime: Long) {
        editor.putLong("screenOnTime", screenOnTime)
        editor.apply()
    }

    fun getScreenOffTime(): Long =
        sharedPreferences.getLong("screenOffTime", 0)

    fun insertScreenOffTime(screenOffEntity: Long) {
        editor.putLong("screenOffTime", screenOffEntity)
        editor.apply()
    }
    fun getCpuAwake(): Long =
        sharedPreferences.getLong("deviceAwake", 0)

    fun insertCpuAwake(cpuAwake: Long) {
        editor.putLong("deviceAwake", cpuAwake)
        editor.apply()
    }

    fun getBatteryLevel(context: Context): Int {
        return sharedPreferences.getInt("batteryLevel", BatteryUtils.getBatteryLevel(context))
    }

    fun insertBatteryLevel(level: Int) {
        editor.putInt("batteryLevel", level)
        editor.apply()
    }

    fun getInitialBatteryLevel(context: Context): Int {
        return sharedPreferences.getInt("initialBatteryLevel", BatteryUtils.getBatteryLevel(context))
    }

    fun insertInitialBatteryLevel(level: Int) {
        editor.putInt("initialBatteryLevel", level)
        editor.apply()
    }

    fun getScreenOnDrain(): Int {
        return sharedPreferences.getInt("screenOnDrain", 0)
    }

    fun insertScreenOnDrain(level: Int) {
        editor.putInt("screenOnDrain", level)
        editor.apply()
    }

    fun getScreenOffDrain(): Int {
        return sharedPreferences.getInt("screenOffDrain", 0)
    }

    fun insertScreenOffDrain(level: Int) {
        editor.putInt("screenOffDrain", level)
        editor.apply()
    }
}