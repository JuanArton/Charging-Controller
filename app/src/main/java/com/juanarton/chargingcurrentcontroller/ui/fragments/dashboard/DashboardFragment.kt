package com.juanarton.chargingcurrentcontroller.ui.fragments.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.juanarton.chargingcurrentcontroller.R
import com.juanarton.chargingcurrentcontroller.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint


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

        Log.d("test", batteryCapacity.toString())
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

                tvBatteryTemperature.text = it.temperature.toInt().toString()

                tvBatteryPower.text = String.format("%.2f", it.power)
            }
        }

        dashboardViewModel.startBatteryMonitoring()
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