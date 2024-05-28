package com.juanarton.batterysense.ui.fragments.dashboard

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private var batteryMonitoringUseCase: BatteryMonitoringUseCase
) : ViewModel() {
    init {
        startBatteryMonitoring()
    }

    private val _batteryInfo = MutableLiveData<BatteryInfo>()
    val batteryInfo = _batteryInfo

    private var scheduledExecutorService: ScheduledExecutorService? = null
    private var isMonitoring = false

    fun startBatteryMonitoring() {
        if (!isMonitoring) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
            scheduledExecutorService?.scheduleWithFixedDelay(
                {
                    viewModelScope.launch {
                        batteryMonitoringUseCase.getBatteryInfo().collect {
                            _batteryInfo.value = it
                        }
                    }
                },
                0, 1, TimeUnit.SECONDS
            )
            isMonitoring = true
        }
    }

    fun stopBatteryMonitoring() {
        scheduledExecutorService?.shutdown()
        scheduledExecutorService = null
        isMonitoring = false
    }

    fun getCapacity() = batteryMonitoringUseCase.getCapacity()
}