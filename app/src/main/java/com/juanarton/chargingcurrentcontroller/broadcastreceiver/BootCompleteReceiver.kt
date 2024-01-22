package com.juanarton.chargingcurrentcontroller.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.Action
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.BatteryMonitorService
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.ServiceState
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.getServiceState
import com.juanarton.core.data.domain.batteryMonitoring.repository.BatteryMonitoringRepoInterface
import com.juanarton.core.utils.BatteryUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompleteReceiver: BroadcastReceiver() {

    @Inject
    lateinit var batteryMonitoringRepoInterface: BatteryMonitoringRepoInterface

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && getServiceState(context) == ServiceState.STARTED) {
            CoroutineScope(Dispatchers.IO).launch {
                batteryMonitoringRepoInterface.insertBatteryLevel(BatteryUtils.getBatteryLevel(context))
            }
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
}