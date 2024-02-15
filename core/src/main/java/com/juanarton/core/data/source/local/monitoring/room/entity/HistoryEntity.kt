package com.juanarton.core.data.source.local.monitoring.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "batteryHistory")
data class HistoryEntity (
    @PrimaryKey
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "level")
    val level: Int,

    @ColumnInfo(name = "current")
    val current: Int,

    @ColumnInfo(name = "temperature")
    val temperature: Int,

    @ColumnInfo(name = "power")
    val power: Float,

    @ColumnInfo(name = "voltage")
    val voltage: Float,
)