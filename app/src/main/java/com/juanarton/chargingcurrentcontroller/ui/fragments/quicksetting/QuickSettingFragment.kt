package com.juanarton.chargingcurrentcontroller.ui.fragments.quicksetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.slider.Slider
import com.juanarton.chargingcurrentcontroller.R
import com.juanarton.chargingcurrentcontroller.databinding.FragmentQuickSettingBinding
import com.juanarton.core.data.domain.model.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuickSettingFragment : Fragment() {

    private var _binding: FragmentQuickSettingBinding? = null
    private val binding get() = _binding

    private val qsViewModel: QuickSettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuickSettingBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qsViewModel.getConfig()

        qsViewModel.config.observe(viewLifecycleOwner) {
            binding?.apply {
                chargingSwitch.isChecked = it.chargingSwitch

                sliderChargingCurrent.value = it.current
                tvCurrentValue.text = it.current.toInt().toString()

                binding?.apply {
                    chargingLimitSwitch.isChecked = it.limitSwitch
                    sliderChargingLimit.value = it.maxCapacity
                    tvMaxCapacity.text = it.maxCapacity.toInt().toString()

                    if (it.chargingLimitTriggered) {
                        tvCSDescription.visibility = View.VISIBLE
                    } else {
                        tvCSDescription.visibility = View.INVISIBLE
                    }
                }
            }
        }

        qsViewModel.setChargingSwitchStatus().observe(viewLifecycleOwner) {
            binding?.apply {
                if (!it.success) {
                    reapplyConfig(it)
                }
            }
        }

        qsViewModel.setTargetCurrent().observe(viewLifecycleOwner) {
            binding?.apply {
                if (!it.success) {
                    reapplyConfig(it)
                }
            }
        }

        qsViewModel.setChargingLimitStatus().observe(viewLifecycleOwner) {
            binding?.apply {
                if (!it.success) {
                    reapplyConfig(it)
                }
            }
        }

        qsViewModel.setMaximumCapacity().observe(viewLifecycleOwner) {
            binding?.apply {
                if (!it.success) {
                    reapplyConfig(it)
                }
            }
        }

        binding?.apply {
            sliderChargingCurrent.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                    val value = slider.value.toInt().toString()
                    tvCurrentValue.text = value
                    qsViewModel.setCurrent(value)
                }
            })

            chargingSwitch.setOnCheckedChangeListener { _, isChecked ->
                qsViewModel.setChargingSwitch(isChecked)
            }

            chargingLimitSwitch.setOnCheckedChangeListener { _, isChecked ->
                qsViewModel.setChargingLimitSwitch(isChecked)
            }

            sliderChargingLimit.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                    val value = slider.value.toInt().toString()
                    tvMaxCapacity.text = value
                    qsViewModel.setMaximumCapacityValue(value)
                }
            })
        }
    }

    private fun reapplyConfig(result: Result) {
        qsViewModel.getConfig()
        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
    }
}