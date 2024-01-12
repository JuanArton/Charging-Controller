package com.juanarton.core.data.repository

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

    override fun getDeepSleep(): Long {
        Log.d("test", "test")
        return 0
    }

    override fun insertDeepSleep(cpuAwake: Long) {
        Log.d("test", "test")
    }

    override fun getCpuAwake(): Long =
        localDataSource.getCpuAwake()

    override fun insertCpuAwake(cpuAwake: Long) {
        localDataSource.insertCpuAwake(cpuAwake)
    }
}