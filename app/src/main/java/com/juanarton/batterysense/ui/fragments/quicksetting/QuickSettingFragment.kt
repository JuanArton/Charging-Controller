package com.juanarton.batterysense.ui.fragments.quicksetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.slider.Slider
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.FragmentQuickSettingBinding
import com.juanarton.core.data.domain.batteryInfo.model.Result
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuickSettingFragment : Fragment() {

    private var _binding: FragmentQuickSettingBinding? = null
    private val binding get() = _binding
    private val qsViewModel: QuickSettingViewModel by viewModels()
    private var firstRun = true

    private lateinit var isRooted: Shell
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isRooted = Shell.getShell()
        return if (isRooted.status == 1) {
            _binding = FragmentQuickSettingBinding.inflate(inflater, container, false)
            binding?.root
        } else {
            inflater.inflate(R.layout.root_denied_view, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isRooted.status == 1) {
            qsViewModel.config.observe(viewLifecycleOwner) { config ->
                binding?.apply {
                    chargingSwitch.isChecked = config.chargingSwitch

                    sliderChargingCurrent.value = config.current
                    tvCurrentValue.text = config.current.toInt().toString()

                    chargingLimitSwitch.isChecked = config.limitSwitch
                    sliderChargingLimit.value = config.maxCapacity
                    tvMaxCapacity.text = config.maxCapacity.toInt().toString()

                    tvCSDescription.visibility =
                        if (config.chargingLimitTriggered) View.VISIBLE else View.INVISIBLE

                    if (firstRun) {
                        registerListener()
                        firstRun = false
                    }
                    test.setHead("test")
                    test.setContent("test")
                }
            }

            val reapplyConfigListener: (Result) -> Unit = { result ->
                if (!result.success) {
                    qsViewModel.getConfig()
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }

            qsViewModel.setChargingSwitchStatus().observe(viewLifecycleOwner, reapplyConfigListener)
            qsViewModel.setTargetCurrent().observe(viewLifecycleOwner, reapplyConfigListener)
            qsViewModel.setChargingLimitStatus().observe(viewLifecycleOwner, reapplyConfigListener)
            qsViewModel.setMaximumCapacity().observe(viewLifecycleOwner, reapplyConfigListener)
        }
    }

    private fun registerListener() {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}