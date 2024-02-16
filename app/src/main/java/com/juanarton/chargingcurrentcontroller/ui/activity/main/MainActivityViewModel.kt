package com.juanarton.chargingcurrentcontroller.ui.activity.main

import android.content.Context
import androidx.lifecycle.ViewModel
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringUseCase
import com.juanarton.core.utils.BatteryUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val batteryMonitoringUseCase: BatteryMonitoringUseCase
) : ViewModel() {
    fun insertInitialValue(context: Context) {
        batteryMonitoringUseCase.insertDeepSleepInitialValue(
            0
        )
        batteryMonitoringUseCase.insertScreenOnTime(0)
        batteryMonitoringUseCase.insertScreenOffTime(0)
        batteryMonitoringUseCase.insertStartTime(Date())

        val batteryLevel = BatteryUtils.getBatteryLevel(context)
        batteryMonitoringUseCase.insertBatteryLevel(batteryLevel)
        batteryMonitoringUseCase.insertInitialBatteryLevel(batteryLevel)
    }
}