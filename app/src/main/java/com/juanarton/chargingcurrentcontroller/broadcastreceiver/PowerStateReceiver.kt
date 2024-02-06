package com.juanarton.chargingcurrentcontroller.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.Action
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.BatteryMonitorService
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.ServiceState
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.getServiceState
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.addAwakeTime
import com.juanarton.core.data.domain.batteryMonitoring.repository.BatteryMonitoringRepoInterface
import com.juanarton.core.utils.BatteryUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class PowerStateReceiver: BroadcastReceiver() {

    @Inject
    lateinit var batteryMonitoringRepoInterface: BatteryMonitoringRepoInterface

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            Intent.ACTION_POWER_DISCONNECTED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val currentDeepSleep = (SystemClock.elapsedRealtime() - SystemClock.uptimeMillis())/1000
                    batteryMonitoringRepoInterface.insertDeepSleepInitialValue(0)
                    batteryMonitoringRepoInterface.insertStartTime(Date())
                    batteryMonitoringRepoInterface.insertScreenOnTime(0)
                    batteryMonitoringRepoInterface.insertScreenOffTime(0)
                    batteryMonitoringRepoInterface.insertCpuAwake(0)
                    val level = BatteryUtils.getBatteryLevel(context)
                    batteryMonitoringRepoInterface.insertBatteryLevel(level)
                    batteryMonitoringRepoInterface.insertInitialBatteryLevel(level)
                    batteryMonitoringRepoInterface.insertScreenOnDrain(0)
                    batteryMonitoringRepoInterface.insertScreenOffDrain(0)
                    BatteryMonitorService.deepSleepInitialValue = currentDeepSleep
                    BatteryMonitorService.deepSleepBuffer = 0
                    BatteryMonitorService.deepSleep = 0
                    addAwakeTime(0)
                    BatteryMonitorService.screenOnBuffer = 0
                    BatteryMonitorService.screenOffBuffer = 0
                }
            }
        }
    }
}