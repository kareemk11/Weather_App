package com.example.weatherapp.Settings

import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

fun setLocale(activity: AppCompatActivity, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = activity.resources.configuration
    config.setLocale(locale)
    activity.createConfigurationContext(config)
    activity.resources.updateConfiguration(config, activity.resources.displayMetrics)
    //refreshActivity(activity)
}

fun refreshActivity(activity: AppCompatActivity) {
    val intent = activity.intent
    activity.finish()
    activity.startActivity(intent)
}

