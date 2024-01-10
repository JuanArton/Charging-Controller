package com.juanarton.chargingcurrentcontroller.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.Action
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.BatteryMonitorService
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.ServiceState
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.getServiceState

class BootCompleteReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && getServiceState(context) == ServiceState.STARTED) {
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