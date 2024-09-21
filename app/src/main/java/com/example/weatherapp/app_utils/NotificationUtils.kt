package com.example.myapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import android.widget.Toast
import com.example.weatherapp.Model.SettingsInPlace
import com.example.weatherapp.R

object NotificationUtils {

    fun handleNotificationsPermissions(context: Context) {
        if(SettingsInPlace.notificationsEnabled){
            checkAndPromptEnableNotifications(context)
        }
        else{
            Toast.makeText(context, R.string.notifications_disabled, Toast.LENGTH_SHORT).show()
        }

    }


    fun checkAndPromptEnableNotifications(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        if (!notificationManager.areNotificationsEnabled()) {
            AlertDialog.Builder(context)
                .setTitle("Enable Notifications")
                .setMessage("Notifications are disabled. Do you want to enable them in settings?")
                .setPositiveButton("Yes") { _, _ ->
                    openNotificationSettings(context)
                    val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putBoolean("notifications_enabled", true).apply()
                }
                .setNegativeButton("No"){
                        _, _ ->
                    Toast.makeText(context, R.string.notifications_disabled, Toast.LENGTH_SHORT).show()
                    val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putBoolean("notifications_enabled", false).apply()
                }
                .show()
        } else {
            Toast.makeText(context, R.string.notifications_enabled, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openNotificationSettings(context: Context) {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else {
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }
}
