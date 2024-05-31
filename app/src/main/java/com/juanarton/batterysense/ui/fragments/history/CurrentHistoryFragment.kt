package com.juanarton.batterysense.ui.fragments.history

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.FragmentCurrentHistoryBinding
import com.juanarton.batterysense.ui.fragments.dashboard.DashboardViewModel
import com.juanarton.batterysense.ui.fragments.history.HistoryUtil.changeChartColor
import com.juanarton.batterysense.ui.fragments.history.HistoryUtil.createStringValue
import com.juanarton.batterysense.ui.fragments.history.HistoryUtil.showHistoryChart
import com.juanarton.batterysense.utils.BatteryHistoryHolder
import com.juanarton.batterysense.utils.ChargingDataHolder.getIsCharging
import com.juanarton.batterysense.utils.ChargingHistoryHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class CurrentHistoryFragment : Fragment() {

    private var _binding: FragmentCurrentHistoryBinding? = null
    private val binding get() = _binding

    private var scheduledExecutorService: ScheduledExecutorService? = null
    private var isMonitoring = false

    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentHistoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCurrentMonitoring()

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

    private fun startCurrentMonitoring() {
        if (!isMonitoring) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
            scheduledExecutorService?.scheduleWithFixedDelay(
                {
                    lifecycleScope.launch {
                        binding?.apply {
                            if (getIsCharging()) {
                                ChargingHistoryHolder.currentData.notifyDataChanged()
                                currentHistoryChart.historyChart.notifyDataSetChanged()
                                currentHistoryChart.historyChart.invalidate()

                                val batteryCurrent = ChargingHistoryHolder.batteryCurrent

                                if (batteryCurrent.isNotEmpty()) {
                                    currentHistoryChart.tvChartValue.text =
                                        createStringValue(
                                            batteryCurrent[batteryCurrent.lastIndex].y.toInt().toString(),
                                            getString(R.string.ma),
                                            requireContext()
                                        )
                                }
                            } else {
                                BatteryHistoryHolder.currentData.notifyDataChanged()
                                currentHistoryChart.historyChart.notifyDataSetChanged()
                                currentHistoryChart.historyChart.invalidate()

                                val batteryCurrent = BatteryHistoryHolder.batteryCurrent

                                if (batteryCurrent.isNotEmpty()) {
                                    currentHistoryChart.tvChartValue.text =
                                        createStringValue(
                                            batteryCurrent[batteryCurrent.lastIndex].y.toInt().toString(),
                                            getString(R.string.ma),
                                            requireContext()
                                        )
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

    private fun stopCurrentMonitoring() {
        scheduledExecutorService?.shutdown()
        scheduledExecutorService = null
        isMonitoring = false
    }

    private fun showChargingChart() {
        showHistoryChart(
            ChargingHistoryHolder.currentLineDataSet,
            ChargingHistoryHolder.currentData,
            requireContext(),
            binding?.currentHistoryChart?.historyChart
        )
    }

    private fun showDischargingChart() {
        showHistoryChart(
            BatteryHistoryHolder.currentLineDataSet,
            BatteryHistoryHolder.currentData,
            requireContext(),
            binding?.currentHistoryChart?.historyChart
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
                changeChartColor(
                    currentHistoryChart.historyChart,
                    ChargingHistoryHolder.currentLineDataSet,
                    chargingColor
                )
                currentHistoryChart.tvChartValue.setTextColor(chargingColor)
            } else {
                changeChartColor(
                    currentHistoryChart.historyChart,
                    BatteryHistoryHolder.currentLineDataSet,
                    typedValue.data
                )
                currentHistoryChart.tvChartValue.setTextColor(typedValue1.data)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startCurrentMonitoring()
    }

    override fun onPause() {
        super.onPause()
        stopCurrentMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCurrentMonitoring()
        _binding = null
    }
}