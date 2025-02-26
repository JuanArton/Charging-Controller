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

    @ColumnInfo(name = "endTime")
    val endTime: Long,

    @ColumnInfo(name = "startLevel")
    val startLevel: Int,

    @ColumnInfo(name = "endLevel")
    val endLevel: Int,

    @ColumnInfo(name = "levelDifference")
    val levelDifference: Int,

    @ColumnInfo(name = "screenOn")
    val screenOn: Long?,

    @ColumnInfo(name = "screenOff")
    val screenOff: Long?,

    @ColumnInfo(name = "screenOnDrain")
    val screenOnDrain: Int?,

    @ColumnInfo(name = "screenOffDrain")
    val screenOffDrain: Int?,

    @ColumnInfo(name = "screenOffDrainPerHr")
    val screenOffDrainPerHr: Double?,

    @ColumnInfo(name = "screenOnDrainPerHr")
    val screenOnDrainPerHr: Double?,

    @ColumnInfo(name = "deepSleepPercentage")
    val deepSleepPercentage: Double?,

    @ColumnInfo(name = "awakePercentage")
    val awakePercentage: Double?,

    @ColumnInfo(name = "awakeDuration")
    val awakeDuration: Long?,

    @ColumnInfo(name = "sleepDuration")
    val sleepDuration: Long?,

    @ColumnInfo(name = "awakeSpeed")
    val awakeSpeed: Double?,

    @ColumnInfo(name = "sleepSpeed")
    val sleepSpeed: Double?,

    @ColumnInfo(name = "chargingSpeed")
    val chargingSpeed: Double?,

    @ColumnInfo(name = "chargingDuration")
    val chargingDuration: Long?,

    @ColumnInfo(name = "isCharging")
    val isCharging: Boolean
)