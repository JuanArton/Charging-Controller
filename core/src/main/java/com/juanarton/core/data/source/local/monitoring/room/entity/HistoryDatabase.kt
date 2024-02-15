package com.juanarton.core.data.source.local.monitoring.room.entity

import androidx.room.Database
import androidx.room.RoomDatabase
import com.juanarton.core.data.source.local.monitoring.room.DAO

@Database(entities = [
    HistoryEntity::class
], version = 1, exportSchema = false)
abstract class HistoryDatabase: RoomDatabase() {
    abstract  fun dbDao(): DAO
}