package com.example.weatherapp.Alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.weatherapp.MainActivity.MainActivity
import com.example.weatherapp.R

private const val CHANNEL_ID = "NOTIFICATION_CHANNEL"
private const val CHANNEL_NAME = "NOTIFICATION_CHANNEL"
private const val CHANNEL_DESC = "NOTIFICATION_CHANNEL_DESC"

const val NOTIFICATION_PERM = 1001

@RequiresApi(Build.VERSION_CODES.S)
fun createNotification(context: Context): NotificationCompat.Builder {

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_MUTABLE)

    if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
        val name = CHANNEL_NAME
        //val desc = CHANNEL_DESC
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,importance)
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

    }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.baseline_notifications_24)
        .setContentTitle("Scheduled Notification")
        .setContentText("Your notification is here!")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
    return builder

}