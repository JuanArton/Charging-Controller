package com.juanarton.batterysense.utils

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
    private var cpuAwakeTmp = 0L
    private var deepSleepTmp = 0L

    fun setScreenOnTime(screenOnTime: Long) {
        this.screenOnTime = screenOnTime
    }

    fun getScreenOnTime(): Long {
        return screenOnTime
    }

    fun setScreenOffTime(screenOffTime: Long) {
        this.screenOffTime = screenOffTime
    }

    fun getScreenOffTime(): Long {
        return screenOffTime
    }

    fun setScreenOnDrainPerHr(screenOnDrainPerHr: Double) {
        this.screenOnDrainPerHr = screenOnDrainPerHr
    }

    fun getScreenOnDrainPerHr(): Double {
        return screenOnDrainPerHr
    }

    fun setScreenOffDrainPerHr(screenOffDrainPerHr: Double) {
        this.screenOffDrainPerHr = screenOffDrainPerHr
    }

    fun getScreenOffDrainPerHr(): Double {
        return screenOffDrainPerHr
    }

    fun setScreenOnDrain(screenOnDrain: Int) {
        this.screenOnDrain = screenOnDrain
    }

    fun getScreenOnDrain(): Int {
        return screenOnDrain
    }

    fun setScreenOffDrain(screenOffDrain: Int) {
        this.screenOffDrain = screenOffDrain
    }

    fun getScreenOffDrain(): Int {
        return screenOffDrain
    }

    fun setLastChargeLevel(lastChargeLevel: Int) {
        this.lastChargeLevel = lastChargeLevel
    }

    fun getLastChargeLevel(): Int {
        return lastChargeLevel
    }

    fun setAwakeTime(awake: Long) {
        this.cpuAwake = awake
        if (awake > 0) cpuAwakeTmp = cpuAwake
    }

    fun getAwakeTime(): Long {
        return cpuAwake
    }

    fun getAwakeTimeTmp(): Long {
        return cpuAwakeTmp
    }

    fun setDeepSleepTime(deepSleep: Long) {
        this.deepSleep = deepSleep
        if (deepSleep > 0) deepSleepTmp = deepSleep
    }

    fun getDeepSleepTime(): Long {
        return deepSleep
    }

    fun getDeepSleepTimeTmp(): Long {
        return deepSleepTmp
    }
}