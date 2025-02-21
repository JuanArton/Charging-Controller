package com.juanarton.core.data.source.local.monitoring.room.chargingHistory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juanarton.core.data.source.local.monitoring.room.chargingHistory.entity.ChargingHistoryEntity

@Dao
interface ChargingHistoryDAO {
    @Query("SELECT * FROM chargingHistory ORDER BY id DESC LIMIT :limit OFFSET :offset")
    suspend fun getChargingHistory(limit: Int, offset: Int): List<ChargingHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ChargingHistoryEntity::class)
    fun insertChargingHistory(chargingHistoryEntity: ChargingHistoryEntity)

    @Query("DELETE FROM chargingHistory")
    fun deleteAll()

    @Query("""
    SELECT * FROM chargingHistory
    WHERE strftime('%Y-%m-%d', datetime(endTime / 1000, 'unixepoch', 'localtime')) = :selectedDay
    ORDER BY endTime ASC
    """)
    fun getChargingHistoryByDay(selectedDay: String): List<ChargingHistoryEntity>
}