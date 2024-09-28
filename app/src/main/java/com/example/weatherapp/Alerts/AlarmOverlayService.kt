package com.example.weatherapp.Alerts


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.weatherapp.R
import com.example.weatherapp.app_utils.AlarmUtils

class AlarmOverlayService : Service() {
    private var overlayView: View? = null
    private lateinit var windowManager: WindowManager
    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = showNotification(
            intent?.getStringExtra("WeatherDetails") ?: "",
            intent?.getStringExtra("Temperature") ?: ""
        )
        startForeground(1, notification)

        intent?.let {
            showAlarmOverlay(it)
        }

        return START_STICKY
    }

    private fun showAlarmOverlay(intent: Intent) {
        if (overlayView != null) return

        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = layoutInflater.inflate(R.layout.alarm_layout, null)

        val weatherDetails = intent.getStringExtra("WeatherDetails")
        val temperature = intent.getStringExtra("Temperature")

        AlarmUtils.playAlarmSound(this)
        AlarmUtils.vibratePhone(this)
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP
        }

        windowManager.addView(overlayView, layoutParams)

        overlayView?.findViewById<TextView>(R.id.tvWeatherDescription)?.text = weatherDetails
        overlayView?.findViewById<TextView>(R.id.tvTemperature)?.text = "$temperature°C"

        setWeatherIcon(weatherDetails)

        overlayView?.findViewById<View>(R.id.btnDismiss)?.setOnClickListener {
            dismissOverlay()
        }
    }

    private fun setWeatherIcon(weatherDetails: String?) {
        val iconResource = when {
            weatherDetails?.contains("rain", ignoreCase = true) == true -> R.drawable.drop
            weatherDetails?.contains("snow", ignoreCase = true) == true -> R.drawable.baseline_cloudy_snowing_24
            weatherDetails?.contains("clear", ignoreCase = true) == true -> R.drawable.ultra_voilet
            weatherDetails?.contains("cloud", ignoreCase = true) == true -> R.drawable.cloudy
            else -> R.drawable.logo
        }
        overlayView?.findViewById<ImageView>(R.id.ivWeatherIcon)?.setImageResource(iconResource)
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }

    private fun dismissOverlay() {
        AlarmUtils.stopAlarm()
        AlarmUtils.stopVibration()
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
        stopSelf()
        stopForeground(true)

    }

    private fun showNotification(
        weatherDetails: String,
        temperature: String,
    ): Notification {

        Log.d("WeatherAlert", "Weather details: $weatherDetails")
        Log.d("WeatherAlert", "Temperature: $temperature")

        val channelId = "weather_alert_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Alert",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
                enableLights(true)
            }

            val existingChannels = notificationManager.notificationChannels
            val channelExists = existingChannels.any { it.id == channelId }
            if (!channelExists) {
                notificationManager.createNotificationChannel(channel)
            }

        }
            //notificationManager.createNotificationChannel(channel)


        val dismissIntent = Intent(this, NotificationReceiver::class.java).apply {
            action = "DISMISS_NOTIFICATION"
            putExtra("isNotification", true)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setContentTitle("Weather Alert")
            .setContentText("Weather details: $weatherDetails Temperature: $temperature")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.baseline_close_24, "Dismiss", dismissPendingIntent)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()
        /*
        notificationBuilder
                .setSmallIcon(iconResId)
                .setContentTitle("Weather Alert")
                .setContentText("Weather details: $weatherDetails Temperature: $temperature")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .addAction(R.drawable.baseline_close_24, "Dismiss", dismissPendingIntent)
                .setAutoCancel(true)notificationBuilder
                .setSmallIcon(iconResId)
                .setContentTitle("Weather Alert")
                .setContentText("Weather details: $weatherDetails Temperature: $temperature")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .addAction(R.drawable.baseline_close_24, "Dismiss", dismissPendingIntent)
                .setAutoCancel(true)
         */
    }


    override fun onBind(intent: Intent?): IBinder? = null
}


