package com.example.weatherapp.app_utils

import android.content.Context.MODE_PRIVATE
import com.example.weatherapp.Model.SettingsInPlace

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtils {

    const val PREF_NAME = "app_settings"
    private const val FIRST_LAUNCH_KEY = "isFirstLaunch"

    fun isFirstLaunch(sharedPreferences: SharedPreferences): Boolean {
        return sharedPreferences.getBoolean(FIRST_LAUNCH_KEY, true)
    }

    fun saveInitialSettings(
        context: Context,
        selectedLocationMethod: String,
        notificationsEnabled: Boolean
    ) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putBoolean(FIRST_LAUNCH_KEY, false)
            putBoolean("notifications_enabled", notificationsEnabled)
            putString("location_method", selectedLocationMethod)
            putString("unit", "metric")
            putString("language", "en")
            apply()
        }
    }

    fun saveSettingsObject(context: Context): SettingsInPlace {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        return SettingsInPlace.apply {
            unit = sharedPreferences.getString("unit", "metric").toString()
            language = sharedPreferences.getString("language", "en").toString()
            locationMethod = sharedPreferences.getString("location_method", "GPS").toString()
            notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        }
    }
}
