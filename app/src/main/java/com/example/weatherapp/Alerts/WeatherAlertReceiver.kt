package com.example.weatherapp.Alerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class WeatherAlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val weatherAlertManager = WeatherAlertManager(context)
        weatherAlertManager.showNotification()
    }



}



