package com.juanarton.batterysense.ui.fragments.history.charging

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChargingHistoryViewModel @Inject constructor(
    private var batteryMonitoringUseCase: BatteryMonitoringUseCase
) : ViewModel() {
    private val _chargingHistoryByDay = MutableLiveData<List<ChargingHistory>>()
    val chargingHistoryByDay = _chargingHistoryByDay

    fun getChargingHistoryByDay(day: String) {
        viewModelScope.launch {
            batteryMonitoringUseCase.getChargingHistoryByDay(day).collect {
                _chargingHistoryByDay.value = it
            }
        }
    }
}