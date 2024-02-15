package com.juanarton.core.data.domain.batteryMonitoring.usecase

import android.content.Context
import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.data.domain.batteryMonitoring.repository.IBatteryMonitoringRepository
import com.juanarton.core.data.source.local.monitoring.room.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class BatteryMonitoringInteractor @Inject constructor(
    private val iBatteryMonitoringRepository: IBatteryMonitoringRepository
): BatteryMonitoringUseCase {
    override fun getBatteryInfo(): Flow<BatteryInfo> =
        iBatteryMonitoringRepository.getBatteryInfo()

    override fun getDeepSleepInitialValue(): Long =
        iBatteryMonitoringRepository.getDeepSleepInitialValue()

    override fun insertDeepSleepInitialValue(deepSleepInitialVale: Long) =
        iBatteryMonitoringRepository.insertDeepSleepInitialValue(deepSleepInitialVale)

    override fun getStartTime(): Date =
        iBatteryMonitoringRepository.getStartTime()

    override fun insertStartTime(startTime: Date) =
        iBatteryMonitoringRepository.insertStartTime(startTime)

    override fun getScreenOnTime(): Long =
        iBatteryMonitoringRepository.getScreenOnTime()

    override fun insertScreenOnTime(seconds: Long) =
        iBatteryMonitoringRepository.insertScreenOnTime(seconds)

    override fun getScreenOffTime(): Long =
        iBatteryMonitoringRepository.getScreenOffTime()

    override fun insertScreenOffTime(seconds: Long) =
        iBatteryMonitoringRepository.insertScreenOffTime(seconds)

    override fun getCpuAwake(): Long =
        iBatteryMonitoringRepository.getCpuAwake()

    override fun insertCpuAwake(cpuAwake: Long) =
        iBatteryMonitoringRepository.insertCpuAwake(cpuAwake)

    override fun getBatteryLevel(context: Context): Int =
        iBatteryMonitoringRepository.getBatteryLevel(context)

    override fun insertBatteryLevel(level: Int) =
        iBatteryMonitoringRepository.insertBatteryLevel(level)

    override fun getInitialBatteryLevel(context: Context): Int =
        iBatteryMonitoringRepository.getBatteryLevel(context)

    override fun insertInitialBatteryLevel(level: Int) =
        iBatteryMonitoringRepository.insertInitialBatteryLevel(level)

    override fun getScreenOnDrain(): Int =
        iBatteryMonitoringRepository.getScreenOnDrain()

    override fun insertScreenOnDrain(level: Int) =
        iBatteryMonitoringRepository.insertScreenOnDrain(level)

    override fun getScreenOffDrain(): Int =
        iBatteryMonitoringRepository.getScreenOffDrain()

    override fun insertScreenOffDrain(level: Int) =
        iBatteryMonitoringRepository.insertScreenOffDrain(level)

    override fun insertHistory(batteryHistory: BatteryHistory) {
        iBatteryMonitoringRepository.insertHistory(batteryHistory)
    }

    override fun getHistoryDataChunk(limit: Int, offset: Int): Flow<List<HistoryEntity>> =
        iBatteryMonitoringRepository.getHistoryDataChunk(limit, offset)

}