package com.juanarton.core.data.source.local.monitoring.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juanarton.core.data.source.local.monitoring.room.entity.HistoryEntity

@Dao
interface DAO {
    @Query("SELECT * FROM batteryHistory LIMIT :limit OFFSET :offset")
    fun getBatteryHistory(limit: Int, offset: Int): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = HistoryEntity::class)
    fun insertHistory(historyEntity: HistoryEntity)

    @Query("SELECT COUNT(*) FROM batteryHistory")
    fun getRowCount(): Int

    @Query("SELECT * FROM batteryHistory LIMIT 1")
    fun getFirst(): HistoryEntity

    @Delete
    fun deleteFirst(historyEntity: HistoryEntity)
}