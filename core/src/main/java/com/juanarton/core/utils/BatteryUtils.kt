package com.juanarton.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.juanarton.core.R
import com.juanarton.core.utils.Constants.CHARGING_CYCLES_PATH
import com.topjohnwu.superuser.Shell
import java.io.IOException

object BatteryUtils {

    private var batteryStatusIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    private var batteryStatus: Intent? = null

    private lateinit var batteryManager: BatteryManager

    fun getCurrent(unit: String): Float {
        return if (unit == "μA") {
            getRawCurrent().toFloat() / 1000
        }
        else {
            getRawCurrent().toFloat()
        }
    }

    fun getRawCurrent(): Int {
        return -(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW))
    }

    fun getVoltage(): Float {
        return (batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1).toFloat() / 1000
    }

    fun getLevel(): Int {
        return batteryStatus?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            (level.toFloat() / scale.toFloat() * 100).toInt()
        }!!
    }

    fun getTemperature(): Int {
        return (batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1) / 10
    }

    fun getACCharge(): Boolean {
        val chargePlug = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        return chargePlug == BatteryManager.BATTERY_PLUGGED_AC
    }

    fun getUSBCharge(): Boolean {
        val chargePlug = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        return chargePlug == BatteryManager.BATTERY_PLUGGED_USB
    }

    fun getChargingStatus(): Int {
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return if (Build.VERSION.SDK_INT < 26) {
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            if (isCharging) 2 else 3
        } else {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        }
    }
    fun registerStickyReceiver(context: Context) {
        if (!::batteryManager.isInitialized) {
            batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        }
        batteryStatus = context.registerReceiver(null, batteryStatusIntentFilter)
    }
    fun calculateDeepSleep(deepSleepInitialValue: Long): Long {
        return ((SystemClock.elapsedRealtime() - SystemClock.uptimeMillis()) / 1000 - deepSleepInitialValue)
    }

    fun getBatteryLevel(context: Context): Int {
        val batteryStatusIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, batteryStatusIntentFilter)
        val level = batteryStatus?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            (level.toFloat() / scale.toFloat() * 100).toInt()
        }

        return level!!
    }

    fun getUptime(): Long {
        return SystemClock.uptimeMillis()/1000
    }

    fun getCycleCount(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            (batteryStatus?.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, -1) ?: -1).toString()
        } else {
            try {
                if (Shell.getShell().isRoot) {
                    val command = "cat $CHARGING_CYCLES_PATH"
                    val result = Shell.cmd(command).exec()
                    result.out[0]
                } else {
                    context.getString(R.string.only_android_14_plus)
                }
            } catch (e: IOException) {
                context.getString(R.string.not_available)
            }
        }
    }

    fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    fun determineCurrentUnit(currentList: List<Int>): String {
        return if (currentList.all { countDigits(it) > 5 }) {
            "μA"
        } else {
            "mA"
        }
    }

    private fun countDigits(number: Int): Int {
        return number.toString().length
    }

    @SuppressLint("PrivateApi")
    fun getDesignedCapacity(context: Context): Double {
        var batteryCapacity = 0.0
        val powerProfileClass = "com.android.internal.os.PowerProfile"

        try {
            val powerProfile = Class.forName(powerProfileClass)
                .getConstructor(Context::class.java)
                .newInstance(context)
            batteryCapacity = Class
                .forName(powerProfileClass)
                .getMethod("getBatteryCapacity")
                .invoke(powerProfile) as Double
        } catch (e: Exception) {
            Log.d("Get Battery Capacity", e.toString())
        }

        return batteryCapacity
    }

}