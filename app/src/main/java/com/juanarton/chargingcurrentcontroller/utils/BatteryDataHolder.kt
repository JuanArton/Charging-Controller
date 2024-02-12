package com.juanarton.chargingcurrentcontroller.utils

object BatteryDataHolder {
    private var screenOnTime = 0L
    private var screenOffTime = 0L
    private var screenOnDrainPerHr = 0.0
    private var screenOffDrainPerHr = 0.0
    private var screenOnDrain = 0
    private var screenOffDrain = 0
    private var lastChargeLevel = 0
    private var cpuAwake = 0L
    private var deepSleep = 0L

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

    fun addScreenOnDrain(screenOnDrain: Int) {
        this.screenOnDrain = screenOnDrain
    }

    fun getScreenOnDrain(): Int {
        return screenOnDrain
    }

    fun addScreenOffDrain(screenOffDrain: Int) {
        this.screenOffDrain = screenOffDrain
    }

    fun getScreenOffDrain(): Int {
        return screenOffDrain
    }

    fun addLastChargeLevel(lastChargeLevel: Int) {
        this.lastChargeLevel = lastChargeLevel
    }

    fun getLastChargeLevel(): Int {
        return lastChargeLevel
    }

    fun addAwakeTime(awake: Long) {
        this.cpuAwake = awake
    }

    fun getAwakeTime(): Long {
        return cpuAwake
    }

    fun addDeepSleepTime(deepSleep: Long) {
        this.deepSleep = deepSleep
    }

    fun getDeepSleepTime(): Long {
        return deepSleep
    }
}