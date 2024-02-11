package com.juanarton.core.data.source.local.monitoring.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juanarton.core.data.source.local.monitoring.room.entity.DeepSleepEntity
import com.juanarton.core.data.source.local.monitoring.room.entity.ScreenOffEntity
import com.juanarton.core.data.source.local.monitoring.room.entity.ScreenOnEntity
import com.juanarton.core.data.source.local.monitoring.room.entity.StartTimeEntity

@Dao
interface DAO {
    @Query("SELECT * FROM deepSleep LIMIT 1")
    fun getDeepSleepInitialValue(): DeepSleepEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = DeepSleepEntity::class)
    fun insertDeepSleepInitialValue(deepSleepInitialVale: DeepSleepEntity)

    @Query("SELECT * FROM startTime LIMIT 1")
    fun getStartTime(): StartTimeEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = StartTimeEntity::class)
    fun insertStartTime(startTimeEntity: StartTimeEntity)

    @Query("SELECT * FROM screenOn LIMIT 1")
    fun getScreenOnTime(): ScreenOnEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ScreenOnEntity::class)
    fun insertScreenOnTime(screenOnEntity: ScreenOnEntity)

    @Query("SELECT * FROM screenOff LIMIT 1")
    fun getScreenOfTime(): ScreenOffEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ScreenOffEntity::class)
    fun insertScreenOffTime(screenOffEntity: ScreenOffEntity)
}