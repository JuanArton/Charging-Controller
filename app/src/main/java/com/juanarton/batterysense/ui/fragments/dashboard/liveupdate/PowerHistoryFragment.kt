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
import com.juanarton.batterysense.databinding.FragmentPowerHistoryBinding
import com.juanarton.batterysense.ui.fragments.dashboard.DashboardViewModel
import com.juanarton.batterysense.ui.fragments.history.util.HistoryUtil
import com.juanarton.batterysense.utils.BatteryHistoryHolder
import com.juanarton.batterysense.utils.ChargingDataHolder.getIsCharging
import com.juanarton.batterysense.utils.ChargingHistoryHolder
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class PowerHistoryFragment : Fragment() {

    private var _binding: FragmentPowerHistoryBinding? = null
    private val binding get() = _binding

    private var scheduledExecutorService: ScheduledExecutorService? = null
    private var isMonitoring = false

    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPowerHistoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startPowerMonitoring()

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

    private fun startPowerMonitoring() {
        if (!isMonitoring) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
            scheduledExecutorService?.scheduleWithFixedDelay(
                {
                    lifecycleScope.launch {
                        binding?.apply {
                            if (getIsCharging()) {
                                ChargingHistoryHolder.powerData.notifyDataChanged()
                                powerHistoryChart.historyChart.notifyDataSetChanged()
                                powerHistoryChart.historyChart.invalidate()

                                val batteryPower = ChargingHistoryHolder.batteryPower

                                if (batteryPower.isNotEmpty()) {
                                    powerHistoryChart.tvChartValue.text =
                                        HistoryUtil.createStringValue(
                                            String.format(
                                                Locale.getDefault(),
                                                "%.1f",
                                                batteryPower[batteryPower.lastIndex].y
                                            ),
                                            getString(R.string.wattage),
                                            requireContext()
                                        )

                                    powerHistoryChart.tvMinValue.text = buildString {
                                        append("${batteryPower.minByOrNull { it.y }?.y?.toInt()} ${getString(R.string.wattage)}")
                                    }
                                    powerHistoryChart.tvMaxValue.text = buildString {
                                        append("${batteryPower.maxByOrNull { it.y }?.y?.toInt()} ${getString(R.string.wattage)}")
                                    }
                                }
                            } else {
                                BatteryHistoryHolder.powerData.notifyDataChanged()
                                powerHistoryChart.historyChart.notifyDataSetChanged()
                                powerHistoryChart.historyChart.invalidate()

                                val batteryPower = BatteryHistoryHolder.batteryPower

                                if (batteryPower.isNotEmpty()) {
                                    powerHistoryChart.tvChartValue.text =
                                        HistoryUtil.createStringValue(
                                            String.format(
                                                Locale.getDefault(),
                                                "%.1f",
                                                batteryPower[batteryPower.lastIndex].y
                                            ),
                                            getString(R.string.wattage),
                                            requireContext()
                                        )

                                    powerHistoryChart.tvMinValue.text = buildString {
                                        append("${batteryPower.minByOrNull { it.y }?.y?.toInt()} ${getString(R.string.wattage)}")
                                    }
                                    powerHistoryChart.tvMaxValue.text = buildString {
                                        append("${batteryPower.maxByOrNull { it.y }?.y?.toInt()} ${getString(R.string.wattage)}")
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

    private fun stopPowerMonitoring() {
        scheduledExecutorService?.shutdown()
        scheduledExecutorService = null
        isMonitoring = false
    }

    private fun showChargingChart() {
        HistoryUtil.showHistoryChart(
            ChargingHistoryHolder.powerLineDataSet,
            ChargingHistoryHolder.powerData,
            requireContext(),
            binding?.powerHistoryChart?.historyChart
        )
    }

    private fun showDischargingChart() {
        HistoryUtil.showHistoryChart(
            BatteryHistoryHolder.powerLineDataSet,
            BatteryHistoryHolder.powerData,
            requireContext(),
            binding?.powerHistoryChart?.historyChart
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
                    powerHistoryChart.historyChart,
                    ChargingHistoryHolder.powerLineDataSet,
                    chargingColor
                )
                powerHistoryChart.tvChartValue.setTextColor(chargingColor)
                powerHistoryChart.tvMinValue.setTextColor(chargingColor)
                powerHistoryChart.tvMaxValue.setTextColor(chargingColor)
            } else {
                HistoryUtil.changeChartColor(
                    powerHistoryChart.historyChart,
                    BatteryHistoryHolder.powerLineDataSet,
                    typedValue.data
                )
                powerHistoryChart.tvChartValue.setTextColor(typedValue1.data)
                powerHistoryChart.tvMinValue.setTextColor(typedValue1.data)
                powerHistoryChart.tvMaxValue.setTextColor(typedValue1.data)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startPowerMonitoring()
    }

    override fun onPause() {
        super.onPause()
        stopPowerMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPowerMonitoring()
        _binding = null
    }
}