package com.juanarton.batterysense.ui.fragments.quicksetting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.slider.Slider
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.FragmentQuickSettingBinding
import com.juanarton.batterysense.ui.activity.main.MainActivity
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

    private val command = "ls /data/adb/modules | grep 3C"
    private val result = Shell.cmd(command).exec()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isRooted = Shell.getShell()
        _binding = FragmentQuickSettingBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settings = requireContext().getSharedPreferences(MainActivity.PREFS_NAME, 0)

        if (result.out.isNotEmpty()) {
            if (isRooted.status == 1 && result.out[0] == "3C") {
                if (settings.getBoolean("qs_first_launch", true)) {
                    showDialog(requireContext(), settings)
                    firstRun = false
                }

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

                        registerListener()
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

            if (isRooted.status == 0 ) {
                registerListener()
            }
        }
    }

    private fun registerListener() {
        binding?.apply {
            sliderChargingCurrent.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                    val checkModule = checkModule()
                    if (checkModule.first) {
                        val value = slider.value.toInt().toString()
                        tvCurrentValue.text = value
                        qsViewModel.setCurrent(value)
                    }
                    else {
                        rootModuleCheckDialog(checkModule.second)
                    }
                }
            })

            chargingSwitch.setOnCheckedChangeListener { _, isChecked ->
                val checkModule = checkModule()
                if (checkModule.first) {
                    qsViewModel.setChargingSwitch(isChecked)
                }
                else {
                    rootModuleCheckDialog(checkModule.second)
                }
            }

            chargingLimitSwitch.setOnCheckedChangeListener { _, isChecked ->

                val checkModule = checkModule()

                if (checkModule.first) {
                    qsViewModel.setChargingLimitSwitch(isChecked)
                }
                else {
                    rootModuleCheckDialog(checkModule.second)
                }
            }

            sliderChargingLimit.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                    val checkModule = checkModule()
                    if (checkModule.first) {
                        val value = slider.value.toInt().toString()
                        tvMaxCapacity.text = value
                        qsViewModel.setMaximumCapacityValue(value)
                    }
                    else {
                        rootModuleCheckDialog(checkModule.second)
                    }
                }
            })
        }
    }

    private fun checkModule(): Pair<Boolean, String> {
        return if (isRooted.status == 1) {
            if (result.out.isNotEmpty()) {
                if (result.out[0] == "3C") {
                    Pair(true, getString(R.string.root_granted_module_installed))
                } else {
                    Pair(false, getString(R.string.root_denied_module_installed))
                }
            } else {
                Pair(false, getString(R.string.root_granted_module_not_installed))
            }
        } else {
            Pair(false, getString(R.string.not_rooted))
        }
    }

    private fun rootModuleCheckDialog(title: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle(title)
            setMessage(getString(R.string.root_module_check_message))
            setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showDialog(context: Context, settings:SharedPreferences) {
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.custom_dialog_box, null)

        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        dialogMessage.text = getString(R.string.qs_dialog_message)

        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
        dialogTitle.text = getString(R.string.warning)

        val okButton = dialogView.findViewById<Button>(R.id.dialog_ok_button)

        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(dialogView)
        alertDialogBuilder.setCancelable(false)

        val alertDialog = alertDialogBuilder.create()

        okButton.setOnClickListener {
            alertDialog.dismiss()
            settings.edit().putBoolean("qs_first_launch", false).apply()
        }

        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}