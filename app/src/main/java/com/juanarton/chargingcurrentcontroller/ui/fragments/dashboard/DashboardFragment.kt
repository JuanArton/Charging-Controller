package com.juanarton.chargingcurrentcontroller.ui.fragments.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.juanarton.chargingcurrentcontroller.R
import com.juanarton.chargingcurrentcontroller.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashboardViewModel.batteryInfo.observe(viewLifecycleOwner) {
            binding?.apply {
                arcBatteryPercentage.progress = it.level

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

                tvBatteryCurrent.text = it.currentNow.toString()

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