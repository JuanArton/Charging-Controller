package com.juanarton.batterysense.ui.fragments.alarm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juanarton.core.data.domain.batteryInfo.usecase.AppConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val appConfigUseCase: AppConfigUseCase
) : ViewModel() {

    private val _batteryLevelThreshold: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val batteryLevelThreshold = _batteryLevelThreshold

    private val _batteryLevelAlarmStatus: MutableLiveData<Boolean> = MutableLiveData()
    val batteryLevelAlarmStatus = _batteryLevelAlarmStatus

    private val _batteryTemperatureThreshold: MutableLiveData<Int> = MutableLiveData()
    val batteryTemperatureThreshold = _batteryTemperatureThreshold

    private val _batteryTempAlarmStatus: MutableLiveData<Boolean> = MutableLiveData()
    val batteryTemperatureAlarmStatus = _batteryTempAlarmStatus

    fun getBatteryLevelThreshold() {
        _batteryLevelThreshold.value = appConfigUseCase.getBatteryLevelThreshold()
    }

    fun setBatteryLevelThreshold (min: Int, max: Int, callback: (Boolean) -> Unit) =
        appConfigUseCase.setBatteryLevelThreshold(min, max, callback)

    fun getBatteryLevelAlarmStatus() {
        _batteryLevelAlarmStatus.value = appConfigUseCase.getBatteryLevelAlarmStatus()
    }

    fun setBatteryLevelAlarmStatus(value: Boolean, callback: (Boolean) -> Unit) =
        appConfigUseCase.setBatteryLevelAlarmStatus(value, callback)

    fun getBatteryTemperatureThreshold() {
        _batteryTemperatureThreshold.value = appConfigUseCase.getBatteryTemperatureThreshold()
    }

    fun setBatteryTemperatureThreshold(temp: Int, callback: (Boolean) -> Unit) {
        appConfigUseCase.setBatteryTemperatureThreshold(temp, callback)
    }

    fun getBatteryTemperatureAlarmStatus() {
        _batteryTempAlarmStatus.value = appConfigUseCase.getBatteryTemperatureAlarmStatus()
    }

    fun setBatteryTemperatureAlarmStatus(value: Boolean, callback: (Boolean) -> Unit) {
        appConfigUseCase.setBatteryTemperatureAlarmStatus(value, callback)
    }
}