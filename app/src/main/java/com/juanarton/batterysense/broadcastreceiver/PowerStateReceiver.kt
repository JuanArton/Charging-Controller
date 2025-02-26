package com.juanarton.batterysense.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.juanarton.batterysense.batterymonitorservice.BatteryMonitorService
import com.juanarton.batterysense.utils.BatteryDataHolder.getAwakeTimeTmp
import com.juanarton.batterysense.utils.BatteryDataHolder.getDeepSleepTime
import com.juanarton.batterysense.utils.BatteryDataHolder.getDeepSleepTimeTmp
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOffDrain
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOffDrainPerHr
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOffTime
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOnDrain
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOnDrainPerHr
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOnTime
import com.juanarton.batterysense.utils.BatteryDataHolder.setAwakeTime
import com.juanarton.batterysense.utils.BatteryDataHolder.setDeepSleepTime
import com.juanarton.batterysense.utils.ChargingDataHolder.getChargingDuration
import com.juanarton.batterysense.utils.ChargingDataHolder.getChargingPerHr
import com.juanarton.batterysense.utils.ChargingDataHolder.setIsCharging
import com.juanarton.batterysense.utils.Utils.calculateCpuAwakePercentage
import com.juanarton.batterysense.utils.Utils.calculateDeepSleepAwakeSpeed
import com.juanarton.batterysense.utils.Utils.calculateDeepSleepPercentage
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import com.juanarton.core.data.domain.batteryMonitoring.repository.IBatteryMonitoringRepository
import com.juanarton.core.utils.BatteryUtils.getBatteryLevel
import com.juanarton.core.utils.BatteryUtils.getCurrentTimeMillis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PowerStateReceiver: BroadcastReceiver() {

    @Inject
    lateinit var iBatteryMonitoringRepository: IBatteryMonitoringRepository

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            Intent.ACTION_POWER_DISCONNECTED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val batteryLevel = getBatteryLevel(context)
                    iBatteryMonitoringRepository.insertLastUnplugged(
                        getCurrentTimeMillis(),
                        batteryLevel
                    )

                    val lastPlugged = iBatteryMonitoringRepository.getLastPlugged()
                    val lastPluggedTime = lastPlugged.first
                    val lastPluggedLevel = lastPlugged.second

                    val chargingSpeed = getChargingPerHr()

                    iBatteryMonitoringRepository.insertChargingHistory(
                        ChargingHistory(
                            null,
                            lastPluggedTime,
                            getCurrentTimeMillis(),
                            lastPluggedLevel,
                            batteryLevel,
                            getBatteryLevel(context) - lastPlugged.second,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            chargingSpeed,
                            getChargingDuration(),
                            true
                        )
                    )
                    resetBatteryData(context)
                }
                setIsCharging(false)
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

                    val screenOn = getScreenOnTime()
                    val screenOff = getScreenOffTime()

                    val screenOnDrain = getScreenOnDrain()
                    val screenOffDrain = getScreenOffDrain()

                    val screenOffDrainPerHrTmp = if (getScreenOffDrainPerHr().isNaN()) 0.0 else getScreenOffDrainPerHr()
                    val screenOnDrainPerHrTmp = if (getScreenOnDrainPerHr().isNaN()) 0.0 else getScreenOnDrainPerHr()

                    val deepSleepPercentage =
                        calculateDeepSleepPercentage(
                            getDeepSleepTime().toDouble(),
                            getScreenOffTime().toDouble()
                        )
                    val awakePercentage = calculateCpuAwakePercentage(deepSleepPercentage)

                    val awakeDuration = getAwakeTimeTmp()
                    val sleepDuration = getDeepSleepTimeTmp()

                    val awakeSpeed = calculateDeepSleepAwakeSpeed(awakePercentage, screenOffDrainPerHrTmp)
                    val sleepSpeed = calculateDeepSleepAwakeSpeed(deepSleepPercentage, screenOffDrainPerHrTmp)

                    iBatteryMonitoringRepository.insertChargingHistory(
                        ChargingHistory(
                            null,
                            lastUnpluggedTime,
                            getCurrentTimeMillis(),
                            lastUnpluggedLevel,
                            batteryLevel,
                            getBatteryLevel(context) - lastUnplugged.second,
                            screenOn,
                            screenOff,
                            screenOnDrain,
                            screenOffDrain,
                            screenOffDrainPerHrTmp,
                            screenOnDrainPerHrTmp,
                            deepSleepPercentage,
                            awakePercentage,
                            awakeDuration,
                            sleepDuration,
                            awakeSpeed,
                            sleepSpeed,
                            null,
                            null,
                            false
                        )
                    )

                    resetBatteryData(context)
                }
                setIsCharging(true)
            }
        }
    }

    private fun resetBatteryData(context: Context) {
        val currentDeepSleep = (SystemClock.elapsedRealtime() - SystemClock.uptimeMillis())/1000
        iBatteryMonitoringRepository.insertDeepSleepInitialValue(0)
        iBatteryMonitoringRepository.insertStartTime(getCurrentTimeMillis())
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
        setDeepSleepTime(0)
        setAwakeTime(0)
        BatteryMonitorService.screenOnBuffer = 0
        BatteryMonitorService.screenOffBuffer = 0
    }
}