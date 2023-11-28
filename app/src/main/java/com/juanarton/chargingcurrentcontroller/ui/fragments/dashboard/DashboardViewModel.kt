package com.juanarton.chargingcurrentcontroller.ui.fragments.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanarton.core.data.domain.model.BatteryInfo
import com.juanarton.core.data.domain.usecase.DataRepositoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val dataRepositoryUseCase: DataRepositoryUseCase) : ViewModel() {

    private val _batteryInfo = MutableLiveData<BatteryInfo>()
    val batteryInfo = _batteryInfo

    private var scheduledExecutorService: ScheduledExecutorService? = null

    private var isMonitoring = false

    fun startBatteryMonitoring() {
        if (!isMonitoring) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
            scheduledExecutorService?.scheduleAtFixedRate(
                {
                    viewModelScope.launch {
                        dataRepositoryUseCase.getBatteryInfo().collect {
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
