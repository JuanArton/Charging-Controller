package com.juanarton.batterysense.ui.activity.batteryhistory

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BatteryHistoryViewModel @Inject constructor(
    private val batteryMonitoringUseCase: BatteryMonitoringUseCase
) : ViewModel() {
    private val _batteryHistory = MutableLiveData<List<BatteryHistory>>()
    val batteryHistory = _batteryHistory
    fun getBatteryHistory() {
        viewModelScope.launch {
            batteryMonitoringUseCase.getHistoryDataChunk(216001 , 0).collect {
                _batteryHistory.value = it
            }
        }
    }
}