package com.example.weatherapp.app_utils


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import android.app.AlertDialog


object PermissionsUtils {





    fun checkOverlayPermissionAndShowDialog(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            AlertDialog.Builder(context)
                .setTitle("Permission Required")
                .setMessage("This app requires overlay permission to function properly. Please grant the permission.")
                .setPositiveButton("Go to Settings") { _, _ ->
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                    context.startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        } else {
            Toast.makeText(context, "Overlay permission is granted.", Toast.LENGTH_SHORT).show()
        }
    }


}
