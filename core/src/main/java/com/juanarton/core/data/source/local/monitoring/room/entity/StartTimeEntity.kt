package com.juanarton.core.data.source.local.monitoring.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "startTime")
data class StartTimeEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "startTime")
    val startTime: String
)