package com.juanarton.chargingcurrentcontroller.ui.activity.batteryhistory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringUseCase
import com.juanarton.core.utils.BatteryUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BatteryHistoryViewModel @Inject constructor(
    private val batteryMonitoringUseCase: BatteryMonitoringUseCase
) : ViewModel() {
    fun getBatteryHistory() = batteryMonitoringUseCase.getHistoryDataChunk(21600, 0).asLiveData()
}