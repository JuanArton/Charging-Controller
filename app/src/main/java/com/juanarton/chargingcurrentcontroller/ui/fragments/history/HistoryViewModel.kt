package com.juanarton.chargingcurrentcontroller.ui.fragments.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private var batteryMonitoringUseCase: BatteryMonitoringUseCase
) : ViewModel() {

    private val _chargingHistory = MutableLiveData<List<ChargingHistory>>()
    val chargingHistory = _chargingHistory

    fun getChargingHistory() {
        viewModelScope.launch {
            batteryMonitoringUseCase.getChargingHistory().collect {
                _chargingHistory.value = it
            }
        }
    }
}