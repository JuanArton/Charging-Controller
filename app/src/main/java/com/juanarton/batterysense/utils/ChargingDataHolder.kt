package com.juanarton.batterysense.utils

object ChargingDataHolder {
    private var chargedLevel = 0
    private var chargingPerHr = 0.0
    private var chargingDuration = 0L
    private var isCharging = false

    fun setChargedLevel(chargedLevel: Int) {
        this.chargedLevel = chargedLevel
    }

    fun getChargedLevel(): Int {
        return chargedLevel
    }

    fun setChargingPerHr(chargingPerHr: Double) {
        this.chargingPerHr = chargingPerHr
    }

    fun getChargingPerHr(): Double {
        return chargingPerHr
    }

    fun setChargingDuration(chargingDuration: Long) {
        this.chargingDuration = chargingDuration
    }

    fun getChargingDuration(): Long {
        return chargingDuration
    }

    fun setIsCharging(isCharging: Boolean) {
        this.isCharging = isCharging
    }

    fun getIsCharging(): Boolean {
        return isCharging
    }
}