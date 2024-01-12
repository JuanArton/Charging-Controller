package com.juanarton.core.data.source.local

import android.content.Context
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

    fun getDeepSleep(): Long =
        sharedPreferences.getLong("deepSleep", 0)

    fun insertDeepSleep(cpuAwake: Long) {
        editor.putLong("deepSleep", cpuAwake)
        editor.apply()
    }

    fun getCpuAwake(): Long =
        sharedPreferences.getLong("deviceAwake", 0)

    fun insertCpuAwake(cpuAwake: Long) {
        editor.putLong("deviceAwake", cpuAwake)
        editor.apply()
    }
}