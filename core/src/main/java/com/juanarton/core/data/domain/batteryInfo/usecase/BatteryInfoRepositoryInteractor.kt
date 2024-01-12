package com.juanarton.core.data.domain.batteryInfo.usecase

import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.data.domain.batteryInfo.model.Config
import com.juanarton.core.data.domain.batteryInfo.model.Result
import com.juanarton.core.data.domain.batteryInfo.repository.BatteryInfoRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BatteryInfoRepositoryInteractor @Inject constructor(
    private val dataRepository: BatteryInfoRepositoryInterface
): BatteryInfoRepositoryUseCase {
    override fun getConfig(): Flow<Config> =
        dataRepository.getConfig()

    override fun setTargetCurrent(targetCurrent: String): Flow<Result> =
        dataRepository.setTargetCurrent(targetCurrent)

    override fun setChargingSwitchStatus(switchStat: Boolean): Flow<Result> =
        dataRepository.setChargingSwitchStatus(switchStat)

    override fun getBatteryInfo(): Flow<BatteryInfo> =
        dataRepository.getBatteryInfo()

    override fun setChargingLimitStatus(switchStat: Boolean): Flow<Result> =
        dataRepository.setChargingLimitStatus(switchStat)

    override fun setMaximumCapacity(maxCapacity: String): Flow<Result> =
        dataRepository.setMaximumCapacity(maxCapacity)

    override fun setBatteryLevelThreshold(min: Int, max: Int, callback: (Boolean) -> Unit) =
        dataRepository.setBatteryLevelThreshold(min, max, callback)

    override fun getBatteryLevelThreshold(): Pair<Int, Int> =
        dataRepository.getBatteryLevelThreshold()
    override fun setAlarmStatus(key: String, value: Boolean, callback: (Boolean) -> Unit) =
        dataRepository.setAlarmStatus(key, value, callback)

    override fun getAlarmStatus(key: String): Boolean =
        dataRepository.getAlarmStatus(key)
}