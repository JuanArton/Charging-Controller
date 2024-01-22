package com.juanarton.chargingcurrentcontroller

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringRepoUseCase
import com.juanarton.core.data.repository.BatteryInfoRepository
import com.juanarton.core.utils.BatteryUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val batteryMonitoringRepoUseCase: BatteryMonitoringRepoUseCase
) : ViewModel() {
    fun insertInitialValue(context: Context) {
        batteryMonitoringRepoUseCase.insertDeepSleepInitialValue(
            0
        )
        batteryMonitoringRepoUseCase.insertScreenOnTime(0)
        batteryMonitoringRepoUseCase.insertScreenOffTime(0)
        batteryMonitoringRepoUseCase.insertStartTime(Date())

        val batteryLevel = BatteryUtils.getBatteryLevel(context)
        batteryMonitoringRepoUseCase.insertBatteryLevel(batteryLevel)
        batteryMonitoringRepoUseCase.insertInitialBatteryLevel(batteryLevel)
    }
}