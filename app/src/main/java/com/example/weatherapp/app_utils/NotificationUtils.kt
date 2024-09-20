package com.example.myapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import android.widget.Toast

object NotificationUtils {

    fun checkAndPromptEnableNotifications(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)

        // Check if notifications are enabled for the app
        if (!notificationManager.areNotificationsEnabled()) {
            // Show alert dialog
            AlertDialog.Builder(context)
                .setTitle("Enable Notifications")
                .setMessage("Notifications are disabled. Do you want to enable them in settings?")
                .setPositiveButton("Yes") { _, _ ->
                    // Open notification settings for this app
                    openNotificationSettings(context)
                }
                .setNegativeButton("No", null)
                .show()
        } else {
            Toast.makeText(context, "Notifications are enabled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openNotificationSettings(context: Context) {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For Android 8.0 and above, open specific notification settings for the app
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else {
            // For older versions, open the general app settings
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }
}
