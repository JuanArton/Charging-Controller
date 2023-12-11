package com.juanarton.chargingcurrentcontroller.ui.fragments.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.juanarton.core.data.domain.model.BatteryInfo
import com.juanarton.core.data.domain.usecase.DataRepositoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val chargingCurrent = mutableListOf<Entry>()
    
    val lineDataSet = LineDataSet(chargingCurrent, "charging current")
    private val iLineDataSet = mutableListOf<ILineDataSet>(lineDataSet)
    val lineData = LineData(iLineDataSet)
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

    fun addDataAndReduceX(entry: Entry) {
        chargingCurrent.forEachIndexed { index, existingEntry ->
            existingEntry.x = existingEntry.x - 1
        }

        while (chargingCurrent.size >= 61) {
            chargingCurrent.removeAt(0)
        }

        chargingCurrent.add(entry)
    }

    fun stopBatteryMonitoring() {
        scheduledExecutorService?.shutdown()
        scheduledExecutorService = null
        isMonitoring = false
    }
}
