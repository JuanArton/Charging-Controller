package com.juanarton.core.data.source.local.monitoring.room.batteryHistory

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juanarton.core.data.source.local.monitoring.room.batteryHistory.entity.BatteryHistoryEntity

@Dao
interface BatteryHistoryDAO {
    @Query("SELECT * FROM batteryHistory ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    fun getBatteryHistory(limit: Int, offset: Int): List<BatteryHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = BatteryHistoryEntity::class)
    fun insertBatteryHistory(batteryHistoryEntity: BatteryHistoryEntity)

    @Query("SELECT COUNT(*) FROM batteryHistory")
    fun getRowCount(): Int

    @Query("SELECT * FROM batteryHistory LIMIT 1")
    fun getFirst(): BatteryHistoryEntity

    @Delete
    fun deleteFirst(batteryHistoryEntity: BatteryHistoryEntity)

    @Query("SELECT * FROM batteryHistory WHERE timestamp > (SELECT max(timestamp) FROM batteryHistory WHERE isCharging = 1) ORDER BY timestamp DESC")
    fun getUsageData(): List<BatteryHistoryEntity>

    @Query("SELECT DISTINCT strftime('%Y-%m-%d', datetime(timestamp / 1000, 'unixepoch', 'localtime')) FROM batteryHistory ORDER BY timestamp DESC")
    fun getAvailableDays(): List<String>

    @Query("""
    SELECT * FROM batteryHistory
    WHERE strftime('%Y-%m-%d', datetime(timestamp / 1000, 'unixepoch', 'localtime')) = :selectedDay
    ORDER BY timestamp ASC
    """)
    fun getHistoryByDay(selectedDay: String): List<BatteryHistoryEntity>

    @Query("SELECT * FROM batteryHistory WHERE timestamp >= :startTime AND timestamp <= :endTime")
    fun getHistoryByRange(startTime: Long, endTime: Long): List<BatteryHistoryEntity>
}