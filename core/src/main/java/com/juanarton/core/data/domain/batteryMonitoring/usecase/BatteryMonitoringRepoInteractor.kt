package com.juanarton.core.data.domain.batteryMonitoring.usecase

import android.content.Context
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

    override fun getCpuAwake(): Long =
        batteryMonitoringRepoInterface.getCpuAwake()

    override fun insertCpuAwake(cpuAwake: Long) =
        batteryMonitoringRepoInterface.insertCpuAwake(cpuAwake)

    override fun getBatteryLevel(context: Context): Int =
        batteryMonitoringRepoInterface.getBatteryLevel(context)

    override fun insertBatteryLevel(level: Int) =
        batteryMonitoringRepoInterface.insertBatteryLevel(level)

    override fun getInitialBatteryLevel(context: Context): Int =
        batteryMonitoringRepoInterface.getBatteryLevel(context)

    override fun insertInitialBatteryLevel(level: Int) =
        batteryMonitoringRepoInterface.insertInitialBatteryLevel(level)

    override fun getScreenOnDrain(): Int =
        batteryMonitoringRepoInterface.getScreenOnDrain()

    override fun insertScreenOnDrain(level: Int) =
        batteryMonitoringRepoInterface.insertScreenOnDrain(level)

    override fun getScreenOffDrain(): Int =
        batteryMonitoringRepoInterface.getScreenOffDrain()

    override fun insertScreenOffDrain(level: Int) =
        batteryMonitoringRepoInterface.insertScreenOffDrain(level)
}