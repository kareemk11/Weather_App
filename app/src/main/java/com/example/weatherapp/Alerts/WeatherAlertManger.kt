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
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.ui.text.font.FontVariation
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapp.utils.NotificationUtils
import com.example.weatherapp.Model.Alert
import com.example.weatherapp.R
import com.example.weatherapp.app_utils.PermissionsUtils
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class WeatherAlertManager(private val context: Context) {
    //private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    //private val activeAlerts = mutableMapOf<Int, WeatherAlert>()

    data class WeatherAlert(
        val startTime: Long,
        val endTime: Long,
        val isNotification: Boolean
    )


    fun showWeatherAlertDialog(viewModel: AlertsViewModel, onResult: (Alert?, Boolean) -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_weather_alert, null)

        val datePicker = dialogView.findViewById<DatePicker>(R.id.date_picker)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.start_time_picker)
        val isNotificationSwitch =
            dialogView.findViewById<SwitchMaterial>(R.id.is_notification_switch)

        timePicker.setIs24HourView(true)

        val today = Calendar.getInstance()
        datePicker.minDate = today.timeInMillis

        val dialog = AlertDialog.Builder(context)
            .setTitle("Set Weather Alert")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->


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
                val isNotification = isNotificationSwitch.isChecked


                val delayMillis = selectedTimeMillis - currentTimeMillis

                if (delayMillis > 0) {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                    val formattedDate = dateFormat.format(selectedDate.time)
                    val formattedTime = timeFormat.format(selectedTime.time)

                    Log.d("WeatherAlert", "Scheduled alert in: $delayMillis ms")

                    CoroutineScope(Dispatchers.Main).launch{
                    if (isNotification)  {
                        NotificationUtils.checkAndPromptEnableNotifications(context)
                    } else {
                        PermissionsUtils.checkOverlayPermissionAndShowDialog(context)
                    }
                        Log.d("WeatherAlert", "Scheduled alert in: $delayMillis ms")
                        scheduleWeatherAlertTask(delayMillis, isNotification, viewModel)
                    }

                    Log.d("WeatherAlert", "Scheduled alert for: $formattedDate at $formattedTime")
                    val alert = Alert(
                        alertDate = formattedDate,
                        alertTime = formattedTime,
                        alertType = if (isNotification) "Notification" else "Alarm",
                        alertMessage = "Weather alert set for $formattedDate at $formattedTime"
                    )
                    onResult(alert, true)
                } else {
                    Toast.makeText(
                        context,
                        "Selected time must be in the future",
                        Toast.LENGTH_SHORT
                    ).show()
                    onResult(null, false)
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                onResult(null, false)
            }
            .create()

        dialog.show()
    }


    suspend fun scheduleWeatherAlertTask(
        delayMillis: Long,
        isNotification: Boolean,
        viewModel: AlertsViewModel
    ) {

        val weatherDetails = viewModel.getWeatherDetailsFromDatabase(delayMillis)
        val weatherDetailsString = weatherDetails?.description

        val temperature = weatherDetails?.temp


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
            notificationManager.createNotificationChannel(channel)
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


//fun stopAlarm(context: Context, mediaPlayer: MediaPlayer) {
//    mediaPlayer.stop()
//    mediaPlayer.release()
//
//    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//    vibrator.cancel()
//}

class DateTimePickers(parent: ViewGroup) {
    private val datePicker: DatePicker
    private val timePicker: TimePicker
    private val calendar = Calendar.getInstance()

    init {
        val inflater = LayoutInflater.from(parent.context)
        val dateTimeLayout = inflater.inflate(R.layout.layout_date_time_picker, parent, false)
        parent.addView(dateTimeLayout)

        datePicker = dateTimeLayout.findViewById(R.id.date_picker)
        timePicker = dateTimeLayout.findViewById(R.id.time_picker)

        initDateTimePickers()
    }

    private fun initDateTimePickers() {
        datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
        }
    }

    fun getTimeMillis(): Long = calendar.timeInMillis
}

/*
 fun setWeatherAlert(startTime: Long, endTime: Long, isNotification: Boolean): Int {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val requestCode = activeAlerts.size

        alarmManager.setWindow(
            AlarmManager.RTC_WAKEUP,
            startTime,
            endTime - startTime,
            pendingIntent
        )
        activeAlerts[requestCode] = WeatherAlert(startTime, endTime, isNotification)

        if (isNotification) {
            //showNotification()
        } else {

        }

        return requestCode
    }


    fun cancelWeatherAlert(requestCode: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
        notificationManager.cancel(requestCode)
        activeAlerts.remove(requestCode)
    }

    private fun updateDateEditText(calendar: Calendar, editText: EditText) {
        editText.setText(
            "${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.DAY_OF_MONTH)}/${
                calendar.get(
                    Calendar.YEAR
                )
            }"
        )
    }

    private fun updateTimeEditText(calendar: Calendar, editText: EditText) {
        editText.setText(
            "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
        )
    }

    private fun getTimeInMillis(dateEditText: EditText, timeEditText: EditText): Long {
        val dateString = dateEditText.text.toString()
        val timeString = timeEditText.text.toString()

        val calendar = Calendar.getInstance()
        val dateAndTime = "$dateString $timeString"
        calendar.time = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault()).parse(dateAndTime)
        return calendar.timeInMillis
    }
 */


//The key changes in this update are:
//
//1. Added `EditText` fields for start date, start time, end date, and end time, along with click listeners that open date and time pickers.
//2. Updated the `setWeatherAlert()` method to take the start and end times (in milliseconds) instead of just a duration.
//3. The `WeatherAlert` data class now includes `startTime` and `endTime` fields instead of just a duration.
//4. The `setWeatherAlert()` method uses the `setWindow()` method of the `AlarmManager` to set a time window for the alarm, instead of just a single trigger time.
//5. The `getTimeInMillis()` method is added to convert the date and time strings entered in the dialog into a long value representing the time in milliseconds.
//
//With these changes, the user can now set a weather alert with a specific start and end date/time, as well as choose whether it should be a notification or a default alarm sound.
//
//Let me know if you have any other questions!