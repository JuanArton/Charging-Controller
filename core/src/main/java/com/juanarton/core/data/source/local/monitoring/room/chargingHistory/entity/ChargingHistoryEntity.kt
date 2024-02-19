package com.juanarton.core.data.source.local.monitoring.room.chargingHistory.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chargingHistory")
data class ChargingHistoryEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int?,

    @ColumnInfo(name = "startTime")
    val startTime: Long,

    @ColumnInfo(name = "current")
    val endTime: Long,

    @ColumnInfo(name = "startLevel")
    val startLevel: Int,

    @ColumnInfo(name = "endLevel")
    val endLevel: Int,

    @ColumnInfo(name = "levelDifference")
    val levelDifference: Int,

    @ColumnInfo(name = "isCharging")
    val isCharging: Boolean
)