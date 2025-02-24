package com.juanarton.batterysense.ui.activity.detailHistory

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.ActivityDetailHistoryBinding
import com.juanarton.batterysense.ui.fragments.history.util.HistoryUtil.createGradient
import com.juanarton.batterysense.utils.BatteryDataHolder.getAwakeTime
import com.juanarton.batterysense.utils.BatteryDataHolder.getDeepSleepTime
import com.juanarton.batterysense.utils.Utils.animateHeight
import com.juanarton.batterysense.utils.Utils.calculateDeepSleepAwakeSpeed
import com.juanarton.batterysense.utils.Utils.formatDeepSleepAwake
import com.juanarton.batterysense.utils.Utils.formatUsagePerHour
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import com.juanarton.core.utils.Utils.convertMillisToDateTimeSecond
import com.juanarton.core.utils.Utils.formatTime
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs

@AndroidEntryPoint
class DetailHistoryActivity : AppCompatActivity() {

    private var _binding: ActivityDetailHistoryBinding? = null
    private val binding get() = _binding
    private var isHide = true
    var originalHeight = 0

    private val detailHistoryViewModel: DetailHistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val chargingHistory = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("HISTORY", ChargingHistory::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("HISTORY")
        }

        binding?.selectedHistoryPanel?.root?.post {
            originalHeight = binding?.selectedHistoryPanel?.root?.height!!
            binding?.selectedHistoryPanel?.root?.visibility = View.GONE
        }

        val day = intent.getStringExtra("DAY")

