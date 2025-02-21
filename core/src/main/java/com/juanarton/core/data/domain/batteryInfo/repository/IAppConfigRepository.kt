package com.juanarton.core.data.domain.batteryInfo.repository

import com.juanarton.core.data.domain.batteryInfo.model.Config
import com.juanarton.core.data.domain.batteryInfo.model.Result
import kotlinx.coroutines.flow.Flow

interface IAppConfigRepository {
    fun getConfig(): Flow<Config>

    fun setTargetCurrent(targetCurrent: String): Flow<Result>

    fun setChargingSwitchStatus(switchStat: Boolean): Flow<Result>

    fun setChargingLimitStatus(
        switchStat: Boolean
    ): Flow<Result>

    fun setMaximumCapacity(
        maxCapacity: String
    ): Flow<Result>

    fun setBatteryLevelThreshold(min: Int, max: Int, callback: (Boolean) -> Unit)

    fun getBatteryLevelThreshold(): Pair<Int, Int>

    fun setBatteryLevelAlarmStatus(value: Boolean, callback: (Boolean) -> Unit)

    fun getBatteryLevelAlarmStatus(): Boolean

    fun setBatteryTemperatureThreshold(temperature: Int, callback: (Boolean) -> Unit)

    fun getBatteryTemperatureThreshold(): Int

    fun setBatteryTemperatureAlarmStatus(value: Boolean, callback: (Boolean) -> Unit)

    fun getBatteryTemperatureAlarmStatus(): Boolean

    fun setOneTimeAlarmStatus(value: Boolean, callback: (Boolean) -> Unit)

    fun getOneTimeAlarmStatus(): Boolean
}