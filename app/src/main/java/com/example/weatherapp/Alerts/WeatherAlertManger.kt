package com.example.weatherapp.Alerts

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapp.utils.NotificationUtils
import com.example.weatherapp.Model.Alert
import com.example.weatherapp.R
import com.example.weatherapp.app_utils.OverlayPermissionsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class WeatherAlertManager(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager





    fun showWeatherAlertDialog(viewModel: AlertsViewModel) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_weather_alert, null)

        val datePicker = dialogView.findViewById<DatePicker>(R.id.date_picker)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.start_time_picker)

        Log.d("WeatherAlert", "DatePicker: $datePicker")

        val alertTypeRadioGroup = dialogView.findViewById<RadioGroup>(R.id.alert_type_radio_group)
        val radioAlert = dialogView.findViewById<RadioButton>(R.id.radio_alert)
        val radioNotification = dialogView.findViewById<RadioButton>(R.id.radio_notification)

        if (alertTypeRadioGroup.checkedRadioButtonId == -1) {
            radioNotification.isChecked = true
        }

        timePicker.setIs24HourView(true)

        val today = Calendar.getInstance()
        datePicker.minDate = today.timeInMillis

        val dialog = AlertDialog.Builder(context)
            .setTitle("Set Weather Alert")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->

                if (alertTypeRadioGroup.checkedRadioButtonId == -1) {
                    Toast.makeText(context, "Please select an alert type", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val selectedDate = Calendar.getInstance().apply {
                    set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                }

                val selectedTime = Calendar.getInstance().apply {
                    timeInMillis = selectedDate.timeInMillis
                    set(Calendar.HOUR_OF_DAY, timePicker.hour)
                    set(Calendar.MINUTE, timePicker.minute)
                }

                val selectedTimeMillis = selectedTime.timeInMillis
                val currentTimeMillis = System.currentTimeMillis()

                val isNotification = radioNotification.isChecked

                val delayMillis = selectedTimeMillis - currentTimeMillis

                if (delayMillis > 0) {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                    val formattedDate = dateFormat.format(selectedDate.time)
                    val formattedTime = timeFormat.format(selectedTime.time)

                    Log.d("WeatherAlert", "Scheduled alert in: $delayMillis ms")

                    CoroutineScope(Dispatchers.Main).launch {
                        if (isNotification) {
                            NotificationUtils.checkAndPromptEnableNotifications(context)
                        } else {
                            OverlayPermissionsUtils.checkOverlayPermissionAndShowDialog(context)
                        }

                        val alert = Alert(
                            alertDate = formattedDate,
                            alertTime = formattedTime,
                            alertType = if (isNotification) "Notification" else "Alert", // Set type based on selection
                            alertMessage = "Weather alert set for $formattedDate at $formattedTime"
                        )
                        scheduleWeatherAlertTask(delayMillis, isNotification, viewModel, alert)
                    }

                    Log.d("WeatherAlert", "Scheduled alert for: $formattedDate at $formattedTime")

                } else {
                    Toast.makeText(context, "Selected time must be in the future", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(context, "Action canceled", Toast.LENGTH_SHORT).show()
            }
            .create()

        dialog.show()
    }



    private suspend fun scheduleWeatherAlertTask(
        delayMillis: Long,
        isNotification: Boolean,
        viewModel: AlertsViewModel,
        alert: Alert
    ) {

        val weatherDetails = viewModel.getWeatherDetailsFromDatabase(delayMillis)
        val weatherDetailsString = weatherDetails?.description

        val temperature = weatherDetails?.temp


        Log.d("WeatherAlert", "Weather details: $weatherDetailsString")
        Log.d("WeatherAlert", "Temperature: $temperature")

        val data = Data.Builder()
            .putBoolean("isNotification", isNotification)
            .putString("weatherDetails", weatherDetailsString)
            .putDouble("temperature", temperature ?: 0.0)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)

        Log.d("WeatherAlert", "Scheduled alert in: $delayMillis ms")

        val workId = workRequest.id
        alert.workManagerId = workId.toString()

        viewModel.insertAlert(alert)
    }




    fun showNotification(
        weatherDetails: String,
        temperature: String,
        isNotification: Boolean
    ) {


        Log.d("WeatherAlert", "Weather details: $weatherDetails")
        Log.d("WeatherAlert", "Temperature: $temperature")

        var iconResId = 0

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

        val dismissIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = "DISMISS_NOTIFICATION"
            putExtra("isNotification", true)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (weatherDetails.contains("rain")) {
            iconResId = R.drawable.drop

        } else if (weatherDetails.contains("snow")) {

            iconResId = R.drawable.baseline_cloudy_snowing_24

        } else if (weatherDetails.contains("clear")) {

            iconResId = R.drawable.ultra_voilet

        } else if (weatherDetails.contains("clouds")) {
            iconResId = R.drawable.baseline_cloud_24
        }
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
        if (isNotification) {
            notificationBuilder
                .setSmallIcon(iconResId)
                .setContentTitle("Weather Alert")
                .setContentText("Weather details: $weatherDetails Temperature: $temperature")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .addAction(R.drawable.baseline_close_24, "Dismiss", dismissPendingIntent)
                .setAutoCancel(true)
        } else {

            startAlarmOverlayService(weatherDetails, temperature)

        }

        val notification = notificationBuilder.build()

        notificationManager.notify(0, notification)

    }

    private fun startAlarmOverlayService(weatherDetails: String, temperature: String) {

        Log.d("WeatherAlert", "Weather details: $weatherDetails")
        Log.d("WeatherAlert", "Temperature: $temperature")

        val serviceIntent = Intent(context, AlarmOverlayService::class.java)
        serviceIntent.putExtra("WeatherDetails", weatherDetails)
        serviceIntent.putExtra("Temperature", temperature)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }


}





