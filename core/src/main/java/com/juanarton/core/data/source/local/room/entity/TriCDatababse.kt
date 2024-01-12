package com.juanarton.core.data.source.local.room.entity

import androidx.room.Database
import androidx.room.RoomDatabase
import com.juanarton.core.data.source.local.room.DAO

@Database(entities = [
    DeepSleepEntity::class,
    ScreenOnEntity::class,
    ScreenOffEntity::class,
    StartTimeEntity::class
], version = 1, exportSchema = false)
abstract class TriCDatababse: RoomDatabase() {
    abstract  fun dbDao(): DAO
}