package com.juanarton.chargingcurrentcontroller.ui.fragments.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.juanarton.chargingcurrentcontroller.R
import com.juanarton.chargingcurrentcontroller.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs


@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding

    private var firstRun = true

    private val dashboardViewModel: DashboardViewModel by viewModels()

    private var currentGraph = true
    private var temperatureGraph = false
    private var powerGraph = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("PrivateApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        val mainColor = typedValue.data
        val currentBackground = Color.argb(128, Color.red(mainColor), Color.green(mainColor), Color.blue(mainColor))
        val temperatureBackground = ContextCompat.getColor(requireContext(), R.color.temperatureBackground)
        val powerBackground = ContextCompat.getColor(requireContext(), R.color.powerBackground)
        val temperatureColor = ContextCompat.getColor(requireContext(), R.color.temperature)
        val powerColor = ContextCompat.getColor(requireContext(), R.color.power)

        var batteryCapacity = 0.0
        val powerProfileClass = "com.android.internal.os.PowerProfile"

        try {
            val powerProfile = Class.forName(powerProfileClass)
                .getConstructor(Context::class.java)
                .newInstance(requireContext())
            batteryCapacity = Class
                .forName(powerProfileClass)
                .getMethod("getBatteryCapacity")
                .invoke(powerProfile) as Double
        } catch (e: Exception) {
            Log.d("Get Battery Capacity", e.toString())
        }

        showCurrent()

        val currentTitle = getString(R.string.current)
        val temperatureTitle = getString(R.string.temperature)
        val powerTitle = getString(R.string.power)

        val temperatureUnit = getString(R.string.degree_symbol)
        val currentUnit = getString(R.string.ma)
        val wattageUnit = getString(R.string.wattage)

        binding?.apply {
            cgGraphSelector.check(R.id.chipChargingCurrent)
            cgGraphSelector.setOnCheckedStateChangeListener { group, _ ->
                when (group.checkedChipId) {
                    chipChargingCurrent.id -> {
                        showCurrent()
                        tvChartInfoTitle1.text = temperatureTitle
                        tvChartInfoTitle2.text = powerTitle

                        ivChartInfo1.setImageResource(R.drawable.temperature)
                        ivChartInfo2.setImageResource(R.drawable.power)

                        ivChartInfo1.backgroundTintList = ColorStateList.valueOf(temperatureBackground)
                        ivChartInfo2.backgroundTintList = ColorStateList.valueOf(powerBackground)

                        tvChartMin.setTextColor(mainColor)
                        tvChartMax.setTextColor(mainColor)
                        tvChartValue.setTextColor(mainColor)
                    }
                    chipTemperature.id -> {
                        showTemperature()
                        tvChartInfoTitle1.text = currentTitle
                        tvChartInfoTitle2.text = powerTitle

                        ivChartInfo1.setImageResource(R.drawable.charging_current)
                        ivChartInfo2.setImageResource(R.drawable.power)

                        ivChartInfo1.backgroundTintList = ColorStateList.valueOf(currentBackground)
                        ivChartInfo2.backgroundTintList = ColorStateList.valueOf(powerBackground)

                        tvChartMin.setTextColor(temperatureColor)
                        tvChartMax.setTextColor(temperatureColor)
                        tvChartValue.setTextColor(temperatureColor)
                    }
                    chipPower.id -> {
                        showPower()
                        tvChartInfoTitle1.text = currentTitle
                        tvChartInfoTitle2.text = temperatureTitle

                        ivChartInfo1.setImageResource(R.drawable.charging_current)
                        ivChartInfo2.setImageResource(R.drawable.temperature)

                        ivChartInfo1.backgroundTintList = ColorStateList.valueOf(currentBackground)
                        ivChartInfo2.backgroundTintList = ColorStateList.valueOf(temperatureBackground)

                        tvChartMin.setTextColor(powerColor)
                        tvChartMax.setTextColor(powerColor)
                        tvChartValue.setTextColor(powerColor)
                    }
                }
            }
        }

        dashboardViewModel.batteryInfo.observe(viewLifecycleOwner) {

            if (firstRun) {
                dashboardViewModel.currentMin = abs(it.currentNow)
                dashboardViewModel.tempMin = abs(it.temperature)
                dashboardViewModel.powerMin = abs(it.power.toInt())
                firstRun = false
            }

            binding?.apply {
                arcBatteryPercentage.progress = it.level
                arcBatteryPercentage.bottomText = buildString {
                    append((batteryCapacity * it.level/100).toInt())
                    append(" ")
                    append(currentUnit)
                }

                tvBatteryStatus.text = when (it.status){
                    1 -> getString(R.string.unknown)
                    2 -> getString(R.string.charging)
                    3 -> getString(R.string.discharging)
                    4 -> getString(R.string.not_charging)
                    else -> getString(R.string.full)
                }

                tvBatteryVoltage.text = it.voltage.toString()

                tvChargingType.text = when {
                    it.acCharge -> getString(R.string.ac)
                    it.usbCharge -> getString(R.string.usb)
                    else -> getString(R.string.battery)
                }

                val powerValue = buildString {
                    append(String.format("%.2f", it.power))
                    append(wattageUnit)
                }

                val currentValue = buildString {
                    append(abs(it.currentNow))
                    append(currentUnit)
                }

                val temperatureValue = buildString {
                    append(it.temperature)
                    append(temperatureUnit)
                }

                with(dashboardViewModel) {
                    addData(
                        Entry(60F, abs(it.currentNow.toFloat())),
                        Entry(60F, abs(it.temperature.toFloat())),
                        Entry(60F, abs(it.power))
                    )
                    currentData.notifyDataChanged()
                    chargingCurrentChart.notifyDataSetChanged()
                    chargingCurrentChart.invalidate()

                    when {
                        currentGraph -> {
                            tvChartValue.text = currentValue
                            tvChartInfoValue1.text = temperatureValue
                            tvChartInfoValue2.text = powerValue
                        }
                        temperatureGraph -> {
                            tvChartValue.text = temperatureValue
                            tvChartInfoValue1.text = currentValue
                            tvChartInfoValue2.text = powerValue
                        }
                        powerGraph -> {
                            tvChartValue.text = powerValue
                            tvChartInfoValue1.text = currentValue
                            tvChartInfoValue2.text = temperatureValue
                        }
                    }

                    currentMin = minChecker(currentMin, abs(it.currentNow))
                    tempMin = minChecker(tempMin, abs(it.temperature))
                    powerMin = minChecker(powerMin, abs(it.power.toInt()))

                    currentMax = maxChecker(currentMax, abs(it.currentNow))
                    tempMax = maxChecker(tempMax, abs(it.temperature))
                    powerMax = maxChecker(powerMax, abs(it.power.toInt()))

                    setMinMaxText(tvChartMin, currentMin)
                    setMinMaxText(tvChartMax, currentMax)
                }
            }
        }
        dashboardViewModel.startBatteryMonitoring()
    }

    private fun setUpLineChart(lineDataSet: LineDataSet, lineData: LineData, mainColor: Int, accentColor: Int, fillGradient: Drawable?) {
        lineDataSet.apply {
            color = mainColor
            setDrawValues(false)
            setDrawCircles(false)
            axisDependency = YAxis.AxisDependency.RIGHT
            valueFormatter = DefaultValueFormatter(0)
            setDrawFilled(true)
            isHighlightEnabled = false
            fillDrawable = fillGradient
        }

        binding?.chargingCurrentChart?.apply {
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setLabelCount(13, true)
                axisMinimum = 0F
                axisMaximum = 60F
                textColor = mainColor
                axisLineColor = mainColor
                gridColor = accentColor
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String = "${60 - value.toInt()}s"
                }
                labelRotationAngle = -45f
            }

            axisRight.apply {
                isEnabled = true
                setLabelCount(8, true)
                textColor = mainColor
                axisLineColor = mainColor
                gridColor = accentColor
                granularity = 100F
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String = value.toInt().toString()
                }
            }

            axisLeft.isEnabled = false
            isAutoScaleMinMaxEnabled = true
            axisRight.enableGridDashedLine(10f, 10f, 0f)
            xAxis.enableGridDashedLine(10f, 10f, 0f)
            description.isEnabled = false
            legend.isEnabled = false
            data = lineData
        }
    }

    private fun showChart(lineDataSet: LineDataSet, lineData: LineData, gradientDrawable: Int, colorId: Int) {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        val mainColor = if (colorId == 0) typedValue.data else ContextCompat.getColor(requireContext(), colorId)
        val accentColor = ContextCompat.getColor(requireContext(), R.color.accent)

        setUpLineChart(lineDataSet, lineData, mainColor, accentColor, ContextCompat.getDrawable(requireContext(), gradientDrawable))
    }

    private fun showCurrent() {
        showChart(
            dashboardViewModel.currentLineDataSet,
            dashboardViewModel.currentData,
            R.drawable.chart_gradient_current,
            0
        )
        currentGraph = true
        temperatureGraph = false
        powerGraph = false
    }

    private fun showTemperature() {
        showChart(
            dashboardViewModel.temperatureLineDataSet,
            dashboardViewModel.temperatureData,
            R.drawable.chart_gradient_temperature,
            R.color.temperature
        )
        currentGraph = false
        temperatureGraph = true
        powerGraph = false
    }

    private fun showPower() {
        showChart(
            dashboardViewModel.powerLineDataSet,
            dashboardViewModel.powerData,
            R.drawable.chart_gradient_power,
            R.color.power
        )
        currentGraph = false
        temperatureGraph = false
        powerGraph = true
    }

    private fun setMinMaxText(tv: TextView, value: Int) {
        tv.text = buildString {
            when {
                currentGraph -> {
                    append(value)
                    append(getString(R.string.ma))
                }
                temperatureGraph -> {
                    append(value)
                    append(getString(R.string.degree_symbol))
                }
                powerGraph -> {
                    append(String.format("%.2f", value / 100.0))
                    append(getString(R.string.wattage))
                }
            }
        }
    }

    private fun minChecker(oldValue: Int, newValue: Int): Int {
        return if (newValue < oldValue ) newValue else oldValue
    }

    private fun maxChecker(oldValue: Int, newValue: Int): Int {
        return if (newValue > oldValue ) newValue else oldValue
    }

    override fun onPause() {
        super.onPause()
        dashboardViewModel.stopBatteryMonitoring()
    }

    override fun onStop() {
        super.onStop()
        dashboardViewModel.stopBatteryMonitoring()
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.startBatteryMonitoring()
    }
}