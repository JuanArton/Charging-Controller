package com.juanarton.core.data.domain.batteryMonitoring.usecase

import android.content.Context
import java.util.Date

interface BatteryMonitoringRepoUseCase {
    fun getDeepSleepInitialValue(): Long

    fun insertDeepSleepInitialValue(deepSleepInitialVale: Long)

    fun getStartTime(): Date

    fun insertStartTime(startTime: Date)

    fun getScreenOnTime(): Long

    fun insertScreenOnTime(seconds: Long)

    fun getScreenOffTime(): Long

    fun insertScreenOffTime(seconds: Long)

    fun getCpuAwake(): Long

    fun insertCpuAwake(cpuAwake: Long)

    fun getBatteryLevel(context: Context): Int

    fun insertBatteryLevel(level: Int)

    fun getInitialBatteryLevel(context: Context): Int

    fun insertInitialBatteryLevel(level: Int)

    fun getScreenOnDrain(): Int

    fun insertScreenOnDrain(level: Int)

    fun getScreenOffDrain(): Int

    fun insertScreenOffDrain(level: Int)
}