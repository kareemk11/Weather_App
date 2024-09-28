package com.example.weatherapp.Alerts

import androidx.work.WorkerParameters
import android.content.Context

import android.util.Log
import androidx.work.Worker
import com.example.weatherapp.Model.WeatherRepository
import com.example.weatherapp.Network.WeatherRemoteDataSource
import com.example.weatherapp.WeatherDatabase.WeatherDatabase
import com.example.weatherapp.WeatherDatabase.WeatherLocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherAlertWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    val repository = WeatherRepository.getInstance(
        WeatherLocalDataSource(
            WeatherDatabase.getInstance(context).forecastDao(),
            WeatherDatabase.getInstance(context).alertDao(),
            WeatherDatabase.getInstance(context).currentWeatherDao()
        ), WeatherRemoteDataSource.getInstance()
    )


    override fun doWork(): Result {

        val workManagerId = id.toString()
        try {
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

            CoroutineScope(Dispatchers.IO).launch {
                Log.d("WeatherAlertWorker", "Deleting alert with workManagerId: $workManagerId")
                repository.deleteAlertByWorkManagerId(workManagerId)
            }
        }catch ( e : Exception) {
            Log.e("WeatherAlertWorker", "Error in WeatherAlertWorker: ${e.message}", e)
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("WeatherAlertWorker", "Deleting alert with workManagerId: $workManagerId")
                repository.deleteAlertByWorkManagerId(workManagerId)
            }

            return Result.failure()
        }


        return Result.success()
    }








}


