package com.juanarton.core.data.repository

import android.content.Context
import com.juanarton.core.data.domain.batteryInfo.model.Config
import com.juanarton.core.data.domain.batteryInfo.model.Result
import com.juanarton.core.data.domain.batteryInfo.repository.IAppConfigRepository
import com.juanarton.core.data.source.local.appConfig.LAppConfigDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class AppConfigRepository @Inject constructor(
    private val lAppConfigDataSource: LAppConfigDataSource,
    private val context: Context
): IAppConfigRepository {

    override fun getConfig(): Flow<Config> =
        flow {
            emit(
                lAppConfigDataSource.getConfig()
            )
        }.flowOn(Dispatchers.IO)

    override fun setTargetCurrent(targetCurrent: String): Flow<Result> =
        flow {
            emit(lAppConfigDataSource.setTargetCurrent(targetCurrent))
        }.flowOn(Dispatchers.IO)

    override fun setChargingSwitchStatus(switchStat: Boolean): Flow<Result> =
        flow {
            emit(
                lAppConfigDataSource.setChargingSwitchStatus(switchStat)
            )
        }.flowOn(Dispatchers.IO)

    override fun setChargingLimitStatus(switchStat: Boolean): Flow<Result> =
        flow {
            emit(
                lAppConfigDataSource.setChargingLimitStatus(switchStat)
            )
        }.flowOn(Dispatchers.IO)

    override fun setMaximumCapacity(maxCapacity: String): Flow<Result> =
        flow {
            emit(
                lAppConfigDataSource.setMaximumCapacity(maxCapacity)
            )
        }.flowOn(Dispatchers.IO)

    override fun setBatteryLevelThreshold(min: Int, max: Int, callback: (Boolean) -> Unit) {
        callback(
            lAppConfigDataSource.setBatteryLevelThreshold(min, max, context)
        )
    }

    override fun getBatteryLevelThreshold(): Pair<Int, Int> {
        return lAppConfigDataSource.getBatteryLevelThreshold(context)
    }

    override fun setBatteryLevelAlarmStatus(value: Boolean, callback: (Boolean) -> Unit) {
        callback(lAppConfigDataSource.setBatteryLevelAlarmStatus(value, context))
    }

    override fun getBatteryLevelAlarmStatus(): Boolean {
        return lAppConfigDataSource.getBatteryLevelAlarmStatus(context)
    }

    override fun setBatteryTemperatureThreshold(temperature: Int, callback: (Boolean) -> Unit) {
        callback(lAppConfigDataSource.setBatteryTemperatureThreshold(temperature, context))
    }

    override fun getBatteryTemperatureThreshold(): Int {
        return lAppConfigDataSource.getBatteryTemperatureThreshold(context)
    }

    override fun setBatteryTemperatureAlarmStatus(value: Boolean, callback: (Boolean) -> Unit) {
        callback(lAppConfigDataSource.setBatteryTemperatureAlarmStatus(value, context))
    }

    override fun getBatteryTemperatureAlarmStatus(): Boolean {
        return lAppConfigDataSource.getBatteryTemperatureAlarmStatus(context)
    }
}