package com.juanarton.core.utils

import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.data.source.local.monitoring.room.entity.HistoryEntity

object DomainUtils {
    fun mapHistoryDomainToEntity(batteryHistory: BatteryHistory) = run {
        HistoryEntity(
            batteryHistory.timestamp,
            batteryHistory.level,
            batteryHistory.current,
            batteryHistory.temperature,
            batteryHistory.power,
            batteryHistory.voltage,
        )
    }
}