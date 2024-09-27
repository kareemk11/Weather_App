package com.example.weatherapp.Alerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.weatherapp.app_utils.AlarmUtils


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "DISMISS_NOTIFICATION") {
            NotificationManagerCompat.from(context).cancel(0)
            AlarmUtils.stopAlarm()
        }
    }
}



