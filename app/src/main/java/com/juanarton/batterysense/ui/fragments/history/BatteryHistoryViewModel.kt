package com.juanarton.batterysense.ui.fragments.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BatteryHistoryViewModel @Inject constructor(
    private val batteryMonitoringUseCase: BatteryMonitoringUseCase
) : ViewModel() {
    private val _availableDay = MutableLiveData<List<String>>()
    val availableDay = _availableDay

    fun getAvailableDay() {
        viewModelScope.launch {
            batteryMonitoringUseCase.getAvailableDays().collect {
                _availableDay.value = it
            }
        }
    }
}