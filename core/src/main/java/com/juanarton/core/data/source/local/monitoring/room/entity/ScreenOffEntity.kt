package com.juanarton.core.data.source.local.monitoring.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screenOff")
data class ScreenOffEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "screenOfTime")
    val screenOffTime: Long
)