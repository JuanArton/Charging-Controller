package com.juanarton.chargingcurrentcontroller.ui.fragments.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringUseCase
import com.juanarton.core.data.repository.BatteryMonitoringRepository
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

    private val _batteryInfo = MutableLiveData<BatteryInfo>()
    val batteryInfo = _batteryInfo

    private var scheduledExecutorService: ScheduledExecutorService? = null
    private var isMonitoring = false

    var currentMin = 0
    var currentMax = 0
    var tempMin = 0
    var tempMax = 0
    var powerMin = 0
    var powerMax = 0

    fun startBatteryMonitoring() {
        if (!isMonitoring) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
            scheduledExecutorService?.scheduleAtFixedRate(
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
}
