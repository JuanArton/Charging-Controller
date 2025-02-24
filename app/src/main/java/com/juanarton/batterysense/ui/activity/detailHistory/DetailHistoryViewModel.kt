package com.juanarton.batterysense.ui.activity.detailHistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailHistoryViewModel @Inject constructor(
    private val batteryMonitoringUseCase: BatteryMonitoringUseCase
) : ViewModel() {

    private val _batteryHistoryByDay = MutableLiveData<List<BatteryHistory>>()
    val batteryHistoryByDay = _batteryHistoryByDay

    fun getBatteryHistoryByDay(startTime: Long, endTime: Long) {
        viewModelScope.launch {
            batteryMonitoringUseCase.getDataByRange(startTime, endTime).collect {
                _batteryHistoryByDay.value = it
            }
        }
    }
}