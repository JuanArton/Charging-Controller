package com.juanarton.core.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import com.juanarton.core.data.domain.batteryMonitoring.repository.IBatteryMonitoringRepository
import com.juanarton.core.data.source.local.monitoring.LMonitoringDataSource
import com.juanarton.core.utils.DomainUtils.mapBatteryHistoryDomainToEntity
import com.juanarton.core.utils.DomainUtils.mapBatteryHistoryEntityToDomain
import com.juanarton.core.utils.DomainUtils.mapChargingHistoryDomainToEntity
import com.juanarton.core.utils.DomainUtils.mapChargingHistoryEntityToDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
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

    override fun getStartTime(): Long {
        return lMonitoringDataSource.getStartTime()
    }

    override fun insertStartTime(startTime: Long) {
        lMonitoringDataSource.insertStartTime(startTime)
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
        if (lMonitoringDataSource.getRowCount() >= 120960) {
            lMonitoringDataSource.deleteFirst(
                lMonitoringDataSource.getFirst()
            )
        }

        lMonitoringDataSource.insertHistory(
            mapBatteryHistoryDomainToEntity(batteryHistory)
        )
    }

    override fun getHistoryDataChunk(limit: Int, offset: Int): Flow<List<BatteryHistory>> =
        flow {
            emit(
                mapBatteryHistoryEntityToDomain(
                    lMonitoringDataSource.getHistoryDataChunk(limit, offset)
                )
            )
        }.flowOn(Dispatchers.IO)

    override fun getLastPlugged(): Pair<Long, Int> =
        lMonitoringDataSource.getLastPlugged()

    override fun insertLastPlugged(lastPlugged: Long, lastPluggedLevel: Int) {
        lMonitoringDataSource.insertLastPlugged(lastPlugged, lastPluggedLevel)
    }

    override fun getLastUnplugged(): Pair<Long, Int> =
        lMonitoringDataSource.getLastUnplugged()

    override fun insertLastUnplugged(lastUnplugged: Long, lastUnpluggedLevel: Int) {
        lMonitoringDataSource.insertLastUnpPlugged(lastUnplugged, lastUnpluggedLevel)
    }

    override fun getChargingHistory(): Flow<PagingData<ChargingHistory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 4,
                enablePlaceholders = false,
                initialLoadSize = 4
            ),
            pagingSourceFactory = {
                lMonitoringDataSource.getChargingHistory()
            }
        ).flow
    }

    override fun insertChargingHistory(chargingHistory: ChargingHistory) {
        lMonitoringDataSource.insertChargingHistory(
            mapChargingHistoryDomainToEntity(chargingHistory)
        )
    }

    override fun getRawCurrent(): Int =
        lMonitoringDataSource.getRawCurrent()

    override fun getCurrentUnit(): String =
        lMonitoringDataSource.getCurrentUnit()

    override fun insertCurrentUnit(currentUnit: String) {
        lMonitoringDataSource.insertCurrentUnit(currentUnit)
    }

    override fun getCapacity(): Int =
        lMonitoringDataSource.getCapacity()

    override fun insertCapacity(capacity: Int) {
        lMonitoringDataSource.insertCapacity(capacity)
    }

    override fun deleteChargingHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            lMonitoringDataSource.deleteChargingHistory()
        }
    }

    override fun getUsageData(): List<BatteryHistory> {
        return mapBatteryHistoryEntityToDomain(lMonitoringDataSource.getUsageData())
    }

    override fun getAvailableDays(): Flow<List<String>> = flow {
        emit(
            lMonitoringDataSource.getAvailableDays()
        )
    }.flowOn(Dispatchers.IO)

    override fun getDataByDay(selectedDay: String): Flow<List<BatteryHistory>> = flow {
        emit(
            mapBatteryHistoryEntityToDomain(
                lMonitoringDataSource.getDataByDay(selectedDay)
            )
        )
    }.flowOn(Dispatchers.IO)

    override fun getChargingHistoryByDay(selectedDay: String): Flow<List<ChargingHistory>> = flow {
        emit(
            mapChargingHistoryEntityToDomain(
                lMonitoringDataSource.getChargingHistoryByDay(selectedDay)
            )
        )
    }.flowOn(Dispatchers.IO)

    override fun getDataByRange(startTime: Long, endTime: Long): Flow<List<BatteryHistory>> = flow {
        emit(
            mapBatteryHistoryEntityToDomain(
                lMonitoringDataSource.getDataByRange(startTime, endTime)
            )
        )
    }.flowOn(Dispatchers.IO)
}