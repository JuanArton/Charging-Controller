package com.juanarton.batterysense.ui.fragments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.FragmentTempHistoryBinding
import com.juanarton.batterysense.utils.BatteryHistoryHolder
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class TempHistoryFragment : Fragment() {

    private var _binding: FragmentTempHistoryBinding? = null
    private val binding get() = _binding

    private var scheduledExecutorService: ScheduledExecutorService? = null
    private var isMonitoring = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTempHistoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCurrentMonitoring()

        val currentChart = binding?.tempHistoryChart?.historyChart
        HistoryUtil.showHistoryChart(
            BatteryHistoryHolder.temperatureLineDataSet,
            BatteryHistoryHolder.temperatureData,
            R.drawable.chart_gradient,
            requireContext(),
            currentChart
        )
    }

    private fun startCurrentMonitoring() {
        if (!isMonitoring) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
            scheduledExecutorService?.scheduleWithFixedDelay(
                {
                    lifecycleScope.launch {
                        binding?.apply {
                            BatteryHistoryHolder.temperatureData.notifyDataChanged()
                            tempHistoryChart.historyChart.notifyDataSetChanged()
                            tempHistoryChart.historyChart.invalidate()

                            val batteryTemperature = BatteryHistoryHolder.batteryTemperature
                            tempHistoryChart.tvChartValue.text =
                                HistoryUtil.createStringValue(
                                    batteryTemperature[batteryTemperature.lastIndex].y.toInt().toString(),
                                    getString(R.string.degree_symbol),
                                    requireContext()
                                )
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