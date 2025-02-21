package com.juanarton.batterysense.ui.fragments.fullhistory.chart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartFullHistoryViewModel @Inject constructor(
    private val batteryMonitoringUseCase: BatteryMonitoringUseCase
) : ViewModel() {

    private val _batteryHistoryByDay = MutableLiveData<List<BatteryHistory>>()
    val batteryHistoryByDay = _batteryHistoryByDay

    fun getBatteryHistoryByDay(day: String) {
        viewModelScope.launch {
            batteryMonitoringUseCase.getDataByDay(day).collect {
                _batteryHistoryByDay.value = it
            }
        }
    }
}