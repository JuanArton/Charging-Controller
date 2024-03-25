package com.juanarton.core.data.domain.batteryMonitoring.repository

import android.content.Context
import androidx.paging.PagingData
import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import kotlinx.coroutines.flow.Flow

interface IBatteryMonitoringRepository {

    fun getBatteryInfo(): Flow<BatteryInfo>
    fun getDeepSleepInitialValue(): Long

    fun insertDeepSleepInitialValue(deepSleepInitialVale: Long)

    fun getStartTime(): Long

    fun insertStartTime(startTime: Long)

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

    fun getHistoryDataChunk(limit: Int, offset: Int): Flow<List<BatteryHistory>>

    fun getLastPlugged(): Pair<Long, Int>

    fun insertLastPlugged(lastPlugged: Long, lastPluggedLevel: Int)

    fun getLastUnplugged(): Pair<Long, Int>

    fun insertLastUnplugged(lastUnplugged: Long, lastUnpluggedLevel: Int)

    fun getChargingHistory(): Flow<PagingData<ChargingHistory>>

    fun insertChargingHistory(chargingHistory: ChargingHistory)

    fun getRawCurrent(): Int

    fun getCurrentUnit(): String

    fun insertCurrentUnit(currentUnit: String)

    fun getCapacity(): Int

    fun insertCapacity(capacity: Int)

    fun deleteChargingHistory()
}