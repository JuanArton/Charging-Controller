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
import com.juanarton.core.data.domain.batteryMonitoring.repository.BatteryMonitoringRepoInterface
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
                    BatteryMonitorService.deepSleepInitialValue = currentDeepSleep
                    BatteryMonitorService.deepSleepBuffer = 0
                    BatteryMonitorService.deepSleep = 0
                    BatteryMonitorService.cpuAwake = 0
                    BatteryMonitorService.screenOnBuffer = 0
                    BatteryMonitorService.screenOffBuffer = 0
                }
            }
        }
    }

    private fun actionOnService(action: Action, context: Context) {
        if (getServiceState(context) == ServiceState.STOPPED && action == Action.STOP) return
        Intent(context, BatteryMonitorService::class.java).also {
            it.action = Action.START.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("BootCompleteReceiver", "Starting the service in >=26 Mode from a BroadcastReceiver")
                context.startForegroundService(it)
                return
            }
            Log.d("BootCompleteReceiver", "Starting the service in < 26 Mode from a BroadcastReceiver")
            context.startService(it)
        }
    }
}