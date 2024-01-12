package com.juanarton.chargingcurrentcontroller

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringRepoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val batteryMonitoringRepoUseCase: BatteryMonitoringRepoUseCase
) : ViewModel() {
    fun insertInitialValue() {
        batteryMonitoringRepoUseCase.insertDeepSleepInitialValue(
            (SystemClock.elapsedRealtime() - SystemClock.uptimeMillis())/1000
        )
        batteryMonitoringRepoUseCase.insertScreenOnTime(0)
        batteryMonitoringRepoUseCase.insertScreenOffTime(0)
        batteryMonitoringRepoUseCase.insertStartTime(Date())
    }
}