package com.juanarton.core.data.source.local.monitoring

import android.content.Context
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.data.source.local.monitoring.room.DAO
import com.juanarton.core.data.source.local.monitoring.room.entity.HistoryEntity
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
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class LMonitoringDataSource @Inject constructor(
    context: Context,
    private val dao: DAO
) {

    fun getBatteryInfo(context: Context): BatteryInfo {
        registerStickyReceiver(context)

        val voltage = getVoltage()

        val current = getCurrent()

        val power = voltage * current / 1000

        return BatteryInfo(
            getChargingStatus(), getACCharge(), getUSBCharge(), getLevel(), voltage,
            current.toInt(), abs(power), getTemperature(), getUptime(), getCycleCount()
        )
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

    fun getStartTime(): String =
        sharedPreferences.getString("startTime", null) ?: Date().toString()

    fun insertStartTime(startTime: String) {
        editor.putString("startTime", startTime)
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

    fun getHistoryDataChunk(limit: Int, offset: Int): List<HistoryEntity> {
        return dao.getBatteryHistory(limit, offset)
    }

    fun insertHistory(historyEntity: HistoryEntity) {
        dao.insertHistory(historyEntity)
    }

    fun getRowCount(): Int {
        return dao.getRowCount()
    }

    fun getFirst(): HistoryEntity {
        return dao.getFirst()
    }

    fun deleteFirst(historyEntity: HistoryEntity) {
        dao.deleteFirst(historyEntity)
    }
}