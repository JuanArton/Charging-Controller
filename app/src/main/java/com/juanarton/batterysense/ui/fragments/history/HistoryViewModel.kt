package com.juanarton.batterysense.ui.fragments.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private var batteryMonitoringUseCase: BatteryMonitoringUseCase
) : ViewModel() {
    fun getChargingHistory(): LiveData<PagingData<ChargingHistory>> {
        return batteryMonitoringUseCase.getChargingHistory().asLiveData().cachedIn(viewModelScope)
    }
}