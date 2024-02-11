package com.juanarton.chargingcurrentcontroller.ui.fragments.dashboard

import android.annotation.SuppressLint
import android.content.Context
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
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.getAwakeTime
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.getDeepSleepTime
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.getLastChargeLevel
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.getScreenOffDrain
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.getScreenOffDrainPerHr
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.getScreenOffTime
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.getScreenOnDrain
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.getScreenOnDrainPerHr
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.getScreenOnTime
import com.juanarton.chargingcurrentcontroller.utils.ServiceUtil.formatTime
import com.juanarton.chargingcurrentcontroller.utils.Utils.calculateCpuAwakePercentage
import com.juanarton.chargingcurrentcontroller.utils.Utils.calculateDeepSleepAwakeSpeed
import com.juanarton.chargingcurrentcontroller.utils.Utils.calculateDeepSleepPercentage
import com.juanarton.chargingcurrentcontroller.utils.Utils.formatDeepSleepAwake
import com.juanarton.chargingcurrentcontroller.utils.Utils.formatSpeed
import com.juanarton.chargingcurrentcontroller.utils.Utils.formatUsagePerHour
import com.juanarton.chargingcurrentcontroller.utils.Utils.formatUsagePercentage
import com.juanarton.core.utils.Utils
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
                    }
                    chipTemperature.id -> {
                        showTemperature()
                        tvChartInfoTitle1.text = currentTitle
                        tvChartInfoTitle2.text = powerTitle

                        ivChartInfo1.setImageResource(R.drawable.charging_current)
                        ivChartInfo2.setImageResource(R.drawable.power)
                    }
                    chipPower.id -> {
                        showPower()
                        tvChartInfoTitle1.text = currentTitle
                        tvChartInfoTitle2.text = temperatureTitle

                        ivChartInfo1.setImageResource(R.drawable.charging_current)
                        ivChartInfo2.setImageResource(R.drawable.temperature)
                    }
                }
            }
        }

        dashboardViewModel.batteryInfo.observe(viewLifecycleOwner) {
            updateUsageData()
            if (firstRun) {
                dashboardViewModel.currentMin = abs(it.currentNow)
                dashboardViewModel.tempMin = abs(it.temperature)
                dashboardViewModel.powerMin = abs((it.power * 100).toInt())
                firstRun = false
            }

            binding?.apply {
                changeWaveHeight(waveAnimation, rescaleNumber(it.level))

                tvBatteryPercentage.text = buildString {
                    append(it.level)
                    append("%")
                }

                tvBatteryCapacity.text = buildString{
                    append("${(batteryCapacity * it.level / 100).toInt()} ${getString(R.string.mah)}")
                    append(" of ")
                    append("${batteryCapacity.toInt()} ${getString(R.string.mah)}")
                }

                tvBatteryStatus.text = Utils.mapBatteryStatus(it.status, requireContext())

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
                    powerMin = minChecker(powerMin, abs((it.power * 100).toInt()))

                    currentMax = maxChecker(currentMax, abs(it.currentNow))
                    tempMax = maxChecker(tempMax, abs(it.temperature))
                    powerMax = maxChecker(powerMax, abs((it.power * 100).toInt()))

                    when {
                        currentGraph -> {
                            setMinMaxText(tvChartMin, currentMin)
                            setMinMaxText(tvChartMax, currentMax)
                        }
                        temperatureGraph -> {
                            setMinMaxText(tvChartMin, tempMin)
                            setMinMaxText(tvChartMax, tempMax)
                        }
                        powerGraph -> {
                            setMinMaxText(tvChartMin, powerMin)
                            setMinMaxText(tvChartMax, powerMax)
                        }
                    }

                    tvUptimeValue.text = formatTime(it.uptime)
                    tvCycleValue.text = it.cycleCount
                    updateUsageData()
                }
            }
        }
        dashboardViewModel.startBatteryMonitoring()
    }

    private fun setUpLineChart(lineDataSet: LineDataSet, lineData: LineData, fillGradient: Drawable?) {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)

        lineDataSet.apply {
            setDrawValues(false)
            setDrawCircles(false)
            axisDependency = YAxis.AxisDependency.RIGHT
            valueFormatter = DefaultValueFormatter(0)
            setDrawFilled(true)
            isHighlightEnabled = false
            fillDrawable = fillGradient
            lineWidth = 2.0F
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = typedValue.data
        }

        binding?.chargingCurrentChart?.apply {
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setLabelCount(13, true)
                axisMinimum = 0F
                axisMaximum = 60F
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String = "${60 - value.toInt()}s"
                }
                labelRotationAngle = -45f
                textColor = typedValue.data
            }

            axisRight.apply {
                isEnabled = true
                setLabelCount(8, true)
                granularity = 100F
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String = value.toInt().toString()
                }
                textColor = typedValue.data
            }

            axisLeft.isEnabled = false
            isAutoScaleMinMaxEnabled = true
            axisRight.enableGridDashedLine(10f, 10f, 0f)
            xAxis.enableGridDashedLine(10f, 10f, 0f)
            description.isEnabled = false
            legend.isEnabled = false
            data = lineData
            animateXY(1000, 1000)
        }
    }

    private fun showChart(lineDataSet: LineDataSet, lineData: LineData, gradientDrawable: Int) {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)

        setUpLineChart(lineDataSet, lineData, ContextCompat.getDrawable(requireContext(), gradientDrawable))
    }

    private fun showCurrent() {
        showChart(
            dashboardViewModel.currentLineDataSet,
            dashboardViewModel.currentData,
            R.drawable.chart_gradient
        )
        currentGraph = true
        temperatureGraph = false
        powerGraph = false
    }

    private fun showTemperature() {
        showChart(
            dashboardViewModel.temperatureLineDataSet,
            dashboardViewModel.temperatureData,
            R.drawable.chart_gradient
        )
        currentGraph = false
        temperatureGraph = true
        powerGraph = false
    }

    private fun showPower() {
        showChart(
            dashboardViewModel.powerLineDataSet,
            dashboardViewModel.powerData,
            R.drawable.chart_gradient
        )
        currentGraph = false
        temperatureGraph = false
        powerGraph = true
    }

    private fun updateUsageData() {
        val screenOffDrainPerHrTmp = if (getScreenOffDrainPerHr().isNaN()) 0.0 else getScreenOffDrainPerHr()
        val screenOnDrainPerHrTmp = if (getScreenOnDrainPerHr().isNaN()) 0.0 else getScreenOnDrainPerHr()
        val deepSleepPercentage =
            calculateDeepSleepPercentage(
                getDeepSleepTime().toDouble(), getScreenOffTime().toDouble()
            )
        val cpuAwakePercentage =
            calculateCpuAwakePercentage(
                deepSleepPercentage
            )
        binding?.apply {
            tvScreenOnValue.text = formatTime(getScreenOnTime())
            tvScreenOffValue.text = formatTime(getScreenOffTime())
            tvTotalTimeValue.text = formatTime(getScreenOffTime() + getScreenOnTime())
            tvScreenOnUsage.text = formatUsagePercentage(getScreenOnDrain(), getLastChargeLevel())
            tvScreenOffUsage.text = formatUsagePercentage(getScreenOffDrain(), getLastChargeLevel())
            tvTotalUsage.text = formatUsagePercentage(
                getScreenOffDrain() + getScreenOnDrain(),
                getLastChargeLevel()
            )
            tvActiveDrainPerHrValue.text = formatUsagePerHour(screenOnDrainPerHrTmp)
            tvIdleDrainPerHrValue.text = formatUsagePerHour(screenOffDrainPerHrTmp)
            tvAwakeValue.text = formatTime(getAwakeTime())
            tvDeepSleepValue.text = formatTime(getDeepSleepTime())
            tvAwakePercentage.text = formatDeepSleepAwake(cpuAwakePercentage)
            tvDeepSleepPercentage.text = formatDeepSleepAwake(deepSleepPercentage)
            tvAwakePerHrValue.text = formatSpeed(
                calculateDeepSleepAwakeSpeed(cpuAwakePercentage, screenOffDrainPerHrTmp)
            )
            tvDeepSleepPerHrValue.text = formatSpeed(
                calculateDeepSleepAwakeSpeed(deepSleepPercentage, screenOffDrainPerHrTmp)
            )
        }
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

    private fun rescaleNumber(input: Int): Int {
        return (input.toDouble() / 100.0 * 246.0).toInt()
    }

    private fun changeWaveHeight(view: View, heightInDp: Int) {
        val density = view.resources.displayMetrics.density
        val heightInPixels = (heightInDp * density).toInt()

        val layoutParams: ViewGroup.LayoutParams = view.layoutParams
        layoutParams.height = heightInPixels
        view.layoutParams = layoutParams
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
        if (!firstRun) {
            dashboardViewModel.startBatteryMonitoring()
        }
    }
}