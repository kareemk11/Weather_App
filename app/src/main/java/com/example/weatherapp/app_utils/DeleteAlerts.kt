package com.example.weatherapp.app_utils

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object DeleteAlerts{


    suspend fun showDeleteAlert(context: Context, title: String, message: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val dialog = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Delete") { _, _ ->
                    continuation.resume(true)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                    continuation.resume(false)
                }
                .setOnCancelListener {
                    continuation.resume(false)
                }
                .create()

            dialog.show()

            // Cancel the coroutine if needed
            continuation.invokeOnCancellation {
                dialog.dismiss()
            }
        }
    }
}
