package com.juanarton

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import com.juanarton.batterysense.ui.activity.setting.SettingsViewModel.Companion.DARK
import com.juanarton.batterysense.ui.activity.setting.SettingsViewModel.Companion.LIGHT
import com.juanarton.batterysense.ui.activity.setting.SettingsViewModel.Companion.SYSTEM
import com.juanarton.batterysense.ui.activity.setting.SettingsViewModel.Companion.THEME
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)

        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val theme = sharedPreferences.getString(THEME, SYSTEM)

        when (theme) {
            SYSTEM -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }
            LIGHT -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) }
            DARK -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) }
        }
    }
}