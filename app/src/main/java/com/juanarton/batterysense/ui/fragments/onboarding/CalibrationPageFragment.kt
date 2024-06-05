package com.juanarton.batterysense.ui.fragments.onboarding

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.FragmentCalibrationPageBinding
import com.juanarton.batterysense.ui.activity.main.MainActivity
import com.juanarton.core.utils.BatteryUtils.determineCurrentUnit
import com.juanarton.core.utils.BatteryUtils.getDesignedCapacity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class CalibrationPageFragment : Fragment() {

    private var _binding: FragmentCalibrationPageBinding? = null
    private val binding get() = _binding

    private val calibrationViewModel: CalibrationViewModel by viewModels()

    private val rawCurrent: ArrayList<Int> = arrayListOf()

    private var unit: String = "μA"

    private var capacity: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalibrationPageBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settings = requireContext().getSharedPreferences(MainActivity.PREFS_NAME, 0)

        binding?.apply {
            tvDeviceName.text = buildString {
                append("${Build.MANUFACTURER} ${Build.MODEL}")
            }
            tvAndroidVersion.text = buildString {
                append("${getString(R.string.android_version)} : ${Build.VERSION.RELEASE}")
            }

            btCalibrate.setOnClickListener {
                if (!isPlugged(requireContext())) {
                    if (btCalibrate.text == getString(R.string.calibrate)) {
                        btCalibrate.disable()

                        capacity = getDesignedCapacity(requireContext()).toInt()

                        tvCapacityValue.text = capacity.toString()

                        CoroutineScope(Dispatchers.IO).launch {
                            repeat(5) {
                                rawCurrent.add(
                                    abs(calibrationViewModel.getRawCurrent())
                                )

                                CoroutineScope(Dispatchers.Main).launch {
                                    tvPowerUsagePattern.text = buildString {
                                        append("${getString(R.string.power_pattern)} : ")
                                        append(rawCurrent.toString())
                                    }
                                }
                                delay(500L)
                            }

                            CoroutineScope(Dispatchers.Main).launch {
                                unit = determineCurrentUnit(rawCurrent)
                                tvUnit.text = buildString {
                                    append("${getString(R.string.unit)} : ")
                                    append(unit)
                                }

                                val wrongCapacityText = SpannableString(getString(R.string.wrong_capacity))
                                wrongCapacityText.setSpan(UnderlineSpan(), 0, wrongCapacityText.length, 0)
                                tvWrongCapacity.visibility = View.VISIBLE
                                tvWrongCapacity.text = wrongCapacityText
                                tvWrongCapacity.setOnClickListener {
                                    showCapacitytDialog()
                                }

                                btCalibrate.enable()
                                btCalibrate.text = getString(R.string.finish)
                            }
                        }
                    }
                    else if (btCalibrate.text == getString(R.string.finish)) {
                        settings.edit().putBoolean("first_launch", false).apply()
                        calibrationViewModel.insertInitialValue(requireContext())
                        calibrationViewModel.insertUnit(unit)
                        capacity?.let { it1 -> calibrationViewModel.insertCapacity(it1) }

                        val fragmentManager = requireActivity().supportFragmentManager
                        val fragment = fragmentManager.findFragmentByTag("Onboarding")
                        val transaction = fragmentManager.beginTransaction()
                        if (fragment != null) {
                            transaction.remove(fragment)
                        }
                        calibrationViewModel.deleteChargingHistory()
                        transaction.commit()
                        restartActivity()
                    }
                }
                else {
                    Toast.makeText(
                        requireContext(), getString(R.string.remove_charging_message), Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun View.disable() {
        val drawable = background
        val grayColor = ContextCompat.getColor(context, R.color.disabled)

        DrawableCompat.setTint(drawable, grayColor)
        isClickable = false
    }

    private fun View.enable() {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)

        val drawable = background
        val color = typedValue.data

        DrawableCompat.setTint(drawable, color)
        isClickable = true
    }

    private fun restartActivity() {
        val intent = Intent(requireContext(), requireActivity()::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun isPlugged(context: Context): Boolean {
        var isPlugged: Boolean
        val intent: Intent? = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val plugged: Int = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB
        isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
        return isPlugged
    }

    private fun showCapacitytDialog() {
        val dialogView: View = LayoutInflater.from(requireContext()).inflate(R.layout.calibration_dialog_box, null)

        val capacityEditText = dialogView.findViewById<EditText>(R.id.et_capacity)

        val okButton = dialogView.findViewById<Button>(R.id.dialog_ok_button)
        val cancelButton = dialogView.findViewById<Button>(R.id.dialog_cancel_button)

        val autostartDialogBuilder = AlertDialog.Builder(requireContext())
        autostartDialogBuilder.setView(dialogView)
        autostartDialogBuilder.setCancelable(true)

        val autostartDialog = autostartDialogBuilder.create()

        okButton.setOnClickListener {
             if (capacityEditText.text.toString().isNotEmpty()) {
                 capacity = capacityEditText.text.toString().toInt()
                 binding?.tvCapacityValue?.text = capacity.toString()
            }

            autostartDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            autostartDialog.dismiss()
        }

        autostartDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}