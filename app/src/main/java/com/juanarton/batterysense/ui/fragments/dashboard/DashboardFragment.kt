package com.juanarton.batterysense.ui.fragments.dashboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.juanarton.batterysense.R
import com.juanarton.batterysense.broadcastreceiver.PowerStateReceiver
import com.juanarton.batterysense.databinding.FragmentNewDashboardBinding
import com.juanarton.batterysense.ui.fragments.history.HistoryUtil
import com.juanarton.batterysense.ui.fragments.history.adapter.HistoryAdapter
import com.juanarton.batterysense.utils.BatteryDataHolder.getLastChargeLevel
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOffTime
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOnTime
import com.juanarton.batterysense.utils.BatteryHistoryHolder
import com.juanarton.batterysense.utils.ChargingHistoryHolder
import com.juanarton.batterysense.utils.FragmentUtil
import com.juanarton.batterysense.utils.FragmentUtil.changeViewHeight
import com.juanarton.batterysense.utils.FragmentUtil.rescaleNumber
import com.juanarton.core.utils.Utils.formatTime
import com.juanarton.core.utils.Utils.mapBatteryStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.random.Random

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentNewDashboardBinding? = null
    private val binding get() =  _binding

    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    private val dashboardReceiver = DashboardReceiver()

    private var bubbleJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewDashboardBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val powerConnectedIntentFilter = IntentFilter()
        powerConnectedIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        powerConnectedIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        requireContext().registerReceiver(dashboardReceiver, powerConnectedIntentFilter)

        val batteryCapacity = dashboardViewModel.getCapacity()

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)

        val vpHistoryChart = binding?.batteryHistoryPanel?.vpHistoryChart
        val pagerAdapter = HistoryAdapter(requireActivity())
        vpHistoryChart?.adapter = pagerAdapter

        if (vpHistoryChart != null) {
            binding?.batteryHistoryPanel?.dotsIndicator?.attachTo(vpHistoryChart)
        }

        dashboardViewModel.batteryInfo.observe(viewLifecycleOwner) {
            binding?.apply {

                val decimalFormat = DecimalFormat("#.##")
                val power = decimalFormat.format(it.power)

                when (batteryHistoryPanel.vpHistoryChart.currentItem) {
                    0 -> batteryInfoPanel.tvBatteryInfo.text = buildString {
                        append("${it.temperature}${getString(R.string.degree_symbol)} · $power${getString(R.string.wattage)}")
                    }
                    1 -> batteryInfoPanel.tvBatteryInfo.text = buildString {
                        append("${abs(it.currentNow)}${getString(R.string.ma)} · $power${getString(R.string.wattage)}")
                    }
                    2 -> batteryInfoPanel.tvBatteryInfo.text = buildString {
                        append("${it.currentNow}${getString(R.string.ma)} · ${it.temperature}${getString(R.string.degree_symbol)}")
                    }
                }

                changeViewHeight(batteryInfoPanel.waveAnimation, rescaleNumber(it.level))
                changeViewHeight(batteryInfoPanel.bubbleEmitter, rescaleNumber(it.level))

                batteryInfoPanel.tvCapacity.text = buildString{
                    append("${(batteryCapacity * it.level / 100)}")
                    append(" / ")
                    append("$batteryCapacity ${getString(R.string.mah)}")
                }

                batteryInfoPanel.tvBatteryPercentage.text = buildString {append("${it.level}%")}
                batteryInfoPanel.tvChargingStatus.text = mapBatteryStatus(it.status, requireContext())

                batteryInfoPanel.tvScreenOnValue.text = formatTime(getScreenOnTime())
                batteryInfoPanel.tvScreenOffValue.text = formatTime(getScreenOffTime())
                batteryInfoPanel.tvBatteryUsedValue.text = buildString { append("${getLastChargeLevel() - it.level}%") }

                if (it.status == 1 || it.status == 3 || it.status == 4) {
                    batteryInfoPanel.tvChargingType.visibility = View.GONE
                    stopEmittingBubbles()
                } else {
                    batteryInfoPanel.tvChargingType.visibility = View.VISIBLE
                    batteryInfoPanel.tvChargingType.text = when {
                        it.acCharge -> getString(R.string.ac)
                        it.usbCharge -> getString(R.string.usb)
                        else -> getString(R.string.battery)
                    }
                    startEmittingBubbles(typedValue)
                }
            }
        }
    }

    private fun startEmittingBubbles(typedValue: TypedValue) {
        bubbleJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                if (FragmentUtil.isEmitting) {
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

    private fun stopEmittingBubbles() {
        bubbleJob?.cancel()
        bubbleJob = null
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        requireContext().unregisterReceiver(dashboardReceiver)
    }

    inner class DashboardReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                Intent.ACTION_POWER_DISCONNECTED -> {
                    dashboardViewModel._powerStateEvent.value = false
                }
                Intent.ACTION_POWER_CONNECTED -> {
                    dashboardViewModel._powerStateEvent.value = true
                }
            }
        }
    }
}