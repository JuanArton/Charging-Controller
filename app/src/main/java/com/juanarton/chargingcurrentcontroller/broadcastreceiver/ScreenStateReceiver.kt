package com.juanarton.chargingcurrentcontroller.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.Action
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.BatteryMonitorService

class ScreenStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = when (intent?.action) {
            Intent.ACTION_SCREEN_ON -> Action.START
            Intent.ACTION_SCREEN_OFF -> Action.STOP
            Intent.ACTION_USER_PRESENT -> Action.START
            else -> null
        }

        /*action?.let {
            val serviceIntent = Intent(context, BatteryMonitorService::class.java).apply {
                this.action = it.name
                Log.d("test", it.name)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("ScreenStateReceiver", "Starting the service in >=26 Mode from a BroadcastReceiver")
                context.startForegroundService(serviceIntent)
            } else {
                Log.d("ScreenStateReceiver", "Starting the service in < 26 Mode from a BroadcastReceiver")
                context.startService(serviceIntent)
            }
        }*/
    }
}
