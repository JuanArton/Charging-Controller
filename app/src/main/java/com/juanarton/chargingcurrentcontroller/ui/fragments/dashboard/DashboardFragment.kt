package com.juanarton.chargingcurrentcontroller.ui.fragments.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.juanarton.chargingcurrentcontroller.R
import com.juanarton.chargingcurrentcontroller.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs


@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding

    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("PrivateApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var batteryCapacity = 0.0
        val powerProfileClass = "com.android.internal.os.PowerProfile"

        try {
            val powerProfile = Class.forName(powerProfileClass)
                .getConstructor(Context::class.java)
                .newInstance(requireContext())
            batteryCapacity = Class
                .forName(powerProfileClass)
                .getMethod("getBatteryCapacity")
                .invoke(powerProfile) as Double
        } catch (e: Exception) {
            Log.d("Get Battery Capacity", e.toString())
        }

        setUpLineChart()

        dashboardViewModel.batteryInfo.observe(viewLifecycleOwner) {

            binding?.apply {
                arcBatteryPercentage.progress = it.level
                arcBatteryPercentage.bottomText = buildString {
                    append((batteryCapacity * it.level/100).toInt())
                    append(" ")
                    append(getString(R.string.mah))
                }

                tvBatteryStatus.text = when (it.status){
                    1 -> getString(R.string.unknown)
                    2 -> getString(R.string.charging)
                    3 -> getString(R.string.discharging)
                    4 -> getString(R.string.not_charging)
                    else -> getString(R.string.full)
                }

                tvBatteryVoltage.text = it.voltage.toString()

                tvChargingType.text = when {
                    it.acCharge -> getString(R.string.ac)
                    it.usbCharge -> getString(R.string.usb)
                    else -> getString(R.string.battery)
                }

                tvBatteryTemperature.text = it.temperature.toString()

                tvBatteryPower.text = String.format("%.2f", it.power)

                dashboardViewModel.addDataAndReduceX(Entry(60F, abs(it.currentNow.toFloat())))
                dashboardViewModel.lineData.notifyDataChanged()
                chargingCurrentChart.notifyDataSetChanged()
                chargingCurrentChart.invalidate()

                tvChargingCurrent.text = buildString {
                    append(it.currentNow)
                    append(getString(R.string.ma))
                }
            }
        }

        dashboardViewModel.startBatteryMonitoring()
    }

    private fun setUpLineChart() {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        val mainColor = typedValue.data

        val accentColor = ContextCompat.getColor(requireContext(), R.color.accent)

        dashboardViewModel.lineDataSet.color = mainColor
        dashboardViewModel.lineDataSet.setDrawValues(false)
        dashboardViewModel.lineDataSet.setDrawCircles(false)
        dashboardViewModel.lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        dashboardViewModel.lineDataSet.valueFormatter = DefaultValueFormatter(0)
        dashboardViewModel.lineDataSet.setDrawFilled(true)
        dashboardViewModel.lineDataSet.isHighlightEnabled = false
        val fillGradient = ContextCompat.getDrawable(requireContext(), R.drawable.chart_gradient)
        dashboardViewModel.lineDataSet.fillDrawable = fillGradient

        binding?.apply {
            chargingCurrentChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

            chargingCurrentChart.axisRight.isEnabled = true
            chargingCurrentChart.axisLeft.isEnabled = false

            chargingCurrentChart.xAxis.setLabelCount(13,true)
            chargingCurrentChart.axisRight.setLabelCount(8, true)

            chargingCurrentChart.xAxis.axisMinimum = 0F
            chargingCurrentChart.xAxis.axisMaximum = 60F

            chargingCurrentChart.isAutoScaleMinMaxEnabled = true

            chargingCurrentChart.axisRight.enableGridDashedLine(10f,10f,0f)
            chargingCurrentChart.xAxis.enableGridDashedLine(10f,10f,0f)

            chargingCurrentChart.description.isEnabled = false

            chargingCurrentChart.legend.isEnabled = false

            chargingCurrentChart.xAxis.textColor = mainColor
            chargingCurrentChart.xAxis.axisLineColor = mainColor
            chargingCurrentChart.xAxis.gridColor = accentColor

            chargingCurrentChart.axisRight.textColor = mainColor
            chargingCurrentChart.axisRight.axisLineColor = mainColor
            chargingCurrentChart.axisRight.gridColor = accentColor

            chargingCurrentChart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${60-value.toInt()}" + "s"
                }
            }

            chargingCurrentChart.axisRight.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }

            chargingCurrentChart.xAxis.labelRotationAngle = -45f

            chargingCurrentChart.axisRight.granularity = 100F

            chargingCurrentChart.data = dashboardViewModel.lineData
        }
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
}