package com.juanarton.batterysense.ui.fragments.charging

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.FragmentChargingBinding
import com.juanarton.batterysense.ui.activity.batteryhistory.BatteryHistoryActivity
import com.juanarton.batterysense.utils.ChargingDataHolder.getChargedLevel
import com.juanarton.batterysense.utils.ChargingDataHolder.getChargingDuration
import com.juanarton.batterysense.utils.ChargingDataHolder.getChargingPerHr
import com.juanarton.batterysense.utils.ChargingDataHolder.getIsCharging
import com.juanarton.batterysense.utils.ChargingHistoryHolder
import com.juanarton.batterysense.utils.FragmentUtil.changeViewHeight
import com.juanarton.batterysense.utils.FragmentUtil.isEmitting
import com.juanarton.batterysense.utils.FragmentUtil.maxChecker
import com.juanarton.batterysense.utils.FragmentUtil.minChecker
import com.juanarton.batterysense.utils.FragmentUtil.rescaleNumber
import com.juanarton.batterysense.utils.Utils.formatUsagePerHour
import com.juanarton.core.utils.Utils
import com.juanarton.core.utils.Utils.formatTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random


@AndroidEntryPoint
class ChargingFragment : Fragment() {

    private var _binding: FragmentChargingBinding? = null
    private val binding get() = _binding

    private var firstRun = true

    private val chargingViewModel: ChargingViewModel by viewModels()

    private var currentGraph = true
    private var temperatureGraph = false
    private var powerGraph = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChargingBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val batteryCapacity = chargingViewModel.getCapacity()

        showCurrent()

        val currentTitle = getString(R.string.current)
        val temperatureTitle = getString(R.string.temperature)
        val powerTitle = getString(R.string.power)

