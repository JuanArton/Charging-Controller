package com.juanarton.core.data.domain.batteryMonitoring.usecase

import com.juanarton.core.data.domain.batteryMonitoring.repository.BatteryMonitoringRepoInterface
import java.util.Date
import javax.inject.Inject

class BatteryMonitoringRepoInteractor @Inject constructor(
    private val batteryMonitoringRepoInterface: BatteryMonitoringRepoInterface
): BatteryMonitoringRepoUseCase {
    override fun getDeepSleepInitialValue(): Long =
        batteryMonitoringRepoInterface.getDeepSleepInitialValue()

    override fun insertDeepSleepInitialValue(deepSleepInitialVale: Long) =
        batteryMonitoringRepoInterface.insertDeepSleepInitialValue(deepSleepInitialVale)

    override fun getStartTime(): Date =
        batteryMonitoringRepoInterface.getStartTime()

    override fun insertStartTime(startTime: Date) =
        batteryMonitoringRepoInterface.insertStartTime(startTime)

    override fun getScreenOnTime(): Long =
        batteryMonitoringRepoInterface.getScreenOnTime()

    override fun insertScreenOnTime(seconds: Long) =
        batteryMonitoringRepoInterface.insertScreenOnTime(seconds)

    override fun getScreenOffTime(): Long =
        batteryMonitoringRepoInterface.getScreenOffTime()

    override fun insertScreenOffTime(seconds: Long) =
        batteryMonitoringRepoInterface.insertScreenOffTime(seconds)

    override fun getDeepSleep(): Long =
        batteryMonitoringRepoInterface.getDeepSleep()

    override fun insertDeepSleep(cpuAwake: Long) {
        batteryMonitoringRepoInterface.insertDeepSleep(cpuAwake)
    }

    override fun getCpuAwake(): Long =
        batteryMonitoringRepoInterface.getCpuAwake()

    override fun insertCpuAwake(cpuAwake: Long) =
        batteryMonitoringRepoInterface.insertCpuAwake(cpuAwake)
}