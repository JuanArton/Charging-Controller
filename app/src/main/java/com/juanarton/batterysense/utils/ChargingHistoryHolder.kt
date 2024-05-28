package com.juanarton.batterysense.utils

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

object ChargingHistoryHolder {
    val batteryCurrent = mutableListOf<Entry>()
    val currentLineDataSet = LineDataSet(batteryCurrent, "battery current")
    val currentILineDataSet = mutableListOf<ILineDataSet>(currentLineDataSet)
    val currentData = LineData(currentILineDataSet)

    private val batteryTemperature = mutableListOf<Entry>()
    val temperatureLineDataSet = LineDataSet(batteryTemperature, "battery temperature")
    private val temperatureILineDataSet = mutableListOf<ILineDataSet>(temperatureLineDataSet)
    val temperatureData = LineData(temperatureILineDataSet)

    private val batteryPower = mutableListOf<Entry>()
    val powerLineDataSet = LineDataSet(batteryPower, "battery power")
    private val powerILineDataSet = mutableListOf<ILineDataSet>(powerLineDataSet)
    val powerData = LineData(powerILineDataSet)

    var chargingCurrentMin = 0
    var chargingCurrentMax = 0
    var chargingTempMin = 0
    var chargingTempMax = 0
    var chargingPowerMin = 0
    var chargingPowerMax = 0

    fun addData(current: Entry, temperature: Entry, power: Entry) {
        batteryCurrent.forEachIndexed { _, it ->
            it.x -= 1
        }

        batteryTemperature.forEachIndexed { _, it ->
            it.x -= 1
        }

        batteryPower.forEachIndexed { _, it ->
            it.x -= 1
        }

        when {
            batteryCurrent.size >= 61 -> batteryCurrent.removeAt(0)
            batteryTemperature.size >= 61 -> batteryTemperature.removeAt(0)
            batteryPower.size >= 61 -> batteryPower.removeAt(0)
        }

        batteryCurrent.add(current)
        batteryTemperature.add(temperature)
        batteryPower.add(power)
    }
}