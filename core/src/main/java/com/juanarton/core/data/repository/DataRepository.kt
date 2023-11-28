package com.juanarton.core.data.repository

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import com.juanarton.core.data.domain.model.BatteryInfo
import com.juanarton.core.data.domain.model.Config
import com.juanarton.core.data.domain.model.Result
import com.juanarton.core.data.domain.repository.DataRepositoryInterface
import com.juanarton.core.utils.Utils
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt


class DataRepository @Inject constructor(private val context: Context): DataRepositoryInterface {

    companion object {
        const val PATH = "/data/adb/modules/3C"
    }

    override fun getConfig(): Flow<Config> =
        flow {
            val targetCurrentCommand =
                Utils.getValue("chargingCurrent").out[0]

            val chargingSwitchCommand =
                Utils.getValue("enableCharging").out[0] == "0"

            val enableLimitCharging =
                Utils.getValue("enableLimitCharging").out[0] == "1"

            val chargingLimitTriggred =
                Utils.getValue("chargingLimitTriggered").out[0] == "1"

            val maxCapacity = Utils.getValue("maxCapacity").out[0]

            emit(
                Config(
                    targetCurrentCommand.toFloat(),
                    chargingSwitchCommand,
                    enableLimitCharging,
                    chargingLimitTriggred,
                    maxCapacity.toFloat()
                )
            )
        }.flowOn(Dispatchers.IO)

    override fun setTargetCurrent(targetCurrent: String): Flow<Result> =
        flow {
            val command = "$PATH/3c.sh setValue chargingCurrent $targetCurrent"
            val result = Shell.cmd(command).exec()

            val readResult = Utils.getValue("chargingCurrent")
            val value = readResult.out[0]

            if (result.isSuccess) {
                if(value == targetCurrent) {
                    emit(Result("Success", true))

                    Shell.cmd("$PATH/3c.sh restartCurrentController").exec()
                } else {
                    emit(Result("Failed to set new target Current", false))
                }
            } else {
                emit(Result("Error: ${result.err}", true))
            }
        }.flowOn(Dispatchers.IO)

    override fun setChargingSwitchStatus(switchStat: Boolean): Flow<Result> =
        flow {
            val stat = if (switchStat) "0" else "1"

            val command = "$PATH/3c.sh setValue enableCharging $stat"
            val result = Shell.cmd(command).exec()

            val readResult = Utils.getValue("enableCharging")
            val value = readResult.out[0] == "1"

            if (result.isSuccess) {
                if(value != switchStat) {
                    emit(Result("Success", true))

                    Shell.cmd("$PATH/3c.sh applyChargingSwitch").exec()
                } else {
                    emit(Result("Failed to switch charging", false))
                }
            } else {
                emit(Result("Error: ${result.err}", false))
            }
        }.flowOn(Dispatchers.IO)

    override fun getBatteryInfo(): Flow<BatteryInfo> = flow {
        val batteryStatusIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, batteryStatusIntentFilter)

        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

        val chargingStatus = if (Build.VERSION.SDK_INT < 26) {
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            if (isCharging) 2 else 3
        } else {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        }

        val chargePlug = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

        val level = batteryStatus?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            (level.toFloat() / scale.toFloat() * 100).toInt()
        }

        val voltage = (batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1).toFloat() / 1000

        val currentNow = (batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)).toFloat() / 1000

        val power = voltage * currentNow / 1000

        emit(
            BatteryInfo(
                chargingStatus,
                acCharge,
                usbCharge,
                level!!,
                voltage,
                -currentNow.toInt(),
                abs(power)
            )
        )
    }.flowOn(Dispatchers.IO)

    override fun setChargingLimitStatus(switchStat: Boolean): Flow<Result> =
        flow {
            val stat = if (switchStat) "1" else "0"

            val command = "$PATH/3c.sh setValue enableLimitCharging $stat"
            val result = Shell.cmd(command).exec()

            val readStatus = Utils.getValue("enableLimitCharging")
            val statusValue = readStatus.out[0] == "1"


            if (result.isSuccess) {
                if(statusValue == switchStat) {
                    emit(Result("Success", true))
                    if(statusValue) {
                        Shell.cmd("$PATH/3c.sh restartBatteryMonitor").exec()
                    } else {
                        Shell.cmd("$PATH/3c.sh killBateryMonitor").exec()
                    }
                } else {
                    emit(Result("Failed to switch charging limit", false))
                }
            } else {
                emit(Result("Error: ${result.err}", false))
            }
        }.flowOn(Dispatchers.IO)

    override fun setMaximumCapacity(maxCapacity: String): Flow<Result> =
        flow {
            val command = "$PATH/3c.sh setValue maxCapacity ${maxCapacity.toInt()}"
            val result = Shell.cmd(command).exec()

            Log.d("test", maxCapacity.toInt().toString())

            val capacityValue = Utils.getValue("maxCapacity").out[0]

            if (result.isSuccess) {
                if(capacityValue == maxCapacity.toInt().toString()) {
                    emit(Result("Success", true))

                    Shell.cmd("$PATH/3c.sh restartBatteryMonitor").exec()
                } else {
                    emit(Result("Failed to switch charging limit", false))
                }
            } else {
                emit(Result("Error: ${result.err}", false))
            }
        }.flowOn(Dispatchers.IO)
}