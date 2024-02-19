package com.juanarton.core.data.source.local.monitoring.room.chargingHistory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juanarton.core.data.source.local.monitoring.room.chargingHistory.entity.ChargingHistoryEntity

@Dao
interface ChargingHistoryDAO {
    @Query("SELECT * FROM chargingHistory ORDER BY id DESC")
    fun getChargingHistory(): List<ChargingHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ChargingHistoryEntity::class)
    fun insertChargingHistory(chargingHistoryEntity: ChargingHistoryEntity)
}