package com.juanarton.chargingcurrentcontroller.utils

import com.airbnb.lottie.L

object BatteryDataHolder {
    private var screenOnTime = 0L
    private var screenOffTime = 0L
    private var screenOnDrainPerHr = 0.0
    private var screenOffDrainPerHr = 0.0

    fun addScreenOnTime(screenOnTime: Long) {
        this.screenOnTime = screenOnTime
    }

    fun getScreenOnTime(): Long {
        return screenOnTime
    }

    fun addScreenOffTime(screenOffTime: Long) {
        this.screenOffTime = screenOffTime
    }

    fun getScreenOffTime(): Long {
        return screenOffTime
    }

    fun addScreenOnDrainPerHr(screenOnDrainPerHr: Double) {
        this.screenOnDrainPerHr = screenOnDrainPerHr
    }

    fun getScreenOnDrainPerHr(): Double {
        return screenOnDrainPerHr
    }

    fun addScreenOffDrainPerHr(screenOffDrainPerHr: Double) {
        this.screenOffDrainPerHr = screenOffDrainPerHr
    }

    fun getScreenOffDrainPerHr(): Double {
        return screenOffDrainPerHr
    }
}