package com.example.weatherapp.app_utils

import com.example.weatherapp.Model.ForecastItem
import com.example.weatherapp.Model.ForecastResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FilterUtils {


     fun filterCurrentDayData(forecastResponse: ForecastResponse): List<ForecastItem> {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        return forecastResponse.list.filter { forecast ->
            val forecastDate = forecast.dt_txt.split(" ")[0]
            forecastDate == currentDate
        }
    }

     fun filterDailyData(forecastResponse: ForecastResponse): List<ForecastItem> {
        val dailyData = mutableListOf<ForecastItem>()
        forecastResponse.list.filter { forecast ->
            val time = forecast.dt_txt.split(" ")[1]
            time == "12:00:00"
        }.forEach { forecast ->
            dailyData.add(forecast)
        }

        dailyData.removeAt(0)


        return dailyData
    }
}