package com.juanarton.core.data.repository

import android.content.Context
import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.data.domain.batteryMonitoring.repository.IBatteryMonitoringRepository
import com.juanarton.core.data.source.local.monitoring.LMonitoringDataSource
import com.juanarton.core.utils.DomainUtils.mapHistoryDomainToEntity
import com.juanarton.core.utils.DomainUtils.mapHistoryEntityToDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class BatteryMonitoringRepository @Inject constructor(
    private val lMonitoringDataSource: LMonitoringDataSource,
    private val context: Context
): IBatteryMonitoringRepository {
    override fun getBatteryInfo(): Flow<BatteryInfo> = flow {
        emit(lMonitoringDataSource.getBatteryInfo(context))
    }.flowOn(Dispatchers.IO)

    override fun getDeepSleepInitialValue(): Long =
        lMonitoringDataSource.getDeepSleepInitialValue()


    override fun insertDeepSleepInitialValue(deepSleepInitialVale: Long) {
        lMonitoringDataSource.insertDeepSleepInitialValue(deepSleepInitialVale)
    }

    override fun getStartTime(): Date {
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
        return dateFormat.parse(lMonitoringDataSource.getStartTime())
    }

    override fun insertStartTime(startTime: Date) {
        lMonitoringDataSource.insertStartTime(startTime.toString())
    }

    override fun getScreenOnTime(): Long =
        lMonitoringDataSource.getScreenOnTime()


    override fun insertScreenOnTime(seconds: Long) {
        lMonitoringDataSource.insertScreenOnTime(seconds)
    }

    override fun getScreenOffTime(): Long =
        lMonitoringDataSource.getScreenOffTime()

    override fun insertScreenOffTime(seconds: Long) {
        lMonitoringDataSource.insertScreenOffTime(seconds)
    }

    override fun getCpuAwake(): Long =
        lMonitoringDataSource.getCpuAwake()

    override fun insertCpuAwake(cpuAwake: Long) {
        lMonitoringDataSource.insertCpuAwake(cpuAwake)
    }

    override fun getBatteryLevel(context: Context): Int =
        lMonitoringDataSource.getBatteryLevel(context)

    override fun insertBatteryLevel(level: Int) {
        lMonitoringDataSource.insertBatteryLevel(level)
    }

    override fun getInitialBatteryLevel(context: Context): Int =
        lMonitoringDataSource.getInitialBatteryLevel(context)

    override fun insertInitialBatteryLevel(level: Int) {
        lMonitoringDataSource.insertInitialBatteryLevel(level)
    }

    override fun getScreenOnDrain(): Int =
        lMonitoringDataSource.getScreenOnDrain()

    override fun insertScreenOnDrain(level: Int) {
        lMonitoringDataSource.insertScreenOnDrain(level)
    }

    override fun getScreenOffDrain(): Int =
        lMonitoringDataSource.getScreenOffDrain()

    override fun insertScreenOffDrain(level: Int) {
        lMonitoringDataSource.insertScreenOffDrain(level)
    }

    override fun insertHistory(batteryHistory: BatteryHistory) {
        if (lMonitoringDataSource.getRowCount() >= 21600) {
            lMonitoringDataSource.deleteFirst(
                lMonitoringDataSource.getFirst()
            )
        }

        lMonitoringDataSource.insertHistory(
            mapHistoryDomainToEntity(batteryHistory)
        )
    }

    override fun getHistoryDataChunk(limit: Int, offset: Int): Flow<List<BatteryHistory>> =
        flow {
            emit(
                mapHistoryEntityToDomain(lMonitoringDataSource.getHistoryDataChunk(limit, offset))
            )
        }.flowOn(Dispatchers.IO)
}