package com.juanarton.batterysense.ui.activity.setting

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.ActivitySettingsBinding
import com.juanarton.batterysense.ui.activity.setting.SettingsViewModel.Companion.DARK
import com.juanarton.batterysense.ui.activity.setting.SettingsViewModel.Companion.LIGHT
import com.juanarton.batterysense.ui.activity.setting.SettingsViewModel.Companion.SYSTEM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val sPrefMonitoring = getSharedPreferences("batteryMonitoringData", Context.MODE_PRIVATE)

        binding?.apply {
            settingsViewModel.getTheme(sharedPreferences).let {
                cgThemeSelector.check(
                    when (it) {
                        SYSTEM -> R.id.chipSystem
                        LIGHT -> R.id.chipLight
                        DARK -> R.id.chipDark
                        else -> { R.id.chipSystem }
                    }
                )
            }

            settingsViewModel.getCurrentUnit(sPrefMonitoring).let {
                cgUnitSelector.check(
                    when(it) {
                        getString(R.string.ma) -> R.id.chipmA
                        getString(com.juanarton.core.R.string.microamp) -> R.id.chipuA
                        else -> R.id.chipuA
                    }
                )
            }

            cgThemeSelector.setOnCheckedStateChangeListener { group, _ ->
                when (group.checkedChipId) {
                    chipSystem.id -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        settingsViewModel.setTheme(sharedPreferences.edit(), SYSTEM)
                    }
                    chipLight.id -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        settingsViewModel.setTheme(sharedPreferences.edit(), LIGHT)
                    }
                    chipDark.id -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        settingsViewModel.setTheme(sharedPreferences.edit(), DARK)
                    }
                }
            }

            cgUnitSelector.setOnCheckedStateChangeListener { group, _ ->
                when (group.checkedChipId) {
                    chipmA.id -> {
                        settingsViewModel.setCurrentUnit(sPrefMonitoring.edit(), getString(R.string.ma))
                    }
                    chipuA.id -> {
                        settingsViewModel.setCurrentUnit(sPrefMonitoring.edit(), getString(com.juanarton.core.R.string.microamp))
                    }
                }
            }
        }
    }
}