package com.juanarton.chargingcurrentcontroller.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.juanarton.chargingcurrentcontroller.batterymonitorservice.BatteryMonitorService
import com.juanarton.core.data.domain.batteryMonitoring.repository.BatteryMonitoringRepoInterface
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BatteryStateReceiver : BroadcastReceiver(){

    @Inject
    lateinit var batteryMonitoringRepoInterface: BatteryMonitoringRepoInterface
    private var batteryUsed = 0
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {

            val batteryLevel = batteryMonitoringRepoInterface.getBatteryLevel(context)
            val newLevel = intent.getIntExtra("level", -1)

            if (newLevel < batteryMonitoringRepoInterface.getBatteryLevel(context)) {
                batteryUsed = batteryLevel - newLevel

                when(BatteryMonitorService.screenOn) {
                    true -> {
                        val drain = batteryMonitoringRepoInterface.getScreenOnDrain()
                        batteryMonitoringRepoInterface.insertScreenOnDrain(drain + batteryUsed)
                    }
                    false -> {
                        val drain = batteryMonitoringRepoInterface.getScreenOffDrain()
                        batteryMonitoringRepoInterface.insertScreenOffDrain(drain + batteryUsed)
                    }
                }
                batteryMonitoringRepoInterface.insertBatteryLevel(newLevel)
                Log.d("test1", newLevel.toString())
                batteryUsed = 0
            }
        }
    }

}