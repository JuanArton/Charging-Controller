package com.juanarton.batterysense.ui.fragments.history

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.FragmentCurrentHistoryBinding
import com.juanarton.batterysense.ui.fragments.history.HistoryUtil.createStringValue
import com.juanarton.batterysense.ui.fragments.history.HistoryUtil.showHistoryChart
import com.juanarton.batterysense.utils.BatteryHistoryHolder
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class CurrentHistoryFragment : Fragment() {

    private var _binding: FragmentCurrentHistoryBinding? = null
    private val binding get() = _binding

    private var scheduledExecutorService: ScheduledExecutorService? = null
    private var isMonitoring = false

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

        val currentChart = binding?.currentHistoryChart?.historyChart
        showHistoryChart(
            BatteryHistoryHolder.currentLineDataSet,
            BatteryHistoryHolder.currentData,
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
                            BatteryHistoryHolder.currentData.notifyDataChanged()
                            currentHistoryChart.historyChart.notifyDataSetChanged()
                            currentHistoryChart.historyChart.invalidate()

                            val batteryCurrent = BatteryHistoryHolder.batteryCurrent

                            currentHistoryChart.tvChartValue.text =
                                createStringValue(
                                    batteryCurrent[batteryCurrent.lastIndex].y.toInt().toString(),
                                    getString(R.string.ma),
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