        if (chargingHistory != null && day != null) {
            detailHistoryViewModel.getBatteryHistoryByDay(day)
            detailHistoryViewModel.batteryHistoryByDay.observe(this) { list ->
                val minHour = getHourFromEpochCompat(chargingHistory.startTime)
                val maxTmp = getHourFromEpochCompat(chargingHistory.endTime)
                val maxHour = if (maxTmp < 24){
                    maxTmp + 1
                } else {
                    maxTmp
                }

                val filtered = filterHistory(list, chargingHistory.startTime, chargingHistory.endTime)
                val entries = mutableListOf<Entry>()
                filtered.forEach {
                    val hourFloat = convertMillisToSecondOfDay(it.timestamp)
                    entries.add(Entry(hourFloat, it.level.toFloat()))
                }

                binding?.apply {
                    setupLineChart(batteryHistoryChart, entries, minHour, maxHour, chargingHistory.isCharging)
                    batteryHistoryChart.setOnChartValueSelectedListener(object :
                        OnChartValueSelectedListener {
                        override fun onValueSelected(e: Entry?, h: Highlight?) {
                            e?.let {
                                val index = list.indexOfFirst { history ->
                                    convertMillisToSecondOfDay(history.timestamp) == e.x
                                }
                                val selected = list[index]

                                selectedHistoryPanel.root.visibility = View.VISIBLE

                                if (isHide) animateHeight(selectedHistoryPanel.root, 0, originalHeight)
                                isHide = false
                                selectedHistoryPanel.apply {
                                    tvHistoryDate.text = convertMillisToDateTimeSecond(selected.timestamp)

                                    tvBatteryLevel.text = buildString {
                                        append(selected.level)
                                        append("%")
                                    }

                                    tvBatteryCurrent.text = buildString {
                                        append(selected.current)
                                        append(this@DetailHistoryActivity.getString(R.string.ma))
                                    }

                                    tvBatteryTemperature.text = buildString {
                                        append(selected.temperature)
                                        append(this@DetailHistoryActivity.getString(R.string.degree_symbol))
                                    }

                                    val decimalFormat = DecimalFormat("#.##")
                                    val power = decimalFormat.format(selected.power)
                                    tvBatteryPower.text = buildString {
                                        append(power)
                                        append(this@DetailHistoryActivity.getString(R.string.wattage))
                                    }

                                    tvBatteryVolt.text = buildString {
                                        append(selected.voltage)
                                        append(this@DetailHistoryActivity.getString(R.string.volt_unit))
                                    }
                                }
                            }
                        }

                        override fun onNothingSelected() {}
                    })

                    batteryExtraPanel.tvAwakeValue.text = chargingHistory.awakePercentage
                        ?.let { formatDeepSleepAwake(it) } ?: " - "
                    batteryExtraPanel.tvSleepValue.text = chargingHistory.deepSleepPercentage
                        ?.let { formatDeepSleepAwake(it) } ?: " - "

                    batteryExtraPanel.tvAwakeDuration.text = chargingHistory.awakeDuration
                        ?.let { formatTime(getAwakeTime()) } ?: " - "
                    batteryExtraPanel.tvSleepDuration.text = chargingHistory.sleepDuration
                        ?. let { formatTime(getDeepSleepTime()) } ?: " - "

                    batteryExtraPanel.tvAwakeValueSpeed.text = chargingHistory.awakePercentage
                        ?. let {
                            formatUsagePerHour(
                                calculateDeepSleepAwakeSpeed(
                                    chargingHistory.awakePercentage!!, chargingHistory.screenOffDrainPerHr!!
                                )
                            )
                        } ?: " - "
                    batteryExtraPanel.tvSleepValueSpeed.text = chargingHistory.awakePercentage
                        ?. let {
                            formatUsagePerHour(
                                calculateDeepSleepAwakeSpeed(
                                    chargingHistory.deepSleepPercentage!!, chargingHistory.screenOffDrainPerHr!!
                                )
                            )
                        } ?: " - "

                    if (!chargingHistory.isCharging) {
                        batteryStatisticPanel.tvScreenOnValueUsage.text = buildString {
                            append("${chargingHistory.screenOnDrain}%")
                        }
                        batteryStatisticPanel.tvScreenOffValueUsage.text = buildString {
                            append("${chargingHistory.screenOffDrain}%")
                        }

                        setActiveIdleVisibility(View.VISIBLE)
                        setChargingSpeedVisibility(View.GONE)

                        batteryStatisticPanel.tvActiveValue.text = chargingHistory.screenOnDrainPerHr
                            ?.let { formatUsagePerHour(it) } ?: " - "
                        batteryStatisticPanel.tvIdleValue.text = chargingHistory.screenOffDrainPerHr
                            ?.let { formatUsagePerHour(it) } ?: " - "
                    } else {
                        batteryStatisticPanel.tvScreenOnValueUsage.text = buildString {
                            append(" - ")
                        }
                        batteryStatisticPanel.tvScreenOffValueUsage.text = buildString {
                            append(" - ")
                        }

                        setActiveIdleVisibility(View.GONE)
                        setChargingSpeedVisibility(View.VISIBLE)

                        batteryStatisticPanel.tvChargingValue.text = chargingHistory.chargingSpeed
                            ?.let { formatUsagePerHour(it) } ?: " - "
                        batteryStatisticPanel.tvChargingDurationValue.text = formatTime(
                            (abs(chargingHistory.startTime - chargingHistory.endTime)) / 1000
                        )
                    }
                }
            }
        }
    }

    private fun filterHistory(
        historyList: List<BatteryHistory>, minHour: Long, maxHour: Long
    ): List<BatteryHistory> {
        return historyList.filter { history ->
            history.timestamp in minHour..maxHour
        }
    }

    private fun setupLineChart(
        lineChart: LineChart, entries: List<Entry>, minHour: Int, maxHour: Int, isCharging: Boolean
    ) {
        val lineDataSets = createLineDataSet(entries, isCharging)

        val lineData = LineData(lineDataSets)
        lineChart.data = lineData

        val typedValue = TypedValue()
        this.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)

        lineChart.apply {
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            animateX(1000)
        }

        lineChart.axisLeft.apply {
            axisMinimum = 0f
            axisMaximum = 100f
            textColor  = typedValue.data
            gridColor = typedValue.data
            setLabelCount(6, true)
        }

        lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return convertSecondToTime(value)
                }
            }

            axisMinimum = (minHour * 3600).toFloat()
            axisMaximum = (maxHour * 3600).toFloat()
            gridColor = typedValue.data
            setLabelCount(7, true)
            textColor = typedValue.data
        }

        lineChart.invalidate()
    }

    private fun convertMillisToSecondOfDay(millis: Long): Float {
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        return (hour * 3600 + minute * 60 + second).toFloat()
    }

    @SuppressLint("DefaultLocale")
    private fun convertSecondToTime(value: Float): String {
        val totalSeconds = value.toInt()
        val hour = totalSeconds / 3600
        val minute = (totalSeconds % 3600) / 60
        return String.format("%02d:%02d", hour, minute)
    }

    private fun createLineDataSet(entries: List<Entry>, isCharging: Boolean): LineDataSet {
        binding?.batteryHistoryChart?.visibility = View.VISIBLE
        val typedValue = TypedValue()
        this.theme.resolveAttribute(android.R.attr.textColor, typedValue, true)

        val newColor = when {
            isCharging -> ContextCompat.getColor(this, R.color.green)
            else -> ContextCompat.getColor(this, R.color.red)
        }

        val fillGradient = createGradient(newColor)

        val lineDataSet =  LineDataSet(entries, if (isCharging) "Charging" else "Discharging").apply {
            color = newColor
            mode = LineDataSet.Mode.LINEAR
            lineWidth = 1.0F
            fillDrawable = fillGradient
            setDrawValues(false)
            setDrawFilled(true)
            setDrawCircles(false)
            valueTextColor = typedValue.data
        }
        return lineDataSet
    }

    private fun getHourFromEpochCompat(epochMillis: Long): Int {
        val timeZone = TimeZone.getDefault()
        val calendar = Calendar.getInstance(timeZone).apply { timeInMillis = epochMillis }
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    private fun setActiveIdleVisibility(state: Int) {
        binding?.batteryStatisticPanel?.apply {
            ivActive.visibility = state
            ivIdle.visibility = state
            linearLayout4.visibility = state
            linearLayout5.visibility = state
        }
    }

    private fun setChargingSpeedVisibility(state: Int) {
        binding?.batteryStatisticPanel?.apply {
            ivCharging.visibility = state
            linearLayout6.visibility = state
            linearLayout7.visibility = state
        }
    }
}