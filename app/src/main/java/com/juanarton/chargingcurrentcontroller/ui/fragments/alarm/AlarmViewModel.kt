package com.juanarton.chargingcurrentcontroller.ui.fragments.alarm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juanarton.core.data.domain.batteryInfo.usecase.BatteryInfoRepositoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val batteryInfoRepositoryUseCase: BatteryInfoRepositoryUseCase) : ViewModel() {

    private val _batteryLevelThreshold: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val batterLevelThreshold = _batteryLevelThreshold

    private val _batteryLevelAlarmStatus: MutableLiveData<Boolean> = MutableLiveData()
    val batteryLevelAlarmStatus = _batteryLevelAlarmStatus

    fun getBatteryLevelThreshold() {
        _batteryLevelThreshold.value = batteryInfoRepositoryUseCase.getBatteryLevelThreshold()
    }

    fun setBatteryLevelThreshold (min: Int, max: Int, callback: (Boolean) -> Unit) =
        batteryInfoRepositoryUseCase.setBatteryLevelThreshold(min, max, callback)

    fun getBatteryLevelAlarmStatus(key: String) {
        _batteryLevelAlarmStatus.value = batteryInfoRepositoryUseCase.getAlarmStatus(key)
    }

    fun setBatteryLevelAlarmStatus(key: String, value: Boolean, callback: (Boolean) -> Unit) =
        batteryInfoRepositoryUseCase.setAlarmStatus(key, value, callback)
}