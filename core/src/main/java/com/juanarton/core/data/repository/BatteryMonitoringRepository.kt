package com.juanarton.core.data.repository

import android.content.Context
import android.util.Log
import com.juanarton.core.data.domain.batteryMonitoring.repository.BatteryMonitoringRepoInterface
import com.juanarton.core.data.source.local.LocalDataSource
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class BatteryMonitoringRepository @Inject constructor(
    private val localDataSource: LocalDataSource
): BatteryMonitoringRepoInterface {
    override fun getDeepSleepInitialValue(): Long =
        localDataSource.getDeepSleepInitialValue()


    override fun insertDeepSleepInitialValue(deepSleepInitialVale: Long) {
        localDataSource.insertDeepSleepInitialValue(deepSleepInitialVale)
    }

    override fun getStartTime(): Date {
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
        return dateFormat.parse(localDataSource.getStartTime())
    }


    override fun insertStartTime(startTime: Date) {
        localDataSource.insertStartTime(startTime.toString())
    }

    override fun getScreenOnTime(): Long =
        localDataSource.getScreenOnTime()


    override fun insertScreenOnTime(seconds: Long) {
        localDataSource.insertScreenOnTime(seconds)
    }

    override fun getScreenOffTime(): Long =
        localDataSource.getScreenOffTime()

    override fun insertScreenOffTime(seconds: Long) {
        localDataSource.insertScreenOffTime(seconds)
    }

    override fun getCpuAwake(): Long =
        localDataSource.getCpuAwake()

    override fun insertCpuAwake(cpuAwake: Long) {
        localDataSource.insertCpuAwake(cpuAwake)
    }

    override fun getBatteryLevel(context: Context): Int =
        localDataSource.getBatteryLevel(context)

    override fun insertBatteryLevel(level: Int) {
        localDataSource.insertBatteryLevel(level)
    }

    override fun getInitialBatteryLevel(context: Context): Int =
        localDataSource.getInitialBatteryLevel(context)

    override fun insertInitialBatteryLevel(level: Int) {
        localDataSource.insertInitialBatteryLevel(level)
    }

    override fun getScreenOnDrain(): Int =
        localDataSource.getScreenOnDrain()

    override fun insertScreenOnDrain(level: Int) {
        localDataSource.insertScreenOnDrain(level)
    }

    override fun getScreenOffDrain(): Int =
        localDataSource.getScreenOffDrain()

    override fun insertScreenOffDrain(level: Int) {
        localDataSource.insertScreenOffDrain(level)
    }
}