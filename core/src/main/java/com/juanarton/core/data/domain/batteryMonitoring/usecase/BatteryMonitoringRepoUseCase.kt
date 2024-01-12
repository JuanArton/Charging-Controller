package com.juanarton.core.data.domain.batteryMonitoring.usecase

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

    fun getDeepSleep(): Long

    fun insertDeepSleep(cpuAwake: Long)

    fun getCpuAwake(): Long

    fun insertCpuAwake(cpuAwake: Long)
}