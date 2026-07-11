package com.tanerkaynar.nexuserp.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("nexus_erp_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
    }

    fun getThemeMode(): Int {
        return prefs.getInt(KEY_THEME_MODE, 0) 
    }

    fun setThemeMode(mode: Int) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply()
    }
}