package com.juanarton.core.data.source.local.monitoring

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import com.juanarton.core.data.source.local.monitoring.room.batteryHistory.BatteryHistoryDAO
import com.juanarton.core.data.source.local.monitoring.room.batteryHistory.entity.BatteryHistoryEntity
import com.juanarton.core.data.source.local.monitoring.room.chargingHistory.ChargingHistoryDAO
import com.juanarton.core.data.source.local.monitoring.room.chargingHistory.entity.ChargingHistoryEntity
import com.juanarton.core.utils.BatteryUtils
import com.juanarton.core.utils.BatteryUtils.getACCharge
import com.juanarton.core.utils.BatteryUtils.getChargingStatus
import com.juanarton.core.utils.BatteryUtils.getCurrent
import com.juanarton.core.utils.BatteryUtils.getCycleCount
import com.juanarton.core.utils.BatteryUtils.getLevel
import com.juanarton.core.utils.BatteryUtils.getTemperature
import com.juanarton.core.utils.BatteryUtils.getUSBCharge
import com.juanarton.core.utils.BatteryUtils.getUptime
import com.juanarton.core.utils.BatteryUtils.getVoltage
import com.juanarton.core.utils.BatteryUtils.registerStickyReceiver
import com.juanarton.core.utils.DomainUtils
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class LMonitoringDataSource @Inject constructor(
    context: Context,
    private val batteryHistoryDao: BatteryHistoryDAO,
    private val chargingHistoryDAO: ChargingHistoryDAO
) {

    fun getBatteryInfo(context: Context): BatteryInfo {
        registerStickyReceiver(context)

        val voltage = getVoltage()

        val current = getCurrent(getCurrentUnit())

        val power = voltage * current / 1000

        return BatteryInfo(
            getChargingStatus(), getACCharge(), getUSBCharge(), getLevel(), voltage,
            current.toInt(), abs(power), getTemperature(), getUptime(), getCycleCount(context)
        )
    }

    fun getRawCurrent(): Int {
        return BatteryUtils.getRawCurrent()
    }

    private val sharedPreferences = context.getSharedPreferences(
        "batteryMonitoringData", Context.MODE_PRIVATE
    )

    private val editor = sharedPreferences.edit()

    fun getDeepSleepInitialValue(): Long =
        sharedPreferences.getLong("deepSleepInitialValue", 0)

    fun insertDeepSleepInitialValue(deepSleepInitialVale: Long) {
        editor.putLong("deepSleepInitialValue", deepSleepInitialVale)
        editor.apply()
    }

    fun getStartTime(): Long =
        sharedPreferences.getLong("startTime", 0)

    fun insertStartTime(startTime: Long) {
        editor.putLong("startTime", startTime)
        editor.apply()
    }

    fun getScreenOnTime(): Long =
        sharedPreferences.getLong("screenOnTime", 0)

    fun insertScreenOnTime(screenOnTime: Long) {
        editor.putLong("screenOnTime", screenOnTime)
        editor.apply()
    }

    fun getScreenOffTime(): Long =
        sharedPreferences.getLong("screenOffTime", 0)

    fun insertScreenOffTime(screenOffEntity: Long) {
        editor.putLong("screenOffTime", screenOffEntity)
        editor.apply()
    }
    fun getCpuAwake(): Long =
        sharedPreferences.getLong("deviceAwake", 0)

    fun insertCpuAwake(cpuAwake: Long) {
        editor.putLong("deviceAwake", cpuAwake)
        editor.apply()
    }

    fun getBatteryLevel(context: Context): Int {
        return sharedPreferences.getInt("batteryLevel", BatteryUtils.getBatteryLevel(context))
    }

    fun insertBatteryLevel(level: Int) {
        editor.putInt("batteryLevel", level)
        editor.apply()
    }

    fun getInitialBatteryLevel(context: Context): Int {
        return sharedPreferences.getInt("initialBatteryLevel", BatteryUtils.getBatteryLevel(context))
    }

    fun insertInitialBatteryLevel(level: Int) {
        editor.putInt("initialBatteryLevel", level)
        editor.apply()
    }

    fun getScreenOnDrain(): Int {
        return sharedPreferences.getInt("screenOnDrain", 0)
    }

    fun insertScreenOnDrain(level: Int) {
        editor.putInt("screenOnDrain", level)
        editor.apply()
    }

    fun getScreenOffDrain(): Int {
        return sharedPreferences.getInt("screenOffDrain", 0)
    }

    fun insertScreenOffDrain(level: Int) {
        editor.putInt("screenOffDrain", level)
        editor.apply()
    }

    fun getHistoryDataChunk(limit: Int, offset: Int): List<BatteryHistoryEntity> {
        return batteryHistoryDao.getBatteryHistory(limit, offset)
    }

    fun insertHistory(batteryHistoryEntity: BatteryHistoryEntity) {
        batteryHistoryDao.insertBatteryHistory(batteryHistoryEntity)
    }

    fun getRowCount(): Int {
        return batteryHistoryDao.getRowCount()
    }

    fun getFirst(): BatteryHistoryEntity {
        return batteryHistoryDao.getFirst()
    }

    fun deleteFirst(batteryHistoryEntity: BatteryHistoryEntity) {
        batteryHistoryDao.deleteFirst(batteryHistoryEntity)
    }

    fun getLastPlugged(): Pair<Long, Int> =
        Pair(
            sharedPreferences.getLong("lastPlugged", 0),
            sharedPreferences.getInt("lastPluggedLevel", 0)
        )

    fun insertLastPlugged(lastPlugged: Long, lastPluggedLevel: Int) {
        editor.putLong("lastPlugged", lastPlugged)
        editor.putInt("lastPluggedLevel", lastPluggedLevel)
        editor.apply()
    }

    fun getLastUnplugged(): Pair<Long, Int> =
        Pair(
            sharedPreferences.getLong("lastUnplugged", 0),
            sharedPreferences.getInt("lastUnpluggedLevel", 0)
        )

    fun insertLastUnpPlugged(lastUnplugged: Long, lastUnpluggedLevel: Int) {
        editor.putLong("lastUnplugged", lastUnplugged)
        editor.putInt("lastUnpluggedLevel", lastUnpluggedLevel)
        editor.apply()
    }

    fun getChargingHistory(): PagingSource<Int, ChargingHistory> {
        return object : PagingSource<Int, ChargingHistory>(){
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChargingHistory> {
                val page = params.key ?: 0

                return try {
                    val chargingHistory = DomainUtils.mapChargingHistoryEntityToDomain(
                        chargingHistoryDAO.getChargingHistory(params.loadSize, page * params.loadSize)
                    )

                    LoadResult.Page(
                        data = chargingHistory,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (chargingHistory.isEmpty()) null else page + 1
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, ChargingHistory>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    val anchorPage = state.closestPageToPosition(anchorPosition)
                    anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
                }
            }
        }
    }

    fun insertChargingHistory(chargingHistoryEntity: ChargingHistoryEntity) {
        chargingHistoryDAO.insertChargingHistory(chargingHistoryEntity)
    }

    fun deleteChargingHistory() {
        chargingHistoryDAO.deleteAll()
    }

    fun getCurrentUnit(): String {
        return sharedPreferences.getString("currentUnit", "μA")?: "μA"
    }

    fun insertCurrentUnit(currentUnit: String) {
        editor.putString("currentUnit", currentUnit)
        editor.apply()
    }

    fun getCapacity(): Int {
        return sharedPreferences.getInt("capacity", 0)
    }

    fun insertCapacity(capacity: Int) {
        editor.putInt("capacity", capacity)
        editor.apply()
    }

    fun getUsageData() : List<BatteryHistoryEntity> = batteryHistoryDao.getUsageData()
}