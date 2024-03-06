package com.juanarton.batterysense.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.juanarton.batterysense.batterymonitorservice.BatteryMonitorService
import com.juanarton.batterysense.utils.BatteryDataHolder.addAwakeTime
import com.juanarton.batterysense.utils.BatteryDataHolder.addDeepSleepTime
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import com.juanarton.core.data.domain.batteryMonitoring.repository.IBatteryMonitoringRepository
import com.juanarton.core.utils.BatteryUtils.getBatteryLevel
import com.juanarton.core.utils.BatteryUtils.getCurrentTimeMillis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class PowerStateReceiver: BroadcastReceiver() {

    @Inject
    lateinit var iBatteryMonitoringRepository: IBatteryMonitoringRepository

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            Intent.ACTION_POWER_DISCONNECTED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    resetBatteryData(context)
                    val batteryLevel = getBatteryLevel(context)
                    iBatteryMonitoringRepository.insertLastUnplugged(
                        getCurrentTimeMillis(),
                        batteryLevel
                    )

                    val lastPlugged = iBatteryMonitoringRepository.getLastPlugged()
                    val lastPluggedTime = lastPlugged.first
                    val lastPluggedLevel = lastPlugged.second
                    iBatteryMonitoringRepository.insertChargingHistory(
                        ChargingHistory(
                            null,
                            lastPluggedTime,
                            getCurrentTimeMillis(),
                            lastPluggedLevel,
                            batteryLevel,
                            getBatteryLevel(context) - lastPlugged.second,
                            true
                        )
                    )
                }
            }
            Intent.ACTION_POWER_CONNECTED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val batteryLevel = getBatteryLevel(context)
                    iBatteryMonitoringRepository.insertLastPlugged(
                        getCurrentTimeMillis(),
                        batteryLevel
                    )

                    val lastUnplugged = iBatteryMonitoringRepository.getLastUnplugged()
                    val lastUnpluggedTime = lastUnplugged.first
                    val lastUnpluggedLevel = lastUnplugged.second
                    iBatteryMonitoringRepository.insertChargingHistory(
                        ChargingHistory(
                            null,
                            lastUnpluggedTime,
                            getCurrentTimeMillis(),
                            lastUnpluggedLevel,
                            batteryLevel,
                            getBatteryLevel(context) - lastUnplugged.second,
                            false
                        )
                    )
                }
            }
        }
    }

    private fun resetBatteryData(context: Context) {
        val currentDeepSleep = (SystemClock.elapsedRealtime() - SystemClock.uptimeMillis())/1000
        iBatteryMonitoringRepository.insertDeepSleepInitialValue(0)
        iBatteryMonitoringRepository.insertStartTime(Date())
        iBatteryMonitoringRepository.insertScreenOnTime(0)
        iBatteryMonitoringRepository.insertScreenOffTime(0)
        iBatteryMonitoringRepository.insertCpuAwake(0)
        val level = getBatteryLevel(context)
        iBatteryMonitoringRepository.insertBatteryLevel(level)
        iBatteryMonitoringRepository.insertInitialBatteryLevel(level)
        iBatteryMonitoringRepository.insertScreenOnDrain(0)
        iBatteryMonitoringRepository.insertScreenOffDrain(0)
        BatteryMonitorService.deepSleepInitialValue = currentDeepSleep
        BatteryMonitorService.deepSleepBuffer = 0
        addDeepSleepTime(0)
        addAwakeTime(0)
        BatteryMonitorService.screenOnBuffer = 0
        BatteryMonitorService.screenOffBuffer = 0
    }
}