package com.juanarton.core.utils

import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import com.juanarton.core.data.source.local.monitoring.room.batteryHistory.entity.BatteryHistoryEntity
import com.juanarton.core.data.source.local.monitoring.room.chargingHistory.entity.ChargingHistoryEntity

object DomainUtils {
    fun mapBatteryHistoryDomainToEntity(batteryHistory: BatteryHistory) = run {
        BatteryHistoryEntity(
            batteryHistory.timestamp,
            batteryHistory.level,
            batteryHistory.current,
            batteryHistory.temperature,
            batteryHistory.power,
            batteryHistory.voltage,
            batteryHistory.isCharging
        )
    }

    fun mapBatteryHistoryEntityToDomain(batteryHistory: List<BatteryHistoryEntity>) = run {
        batteryHistory.map {
            BatteryHistory(
                it.timestamp,
                it.level,
                it.current,
                it.temperature,
                it.power,
                it.voltage,
                it.isCharging
            )
        }
    }

    fun mapChargingHistoryDomainToEntity(chargingHistory: ChargingHistory) = run {
        ChargingHistoryEntity(
            chargingHistory.id,
            chargingHistory.startTime,
            chargingHistory.endTime,
            chargingHistory.startLevel,
            chargingHistory.endLevel,
            chargingHistory.levelDifference,
            chargingHistory.screenOn,
            chargingHistory.screenOn,
            chargingHistory.screenOnDrain,
            chargingHistory.screenOffDrain,
            chargingHistory.screenOffDrainPerHr,
            chargingHistory.screenOnDrainPerHr,
            chargingHistory.deepSleepPercentage,
            chargingHistory.awakePercentage,
            chargingHistory.awakeDuration,
            chargingHistory.sleepDuration,
            chargingHistory.awakeSpeed,
            chargingHistory.sleepSpeed,
            chargingHistory.chargingSpeed,
            chargingHistory.isCharging
        )
    }

    fun mapChargingHistoryEntityToDomain(chargingHistory: List<ChargingHistoryEntity>) = run {
        chargingHistory.map {
            ChargingHistory(
                it.id,
                it.startTime,
                it.endTime,
                it.startLevel,
                it.endLevel,
                it.levelDifference,
                it.screenOn,
                it.screenOn,
                it.screenOnDrain,
                it.screenOffDrain,
                it.screenOffDrainPerHr,
                it.screenOnDrainPerHr,
                it.deepSleepPercentage,
                it.awakePercentage,
                it.awakeDuration,
                it.sleepDuration,
                it.awakeSpeed,
                it.sleepSpeed,
                it.chargingSpeed,
                it.isCharging
            )
        }
    }
}