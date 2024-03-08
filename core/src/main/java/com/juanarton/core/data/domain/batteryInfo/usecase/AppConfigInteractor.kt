package com.juanarton.core.data.domain.batteryInfo.usecase

import com.juanarton.core.data.domain.batteryInfo.model.Config
import com.juanarton.core.data.domain.batteryInfo.model.Result
import com.juanarton.core.data.domain.batteryInfo.repository.IAppConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppConfigInteractor @Inject constructor(
    private val iAppConfigRepository: IAppConfigRepository
): AppConfigUseCase {
    override fun getConfig(): Flow<Config> =
        iAppConfigRepository.getConfig()

    override fun setTargetCurrent(targetCurrent: String): Flow<Result> =
        iAppConfigRepository.setTargetCurrent(targetCurrent)

    override fun setChargingSwitchStatus(switchStat: Boolean): Flow<Result> =
        iAppConfigRepository.setChargingSwitchStatus(switchStat)

    override fun setChargingLimitStatus(switchStat: Boolean): Flow<Result> =
        iAppConfigRepository.setChargingLimitStatus(switchStat)

    override fun setMaximumCapacity(maxCapacity: String): Flow<Result> =
        iAppConfigRepository.setMaximumCapacity(maxCapacity)

    override fun setBatteryLevelThreshold(min: Int, max: Int, callback: (Boolean) -> Unit) =
        iAppConfigRepository.setBatteryLevelThreshold(min, max, callback)

    override fun getBatteryLevelThreshold(): Pair<Int, Int> =
        iAppConfigRepository.getBatteryLevelThreshold()
    override fun setBatteryLevelAlarmStatus(value: Boolean, callback: (Boolean) -> Unit) =
        iAppConfigRepository.setBatteryLevelAlarmStatus(value, callback)

    override fun getBatteryLevelAlarmStatus(): Boolean =
        iAppConfigRepository.getBatteryLevelAlarmStatus()

    override fun setBatteryTemperatureThreshold(temperature: Int, callback: (Boolean) -> Unit) =
        iAppConfigRepository.setBatteryTemperatureThreshold(temperature, callback)

    override fun getBatteryTemperatureThreshold(): Int =
        iAppConfigRepository.getBatteryTemperatureThreshold()

    override fun setBatteryTemperatureAlarmStatus(value: Boolean, callback: (Boolean) -> Unit) =
        iAppConfigRepository.setBatteryTemperatureAlarmStatus(value, callback)

    override fun getBatteryTemperatureAlarmStatus(): Boolean =
        iAppConfigRepository.getBatteryTemperatureAlarmStatus()

    override fun setOneTimeAlarmStatus(value: Boolean, callback: (Boolean) -> Unit) =
        iAppConfigRepository.setOneTimeAlarmStatus(value, callback)

    override fun getOneTimeAlarmStatus(): Boolean =
        iAppConfigRepository.getOneTimeAlarmStatus()
}