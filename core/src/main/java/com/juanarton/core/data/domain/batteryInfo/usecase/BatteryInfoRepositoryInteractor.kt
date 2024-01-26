package com.juanarton.core.data.domain.batteryInfo.usecase

import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.data.domain.batteryInfo.model.Config
import com.juanarton.core.data.domain.batteryInfo.model.Result
import com.juanarton.core.data.domain.batteryInfo.repository.BatteryInfoRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BatteryInfoRepositoryInteractor @Inject constructor(
    private val batteryInfoRepositoryInterface: BatteryInfoRepositoryInterface
): BatteryInfoRepositoryUseCase {
    override fun getConfig(): Flow<Config> =
        batteryInfoRepositoryInterface.getConfig()

    override fun setTargetCurrent(targetCurrent: String): Flow<Result> =
        batteryInfoRepositoryInterface.setTargetCurrent(targetCurrent)

    override fun setChargingSwitchStatus(switchStat: Boolean): Flow<Result> =
        batteryInfoRepositoryInterface.setChargingSwitchStatus(switchStat)

    override fun getBatteryInfo(): Flow<BatteryInfo> =
        batteryInfoRepositoryInterface.getBatteryInfo()

    override fun setChargingLimitStatus(switchStat: Boolean): Flow<Result> =
        batteryInfoRepositoryInterface.setChargingLimitStatus(switchStat)

    override fun setMaximumCapacity(maxCapacity: String): Flow<Result> =
        batteryInfoRepositoryInterface.setMaximumCapacity(maxCapacity)

    override fun setBatteryLevelThreshold(min: Int, max: Int, callback: (Boolean) -> Unit) =
        batteryInfoRepositoryInterface.setBatteryLevelThreshold(min, max, callback)

    override fun getBatteryLevelThreshold(): Pair<Int, Int> =
        batteryInfoRepositoryInterface.getBatteryLevelThreshold()
    override fun setBatteryLevelAlarmStatus(value: Boolean, callback: (Boolean) -> Unit) =
        batteryInfoRepositoryInterface.setBatteryLevelAlarmStatus(value, callback)

    override fun getBatteryLevelAlarmStatus(): Boolean =
        batteryInfoRepositoryInterface.getBatteryLevelAlarmStatus()

    override fun setBatteryTemperatureThreshold(temperature: Int, callback: (Boolean) -> Unit) =
        batteryInfoRepositoryInterface.setBatteryTemperatureThreshold(temperature, callback)

    override fun getBatteryTemperatureThreshold(): Int =
        batteryInfoRepositoryInterface.getBatteryTemperatureThreshold()

    override fun setBatteryTemperatureAlarmStatus(value: Boolean, callback: (Boolean) -> Unit) =
        batteryInfoRepositoryInterface.setBatteryTemperatureAlarmStatus(value, callback)

    override fun getBatteryTemperatureAlarmStatus(): Boolean =
        batteryInfoRepositoryInterface.getBatteryTemperatureAlarmStatus()
}