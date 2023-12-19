package com.juanarton.core.data.domain.usecase

import com.juanarton.core.data.domain.model.BatteryInfo
import com.juanarton.core.data.domain.model.Config
import com.juanarton.core.data.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface DataRepositoryUseCase {
    fun getConfig(): Flow<Config>

    fun setTargetCurrent(targetCurrent: String): Flow<Result>

    fun setChargingSwitchStatus(switchStat: Boolean): Flow<Result>

    fun getBatteryInfo(): Flow<BatteryInfo>

    fun setChargingLimitStatus(
        switchStat: Boolean
    ): Flow<Result>

    fun setMaximumCapacity(
        maxCapacity: String
    ): Flow<Result>

    fun setBatteryLevelThreshold(min: Int, max: Int, callback: (Boolean) -> Unit)

    fun getBatteryLevelThreshold(): Pair<Int, Int>

    fun setAlarmStatus(key: String, value: Boolean, callback: (Boolean) -> Unit)

    fun getAlarmStatus(key: String): Boolean
}