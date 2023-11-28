package com.juanarton.chargingcurrentcontroller.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

class BatteryStateReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
            val current = intent.getIntExtra(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW.toString(), 0)
        }
    }

}