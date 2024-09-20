package com.example.weatherapp.app_utils

import android.content.SharedPreferences
import android.content.Context
import android.view.LayoutInflater
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import com.example.weatherapp.R

object SettingsDialogUtils {

    fun showInitialSettingsDialog(
        context: Context,
        sharedPreferences: SharedPreferences,
        onSave: (String, Boolean) -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_initial_settings, null)

        val locationGroup = dialogView.findViewById<RadioGroup>(R.id.locationGroup)
        val notificationsSwitch = dialogView.findViewById<SwitchCompat>(R.id.notificationSwitch)

        notificationsSwitch.isChecked = sharedPreferences.getBoolean("notifications_enabled", true)
        val savedLocationMethod = sharedPreferences.getString("location_method", "GPS")
        when (savedLocationMethod) {
            "GPS" -> locationGroup.check(R.id.gpsOption)
            "Map" -> locationGroup.check(R.id.mapOption)
        }

        AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val selectedLocationMethod = when (locationGroup.checkedRadioButtonId) {
                    R.id.gpsOption -> "GPS"
                    R.id.mapOption -> "Map"
                    else -> "GPS"
                }
                val notificationsEnabled = notificationsSwitch.isChecked
                onSave(selectedLocationMethod, notificationsEnabled)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
