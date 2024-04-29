package com.juanarton.core.data.source.local.monitoring.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.juanarton.core.data.source.local.monitoring.room.batteryHistory.BatteryHistoryDAO
import com.juanarton.core.data.source.local.monitoring.room.batteryHistory.entity.BatteryHistoryEntity
import com.juanarton.core.data.source.local.monitoring.room.chargingHistory.ChargingHistoryDAO
import com.juanarton.core.data.source.local.monitoring.room.chargingHistory.entity.ChargingHistoryEntity

@Database(entities = [
    BatteryHistoryEntity::class,
    ChargingHistoryEntity::class
], version = 2, exportSchema = false)
abstract class HistoryDatabase: RoomDatabase() {

    abstract fun batteryHistoryDao(): BatteryHistoryDAO
    abstract fun chargingHistoryDao(): ChargingHistoryDAO
}