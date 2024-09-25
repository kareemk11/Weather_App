package com.example.weatherapp.Alerts

import androidx.work.WorkerParameters
import android.content.Context
import android.util.Log
import androidx.work.Worker

class WeatherAlertWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {

        Log.d("WeatherAlertWorker", "Weather alert triggered!")
        WeatherAlertManager(applicationContext).showNotification()
        return Result.success()
    }
}