        val temperatureUnit = getString(R.string.degree_symbol)
        val currentUnit = getString(R.string.ma)
        val wattageUnit = getString(R.string.wattage)

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)

        binding?.apply {
            batteryHistoryPanel.cgGraphSelector.check(R.id.chipChargingCurrent)
            batteryHistoryPanel.cgGraphSelector.setOnCheckedStateChangeListener { group, _ ->
                when (group.checkedChipId) {
                    batteryHistoryPanel.chipChargingCurrent.id -> {
                        showCurrent()
                        batteryInfoPanel.cvChartInfo1.titleText = temperatureTitle
                        batteryInfoPanel.cvChartInfo2.titleText = powerTitle

                        batteryInfoPanel.cvChartInfo1.iconResource = R.drawable.temperature
                        batteryInfoPanel.cvChartInfo2.iconResource = R.drawable.power
                    }
                    batteryHistoryPanel.chipTemperature.id -> {
                        showTemperature()
                        batteryInfoPanel.cvChartInfo1.titleText = currentTitle
                        batteryInfoPanel.cvChartInfo2.titleText = powerTitle

                        batteryInfoPanel.cvChartInfo1.iconResource = R.drawable.charging_current
                        batteryInfoPanel.cvChartInfo2.iconResource = R.drawable.power
                    }
                    batteryHistoryPanel.chipPower.id -> {
                        showPower()
                        batteryInfoPanel.cvChartInfo1.titleText = currentTitle
                        batteryInfoPanel.cvChartInfo2.titleText = temperatureTitle

                        batteryInfoPanel.cvChartInfo1.iconResource = R.drawable.charging_current
                        batteryInfoPanel.cvChartInfo2.iconResource = R.drawable.temperature
                    }
                }
            }
            batteryHistoryPanel.btFullHistory.setOnClickListener {
                startActivity(Intent(requireContext(), BatteryHistoryActivity::class.java))
            }
            batteryHistoryPanel.tvFullHistory.setOnClickListener {
                startActivity(Intent(requireContext(), BatteryHistoryActivity::class.java))
            }

            emitBubbles(typedValue)
        }

        chargingViewModel.batteryInfo.observe(viewLifecycleOwner) {
            updateChargingData()
            if (firstRun) {
                chargingViewModel.currentMin = abs(it.currentNow)
                chargingViewModel.tempMin = abs(it.temperature)
                chargingViewModel.powerMin = abs((it.power * 100).toInt())
                firstRun = false
            }

            binding?.apply {
                changeViewHeight(batteryInfoPanel.waveAnimation, rescaleNumber(it.level))
                changeViewHeight(batteryInfoPanel.bubbleEmitter, rescaleNumber(it.level))

                batteryInfoPanel.tvBatteryPercentage.text = buildString {
                    append(it.level)
                    append("%")
                }

                batteryInfoPanel.tvBatteryCapacity.text = buildString{
                    append("${(batteryCapacity * it.level / 100)} ${getString(R.string.mah)}")
                    append(" of ")
                    append("$batteryCapacity ${getString(R.string.mah)}")
                }

                batteryInfoPanel.cvStatus.contentText = Utils.mapBatteryStatus(it.status, requireContext())

                batteryInfoPanel.cvVoltage.contentText = it.voltage.toString()

                batteryInfoPanel.cvChargingType.contentText = when {
                    it.acCharge -> getString(R.string.ac)
                    it.usbCharge -> getString(R.string.usb)
                    else -> getString(R.string.battery)
                }

                val powerValue = buildString {
                    append(String.format("%.1f", it.power))
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

                with(chargingViewModel) {
                    ChargingHistoryHolder.currentData.notifyDataChanged()
                    batteryHistoryPanel.chargingCurrentChart.notifyDataSetChanged()
                    batteryHistoryPanel.chargingCurrentChart.invalidate()

                    when {
                        currentGraph -> {
                            batteryHistoryPanel.tvChartValue.text = currentValue
                            batteryInfoPanel.cvChartInfo1.contentText = temperatureValue
                            batteryInfoPanel.cvChartInfo2.contentText = powerValue
                        }
                        temperatureGraph -> {
                            batteryHistoryPanel.tvChartValue.text = temperatureValue
                            batteryInfoPanel.cvChartInfo1.contentText = currentValue
                            batteryInfoPanel.cvChartInfo2.contentText = powerValue
                        }
                        powerGraph -> {
                            batteryHistoryPanel.tvChartValue.text = powerValue
                            batteryInfoPanel.cvChartInfo1.contentText = currentValue
                            batteryInfoPanel.cvChartInfo2.contentText = temperatureValue
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
                            setMinMaxText(batteryHistoryPanel.tvChartMin, currentMin)
                            setMinMaxText(batteryHistoryPanel.tvChartMax, currentMax)
                        }
                        temperatureGraph -> {
                            setMinMaxText(batteryHistoryPanel.tvChartMin, tempMin)
                            setMinMaxText(batteryHistoryPanel.tvChartMax, tempMax)
                        }
                        powerGraph -> {
                            setMinMaxText(batteryHistoryPanel.tvChartMin, powerMin)
                            setMinMaxText(batteryHistoryPanel.tvChartMax, powerMax)
                        }
                    }

                    chargingSummaryPanel.cvUptimeCharging.contentText = formatTime(it.uptime)
                }
            }

            if (getIsCharging()) {
                chargingViewModel.startBatteryMonitoring()
            } else {
                chargingViewModel.stopBatteryMonitoring()
                binding?.batteryHistoryPanel?.tvChartValue?.text = getString(com.juanarton.core.R.string.discharging)
                binding?.batteryHistoryPanel?.tvChartMax?.text = "-"
                binding?.batteryHistoryPanel?.tvChartMin?.text = "-"
            }
        }
    }

    private fun setUpLineChart(
        lineDataSet: LineDataSet, lineData: LineData, fillGradient: Drawable?, typedValue: TypedValue
    ) {
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

        binding?.batteryHistoryPanel?.chargingCurrentChart?.apply {
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

    private fun emitBubbles(typedValue: TypedValue) {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                if (isEmitting) {
                    val size = Random.nextInt(50, 100)
                    binding?.batteryInfoPanel?.bubbleEmitter?.emitBubble(size)
                    binding?.batteryInfoPanel?.bubbleEmitter?.setColors(
                        typedValue.data,
                        typedValue.data,
                        typedValue.data
                    )
                }
                delay(Random.nextLong(100, 500))
            }
        }
    }

    private fun updateChargingData() {
        binding?.apply {
            chargingSummaryPanel.cvChargedLevel.contentText = "${getChargedLevel()}%"
            chargingSummaryPanel.cvChargingPerHr.contentText = formatUsagePerHour(getChargingPerHr())
            chargingSummaryPanel.cvChargingDuration.contentText = formatTime(getChargingDuration())
        }
    }

    private fun showChart(lineDataSet: LineDataSet, lineData: LineData, gradientDrawable: Int) {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)

        setUpLineChart(
            lineDataSet, lineData, ContextCompat.getDrawable(requireContext(), gradientDrawable), typedValue
        )
    }

    private fun showCurrent() {
        showChart(
            ChargingHistoryHolder.currentLineDataSet,
            ChargingHistoryHolder.currentData,
            R.drawable.chart_gradient
        )
        currentGraph = true
        temperatureGraph = false
        powerGraph = false
    }

    private fun showTemperature() {
        showChart(
            ChargingHistoryHolder.temperatureLineDataSet,
            ChargingHistoryHolder.temperatureData,
            R.drawable.chart_gradient
        )
        currentGraph = false
        temperatureGraph = true
        powerGraph = false
    }

    private fun showPower() {
        showChart(
            ChargingHistoryHolder.powerLineDataSet,
            ChargingHistoryHolder.powerData,
            R.drawable.chart_gradient
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
                    append(String.format("%.1f", value / 100.0))
                    append(getString(R.string.wattage))
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        chargingViewModel.stopBatteryMonitoring()
    }

    override fun onStop() {
        super.onStop()
        chargingViewModel.stopBatteryMonitoring()
    }

    override fun onResume() {
        super.onResume()
        if (!firstRun) {
            chargingViewModel.startBatteryMonitoring()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}