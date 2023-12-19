package com.juanarton.chargingcurrentcontroller.ui.fragments.alarm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juanarton.core.data.domain.usecase.DataRepositoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val dataRepositoryUseCase: DataRepositoryUseCase) : ViewModel() {

    private val _batteryLevelThreshold: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val batterLevelThreshold = _batteryLevelThreshold

    private val _batteryLevelAlarmStatus: MutableLiveData<Boolean> = MutableLiveData()
    val batteryLevelAlarmStatus = _batteryLevelAlarmStatus

    fun getBatteryLevelThreshold() {
        _batteryLevelThreshold.value = dataRepositoryUseCase.getBatteryLevelThreshold()
    }

    fun setBatteryLevelThreshold (min: Int, max: Int, callback: (Boolean) -> Unit) =
        dataRepositoryUseCase.setBatteryLevelThreshold(min, max, callback)

    fun getBatteryLevelAlarmStatus(key: String) {
        _batteryLevelAlarmStatus.value = dataRepositoryUseCase.getAlarmStatus(key)
    }

    fun setBatteryLevelAlarmStatus(key: String, value: Boolean, callback: (Boolean) -> Unit) =
        dataRepositoryUseCase.setAlarmStatus(key, value, callback)
}