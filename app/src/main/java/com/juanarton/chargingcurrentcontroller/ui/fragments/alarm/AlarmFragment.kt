package com.juanarton.chargingcurrentcontroller.ui.fragments.alarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.slider.RangeSlider
import com.juanarton.chargingcurrentcontroller.R
import com.juanarton.chargingcurrentcontroller.databinding.FragmentAlarmBinding
import com.juanarton.core.data.repository.DataRepository.Companion.BATTERY_LEVEL_ALARM_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmFragment : Fragment() {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding

    private val alarmViewModel: AlarmViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarmViewModel.getBatteryLevelThreshold()
        alarmViewModel.getBatteryLevelAlarmStatus(BATTERY_LEVEL_ALARM_KEY)

        binding?.apply {
            with (alarmViewModel) {
                batteryLevelAlarmStatus.observe(viewLifecycleOwner) {
                    batteryLevelAlarmSwitch.isChecked = it
                }

                batterLevelThreshold.observe(viewLifecycleOwner) {
                    rsBatteryLevelThreshold.values = listOf(it.first.toFloat(), it.second.toFloat())
                }

                batteryLevelAlarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                    setBatteryLevelAlarmStatus(BATTERY_LEVEL_ALARM_KEY, isChecked) { isSuccess ->
                        if (!isSuccess) {
                            getBatteryLevelThreshold()
                        }
                    }
                }

                rsBatteryLevelThreshold.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
                    override fun onStartTrackingTouch(slider: RangeSlider) {}

                    override fun onStopTrackingTouch(slider: RangeSlider) {
                        val min = slider.values[0].toInt()
                        val max = slider.values[1].toInt()
                        setBatteryLevelThreshold(min, max) { isSuccess ->
                            if (isSuccess)  {
                                tvMinBatteryLevel.text = min.toString()
                                tvMaxBatteryLevel.text = max.toString()
                            } else {
                                Toast.makeText(
                                    requireContext(), getString(R.string.setBatteryThresholdError), Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })
            }
        }
    }
}