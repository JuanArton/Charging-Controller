package com.juanarton.core.data.domain.batteryMonitoring.usecase

import android.content.Context
import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.data.source.local.monitoring.room.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface BatteryMonitoringUseCase {
    fun getBatteryInfo(): Flow<BatteryInfo>
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

    fun insertHistory(batteryHistory: BatteryHistory)

    fun getHistoryDataChunk(limit: Int, offset: Int): Flow<List<HistoryEntity>>
}