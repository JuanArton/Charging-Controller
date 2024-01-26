package com.juanarton.chargingcurrentcontroller.ui.fragments.alarm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juanarton.core.data.domain.batteryInfo.usecase.BatteryInfoRepositoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val batteryInfoRepositoryUseCase: BatteryInfoRepositoryUseCase) : ViewModel() {

    private val _batteryLevelThreshold: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val batteryLevelThreshold = _batteryLevelThreshold

    private val _batteryLevelAlarmStatus: MutableLiveData<Boolean> = MutableLiveData()
    val batteryLevelAlarmStatus = _batteryLevelAlarmStatus

    private val _batteryTemperatureThreshold: MutableLiveData<Int> = MutableLiveData()
    val batteryTemperatureThreshold = _batteryTemperatureThreshold

    private val _batteryTempAlarmStatus: MutableLiveData<Boolean> = MutableLiveData()
    val batteryTemperatureAlarmStatus = _batteryTempAlarmStatus

    fun getBatteryLevelThreshold() {
        _batteryLevelThreshold.value = batteryInfoRepositoryUseCase.getBatteryLevelThreshold()
    }

    fun setBatteryLevelThreshold (min: Int, max: Int, callback: (Boolean) -> Unit) =
        batteryInfoRepositoryUseCase.setBatteryLevelThreshold(min, max, callback)

    fun getBatteryLevelAlarmStatus() {
        _batteryLevelAlarmStatus.value = batteryInfoRepositoryUseCase.getBatteryLevelAlarmStatus()
    }

    fun setBatteryLevelAlarmStatus(value: Boolean, callback: (Boolean) -> Unit) =
        batteryInfoRepositoryUseCase.setBatteryLevelAlarmStatus(value, callback)

    fun getBatteryTemperatureThreshold() {
        _batteryTemperatureThreshold.value = batteryInfoRepositoryUseCase.getBatteryTemperatureThreshold()
    }

    fun setBatteryTemperatureThreshold(temp: Int, callback: (Boolean) -> Unit) {
        batteryInfoRepositoryUseCase.setBatteryTemperatureThreshold(temp, callback)
    }

    fun getBatteryTemperatureAlarmStatus() {
        _batteryTempAlarmStatus.value = batteryInfoRepositoryUseCase.getBatteryTemperatureAlarmStatus()
    }

    fun setBatteryTemperatureAlarmStatus(value: Boolean, callback: (Boolean) -> Unit) {
        batteryInfoRepositoryUseCase.setBatteryTemperatureAlarmStatus(value, callback)
    }
}