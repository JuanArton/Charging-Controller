package com.juanarton.batterysense.ui.fragments.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import com.juanarton.core.data.domain.batteryMonitoring.usecase.BatteryMonitoringUseCase
import com.juanarton.core.utils.BatteryUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CalibrationViewModel @Inject constructor(
    private var batteryMonitoringUseCase: BatteryMonitoringUseCase
) : ViewModel() {
    fun getRawCurrent() = batteryMonitoringUseCase.getRawCurrent()

    fun insertUnit(unit: String) = batteryMonitoringUseCase.insertCurrentUnit(unit)

    fun insertCapacty(capacity: Int) {
        batteryMonitoringUseCase.insertCapacity(capacity)
    }

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
        batteryMonitoringUseCase.insertLastUnpPlugged(
            BatteryUtils.getCurrentTimeMillis(),
            BatteryUtils.getBatteryLevel(context)
        )
    }

    fun deleteChargingHistory() = batteryMonitoringUseCase.deleteChargingHistory()
}