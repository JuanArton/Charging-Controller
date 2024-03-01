package com.juanarton.batterysense.ui.fragments.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.FragmentAlarmBinding
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
        alarmViewModel.getBatteryLevelAlarmStatus()
        alarmViewModel.getBatteryTemperatureThreshold()
        alarmViewModel.getBatteryTemperatureAlarmStatus()
        alarmViewModel.getOneTimeAlarmStatus()

        binding?.apply {
            with (alarmViewModel) {
                batteryLevelAlarmStatus.observe(viewLifecycleOwner) {
                    batteryLevelAlarmSwitch.isChecked = it
                }

                batteryLevelThreshold.observe(viewLifecycleOwner) {
                    rsBatteryLevelThreshold.values = listOf(it.first.toFloat(), it.second.toFloat())
                    tvMinBatteryLevel.text = it.first.toString()
                    tvMaxBatteryLevel.text = it.second.toString()
                }

                batteryTemperatureAlarmStatus.observe(viewLifecycleOwner) {
                    temperatureAlarmSwitch.isChecked = it
                }

                batteryTemperatureThreshold.observe(viewLifecycleOwner) {
                    sliderMaxTemperature.value = it.toFloat()
                    tvMaxTemperature.text = it.toString()
                }

                oneTimeAlarmStatus.observe(viewLifecycleOwner) {
                    oneTimeAlarmSwitch.isChecked = it
                }

                batteryLevelAlarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                    setBatteryLevelAlarmStatus(isChecked) { isSuccess ->
                        if (!isSuccess) {
                            getBatteryLevelThreshold()
                        }
                    }
                }

                temperatureAlarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                    setBatteryTemperatureAlarmStatus(isChecked) { isSuccess ->
                        if (!isSuccess) {
                            getBatteryTemperatureThreshold()
                        }
                    }
                }

                oneTimeAlarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                    setOneTimeAlarmStatus(isChecked) {isSuccess ->
                        if (!isSuccess) {
                            getOneTimeAlarmStatus()
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
                                    requireContext(), getString(R.string.setBatteryLevelThresholdError), Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })

                sliderMaxTemperature.addOnSliderTouchListener(object : Slider.OnSliderTouchListener{
                    override fun onStartTrackingTouch(slider: Slider) {}

                    override fun onStopTrackingTouch(slider: Slider) {
                        val maxTemp = slider.value.toInt()
                        setBatteryTemperatureThreshold(maxTemp) { isSuccess ->
                            if (isSuccess)  {
                                tvMaxTemperature.text = maxTemp.toString()
                            } else {
                                Toast.makeText(
                                    requireContext(), getString(R.string.setBatteryLevelThresholdError), Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}