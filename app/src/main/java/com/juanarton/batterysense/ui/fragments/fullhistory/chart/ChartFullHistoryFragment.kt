package com.juanarton.batterysense.ui.fragments.fullhistory.chart

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.FragmentChartFullHistoryBinding
import com.juanarton.batterysense.ui.fragments.history.util.HistoryUtil.createGradient
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class ChartFullHistoryFragment : Fragment() {

    lateinit var day: String

    private var _binding: FragmentChartFullHistoryBinding? = null
    private val binding get() = _binding

    private val chartFullHistoryViewModel: ChartFullHistoryViewModel by viewModels()

    private var listBatteryHistory: ArrayList<BatteryHistory> = arrayListOf()
    private var filteredHistoryList: ArrayList<BatteryHistory> = arrayListOf()

    companion object {
        private const val ARG_DAY = "day"

        fun newInstance(day: String) = ChartFullHistoryFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_DAY, day)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChartFullHistoryBinding.inflate(layoutInflater, container, false)
        return  binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            day = it.getString(ARG_DAY) ?: ""

            chartFullHistoryViewModel.getBatteryHistoryByDay(day)
            chartFullHistoryViewModel.batteryHistoryByDay.observe(viewLifecycleOwner) { list ->
                listBatteryHistory.addAll(list)
                binding?.apply {
                    tvOngoing.text = if (list.last().isCharging) {
                        buildString {
                            append(getString(R.string.ongoing))
                            append(" ${getString(R.string.charging)}")
                        }
                    } else {
                        buildString {
                            append(getString(R.string.ongoing))
                            append(" ${getString(com.juanarton.core.R.string.discharging)}")
                        }
                    }
                    val color = ContextCompat.getColor(requireContext(), R.color.yellow)
                    tvOngoing.setTextColor(color)

                    setupLineChart(batteryHistoryChart, list, 0, 24)
                    tvDate.text = formatDate(day)
                }
            }
        }
    }

    private fun setupLineChart(
        lineChart: LineChart, batteryHistoryList: List<BatteryHistory>, minHour: Int, maxHour: Int
    ) {
        val lineDataSets = mutableListOf<ILineDataSet>()

        if (batteryHistoryList.isEmpty()) return

        var tempEntries = mutableListOf<Entry>()
        var currentState = batteryHistoryList.first().isCharging

        for (data in batteryHistoryList) {
            val hourFloat = convertMillisToSecondOfDay(data.timestamp)
            val entry = Entry(hourFloat, data.level.toFloat())

            if (data.isCharging == currentState) {
                tempEntries.add(entry)
            } else {
                if (tempEntries.isNotEmpty()) {
                    lineDataSets.add(createLineDataSet(tempEntries, currentState))
                }
                tempEntries = mutableListOf(entry)
                currentState = data.isCharging
            }
        }

        if (tempEntries.isNotEmpty()) {
            lineDataSets.add(createLineDataSet(tempEntries, currentState))
        }

        val lineData = LineData(lineDataSets)
        lineChart.data = lineData

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)

        lineChart.apply {
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            animateX(1000)
            setScaleEnabled(false)
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

    fun updateData(minHour: Int, maxHour: Int) {
        filteredHistoryList.clear()
        filteredHistoryList.addAll(filterHistory(listBatteryHistory, minHour, maxHour))

        binding?.apply {
            batteryHistoryChart.clear()
            setupLineChart(batteryHistoryChart, filteredHistoryList, minHour, maxHour)
        }
    }

    fun getHistoryList(): List<BatteryHistory> {
        return listBatteryHistory
    }

    private fun filterHistory(
        historyList: List<BatteryHistory>, minHour: Int, maxHour: Int
    ): List<BatteryHistory> {
        return historyList.filter { history ->
            val calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
                timeInMillis = history.timestamp
            }
            val timestamp = calendar.get(Calendar.HOUR_OF_DAY)
            timestamp in minHour..maxHour
        }
    }

    private fun createLineDataSet(entries: List<Entry>, isCharging: Boolean): LineDataSet {
        binding?.cpiChart?.visibility = View.GONE
        binding?.batteryHistoryChart?.visibility = View.VISIBLE
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
        val onGoing = entries.last().x == convertMillisToSecondOfDay(listBatteryHistory.last().timestamp)

        val newColor = when {
            onGoing -> ContextCompat.getColor(requireContext(), R.color.yellow)
            isCharging -> ContextCompat.getColor(requireContext(), R.color.green)
            else -> ContextCompat.getColor(requireContext(), R.color.red)
        }
        
        val fillGradient = createGradient(newColor)

        val lineDataSet =  LineDataSet(entries, if (isCharging) "Charging" else "Discharging").apply {
            color = newColor
            isHighlightEnabled = false
            lineWidth = 2.0F
            fillDrawable = fillGradient
            setDrawValues(false)
            setDrawFilled(true)
            setDrawCircles(false)
            valueTextColor = typedValue.data
        }
        return lineDataSet
    }

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

        val date = inputFormat.parse(inputDate) ?: return inputDate
        return outputFormat.format(date)
    }
}