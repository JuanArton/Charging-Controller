package com.juanarton.core.data.source.local.monitoring.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screenOn")
data class ScreenOnEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "screenOnTime")
    val screenOnTime: Long
)