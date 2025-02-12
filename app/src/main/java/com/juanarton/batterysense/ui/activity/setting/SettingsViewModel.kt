package com.juanarton.batterysense.ui.activity.setting

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    companion object {
        const val THEME = "THEME"
        const val DARK = "DARK"
        const val LIGHT = "LIGHT"
        const val SYSTEM = "SYSTEM"
        const val CURRENT_UNIT = "currentUnit"
    }
    fun setTheme(editor: Editor, value: String) {
        editor.putString(THEME, value)
        editor.apply()
    }

    fun getTheme(sPref: SharedPreferences): String? {
        return sPref.getString(THEME, SYSTEM)
    }

    fun getCurrentUnit(sPref: SharedPreferences): String? {
        return sPref.getString(CURRENT_UNIT, "μA")
    }

    fun setCurrentUnit(editor: Editor, value: String) {
        editor.putString(CURRENT_UNIT, value)
        editor.apply()
    }
}