package com.juanarton.batterysense.ui.fragments.dashboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.FragmentDashboardBinding
import com.juanarton.batterysense.ui.fragments.dashboard.adapter.HistoryAdapter
import com.juanarton.batterysense.utils.BatteryDataHolder.getAwakeTime
import com.juanarton.batterysense.utils.BatteryDataHolder.getDeepSleepTime
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOffDrain
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOffDrainPerHr
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOffTime
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOnDrain
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOnDrainPerHr
import com.juanarton.batterysense.utils.BatteryDataHolder.getScreenOnTime
import com.juanarton.batterysense.utils.ChargingDataHolder.getChargedLevel
import com.juanarton.batterysense.utils.ChargingDataHolder.getChargingDuration
import com.juanarton.batterysense.utils.ChargingDataHolder.getChargingPerHr
import com.juanarton.batterysense.utils.FragmentUtil
import com.juanarton.batterysense.utils.FragmentUtil.changeViewHeight
import com.juanarton.batterysense.utils.FragmentUtil.rescaleNumber
import com.juanarton.batterysense.utils.Utils.calculateCpuAwakePercentage
import com.juanarton.batterysense.utils.Utils.calculateDeepSleepAwakeSpeed
import com.juanarton.batterysense.utils.Utils.calculateDeepSleepPercentage
import com.juanarton.batterysense.utils.Utils.formatDeepSleepAwake
import com.juanarton.batterysense.utils.Utils.formatUsagePerHour
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

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() =  _binding

    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    private val dashboardReceiver = DashboardReceiver()

    private var bubbleJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
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

        val handler = Handler(Looper.getMainLooper())

        val runnable = Runnable {
            if (vpHistoryChart != null) {
                binding?.batteryHistoryPanel?.historyDotsIndicator?.attachTo(vpHistoryChart)
            }
        }
        val delayMillis: Long = 1000
        handler.postDelayed(runnable, delayMillis)

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

                batteryStatisticPanel.tvScreenOnValueUsage.text = buildString {
                    append("${getScreenOnDrain()}%")
                }
                batteryStatisticPanel.tvScreenOffValueUsage.text = buildString {
                    append("${getScreenOffDrain()}%")
                }

                val screenOffDrainPerHrTmp = if (getScreenOffDrainPerHr().isNaN()) 0.0 else getScreenOffDrainPerHr()
                val screenOnDrainPerHrTmp = if (getScreenOnDrainPerHr().isNaN()) 0.0 else getScreenOnDrainPerHr()
                val deepSleepPercentage =
                    calculateDeepSleepPercentage(
                        getDeepSleepTime().toDouble(),
                        getScreenOffTime().toDouble()
                    )
                val cpuAwakePercentage =
                    calculateCpuAwakePercentage(
                        deepSleepPercentage
                    )

                batteryExtraPanel.tvAwakeValue.text = formatDeepSleepAwake(cpuAwakePercentage)
                batteryExtraPanel.tvSleepValue.text = formatDeepSleepAwake(deepSleepPercentage)

                batteryExtraPanel.tvAwakeDuration.text = formatTime(getAwakeTime())
                batteryExtraPanel.tvSleepDuration.text = formatTime(getDeepSleepTime())

                batteryExtraPanel.tvAwakeValueSpeed.text = formatUsagePerHour(
                    calculateDeepSleepAwakeSpeed(cpuAwakePercentage, screenOffDrainPerHrTmp)
                )
                batteryExtraPanel.tvSleepValueSpeed.text = formatUsagePerHour(
                    calculateDeepSleepAwakeSpeed(deepSleepPercentage, screenOffDrainPerHrTmp)
                )

                if (it.status == 1 || it.status == 3 || it.status == 4) {
                    batteryInfoPanel.tvChargingType.visibility = View.GONE
                    stopEmittingBubbles()

                    setBatteryAnimationColor(typedValue.data)

                    batteryInfoPanel.tvBatteryUsedValue.text = buildString {
                        append("${dashboardViewModel.lastUnplugged().second - it.level}%")
                    }

                    setActiveIdleVisibility(View.VISIBLE)
                    setChargingSpeedVisibility(View.GONE)

                    batteryStatisticPanel.tvActiveValue.text = formatUsagePerHour(screenOnDrainPerHrTmp)
                    batteryStatisticPanel.tvIdleValue.text = formatUsagePerHour(screenOffDrainPerHrTmp)
                } else {
                    batteryInfoPanel.tvChargingType.visibility = View.VISIBLE
                    batteryInfoPanel.tvChargingType.text = when {
                        it.acCharge -> getString(R.string.ac)
                        it.usbCharge -> getString(R.string.usb)
                        else -> getString(R.string.battery)
                    }

                    setBatteryAnimationColor(getColor(requireContext(), R.color.green))

                    batteryInfoPanel.tvBatteryUsedValue.text = buildString {
                        append("${getChargedLevel()}%")
                    }

                    setActiveIdleVisibility(View.GONE)
                    setChargingSpeedVisibility(View.VISIBLE)

                    batteryStatisticPanel.tvChargingValue.text = formatUsagePerHour(getChargingPerHr())
                    batteryStatisticPanel.tvChargingDurationValue.text = formatTime(getChargingDuration())
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

    private fun setBatteryAnimationColor(color: Int) {
        binding?.batteryInfoPanel?.waveAnimation?.closeColor = color
        binding?.batteryInfoPanel?.waveAnimation?.startColor = color

        binding?.batteryInfoPanel?.bubbleEmitter?.setColors(color)
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