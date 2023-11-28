package com.juanarton.core.data.domain.usecase

import com.juanarton.core.data.domain.model.BatteryInfo
import com.juanarton.core.data.domain.model.Config
import com.juanarton.core.data.domain.model.Result
import com.juanarton.core.data.domain.repository.DataRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataRepositoryInteractor @Inject constructor(
    private val dataRepository: DataRepositoryInterface
): DataRepositoryUseCase {
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
}