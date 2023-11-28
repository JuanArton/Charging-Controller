package com.juanarton.core.data.domain.repository

import com.juanarton.core.data.domain.model.BatteryInfo
import com.juanarton.core.data.domain.model.Config
import com.juanarton.core.data.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface DataRepositoryInterface {
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
}