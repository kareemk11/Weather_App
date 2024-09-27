package com.example.weatherapp.Alerts

import androidx.work.WorkerParameters
import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.work.Worker
import com.example.weatherapp.app_utils.AlarmUtils

class WeatherAlertWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("WeatherAlertWorker", "Weather alert triggered!")
        val isNotification = inputData.getBoolean("isNotification", false)
        val weatherDetails = inputData.getString("weatherDetails")
        val temperature = inputData.getDouble("temperature", 0.0)

        if (weatherDetails != null ) {

                WeatherAlertManager(applicationContext).showNotification(
                    weatherDetails,
                    temperature.toString(),
                    isNotification
                )
        }

        return Result.success()
    }







}


