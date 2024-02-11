package com.juanarton.core.data.source.local.monitoring.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deepSleep")
data class DeepSleepEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "deepSleepInitialValue")
    val deepSleepInitialValue: Long
)