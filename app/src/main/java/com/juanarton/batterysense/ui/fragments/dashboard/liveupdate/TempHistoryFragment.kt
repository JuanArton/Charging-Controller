package com.juanarton.batterysense.ui.fragments.dashboard.liveupdate

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.FragmentTempHistoryBinding
import com.juanarton.batterysense.ui.fragments.dashboard.DashboardViewModel
import com.juanarton.batterysense.ui.fragments.history.util.HistoryUtil
import com.juanarton.batterysense.utils.BatteryHistoryHolder
import com.juanarton.batterysense.utils.ChargingDataHolder.getIsCharging
import com.juanarton.batterysense.utils.ChargingHistoryHolder
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class TempHistoryFragment : Fragment() {

    private var _binding: FragmentTempHistoryBinding? = null
    private val binding get() = _binding

    private var scheduledExecutorService: ScheduledExecutorService? = null
    private var isMonitoring = false

    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTempHistoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startTempMonitoring()

        if (getIsCharging()) {
            showChargingChart()
        } else {
            showDischargingChart()
        }

        setChartColor()

        dashboardViewModel.powerStateEvent.observe(viewLifecycleOwner) {
            binding?.apply {
                if (it) {
                    showChargingChart()
                } else {
                    showDischargingChart()
                }
                setChartColor()
            }
        }
    }

    private fun startTempMonitoring() {
        if (!isMonitoring) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
            scheduledExecutorService?.scheduleWithFixedDelay(
                {
                    lifecycleScope.launch {
                        binding?.apply {
                            if (getIsCharging()) {
                                ChargingHistoryHolder.temperatureData.notifyDataChanged()
                                tempHistoryChart.historyChart.notifyDataSetChanged()
                                tempHistoryChart.historyChart.invalidate()

                                val batteryTemperature = ChargingHistoryHolder.batteryTemperature

                                if (batteryTemperature.isNotEmpty()) {
                                    tempHistoryChart.tvChartValue.text =
                                        HistoryUtil.createStringValue(
                                            batteryTemperature[batteryTemperature.lastIndex].y.toInt()
                                                .toString(),
                                            getString(R.string.degree_symbol),
                                            requireContext()
                                        )

                                    tempHistoryChart.tvMinValue.text = buildString {
                                        append("${batteryTemperature.minByOrNull { it.y }?.y?.toInt()} ${getString(R.string.degree_symbol)}")
                                    }
                                    tempHistoryChart.tvMaxValue.text = buildString {
                                        append("${batteryTemperature.maxByOrNull { it.y }?.y?.toInt()} ${getString(R.string.degree_symbol)}")
                                    }
                                }
                            } else {
                                BatteryHistoryHolder.temperatureData.notifyDataChanged()
                                tempHistoryChart.historyChart.notifyDataSetChanged()
                                tempHistoryChart.historyChart.invalidate()

                                val batteryTemperature = BatteryHistoryHolder.batteryTemperature

                                if (batteryTemperature.isNotEmpty()) {
                                    tempHistoryChart.tvChartValue.text =
                                        HistoryUtil.createStringValue(
                                            batteryTemperature[batteryTemperature.lastIndex].y.toInt()
                                                .toString(),
                                            getString(R.string.degree_symbol),
                                            requireContext()
                                        )

                                    tempHistoryChart.tvMinValue.text = buildString {
                                        append("${batteryTemperature.minByOrNull { it.y }?.y?.toInt()} ${getString(R.string.degree_symbol)}")
                                    }
                                    tempHistoryChart.tvMaxValue.text = buildString {
                                        append("${batteryTemperature.maxByOrNull { it.y }?.y?.toInt()} ${getString(R.string.degree_symbol)}")
                                    }
                                }
                            }
                        }
                    }
                },
                0, 1, TimeUnit.SECONDS
            )
            isMonitoring = true
        }
    }

    private fun showChargingChart() {
        HistoryUtil.showHistoryChart(
            ChargingHistoryHolder.temperatureLineDataSet,
            ChargingHistoryHolder.temperatureData,
            requireContext(),
            binding?.tempHistoryChart?.historyChart
        )
    }

    private fun showDischargingChart() {
        HistoryUtil.showHistoryChart(
            BatteryHistoryHolder.temperatureLineDataSet,
            BatteryHistoryHolder.temperatureData,
            requireContext(),
            binding?.tempHistoryChart?.historyChart
        )
    }

    private fun setChartColor() {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)

        val typedValue1 = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.textColor, typedValue1, true)

        val chargingColor = requireActivity().getColor(R.color.green)

        binding?.apply {
            if (getIsCharging()) {
                HistoryUtil.changeChartColor(
                    tempHistoryChart.historyChart,
                    ChargingHistoryHolder.temperatureLineDataSet,
                    chargingColor
                )
                tempHistoryChart.tvChartValue.setTextColor(chargingColor)
                tempHistoryChart.tvMinValue.setTextColor(chargingColor)
                tempHistoryChart.tvMaxValue.setTextColor(chargingColor)
            } else {
                HistoryUtil.changeChartColor(
                    tempHistoryChart.historyChart,
                    BatteryHistoryHolder.temperatureLineDataSet,
                    typedValue.data
                )
                tempHistoryChart.tvChartValue.setTextColor(typedValue1.data)
                tempHistoryChart.tvMinValue.setTextColor(typedValue1.data)
                tempHistoryChart.tvMaxValue.setTextColor(typedValue1.data)
            }
        }
    }

    private fun stopTempMonitoring() {
        scheduledExecutorService?.shutdown()
        scheduledExecutorService = null
        isMonitoring = false
    }

    override fun onResume() {
        super.onResume()
        startTempMonitoring()
    }

    override fun onPause() {
        super.onPause()
        stopTempMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTempMonitoring()
        _binding = null
    }
}