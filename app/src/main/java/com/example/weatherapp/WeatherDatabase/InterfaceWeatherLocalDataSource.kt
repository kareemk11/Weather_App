package com.example.weatherapp.WeatherDatabase

import com.example.weatherapp.Model.Alert
import com.example.weatherapp.Model.CurrentWeather
import com.example.weatherapp.Model.ForecastLocal
import kotlinx.coroutines.flow.Flow

interface InterfaceWeatherLocalDataSource {
    // Forecast Methods
    suspend fun insertForecast(forecast: ForecastLocal)

    suspend fun getAllForecasts(): List<ForecastLocal>

    suspend fun deleteAllForecasts()

    suspend fun getForecastByWeatherID(currentWeatherId: Int): Flow<List<ForecastLocal>>

    suspend fun getForecastDetails(): List<ForecastLocal>

    // Alert Methods
    suspend fun insertAlert(alert: Alert?)

    suspend fun getAllAlerts(): Flow<List<Alert>>

    suspend fun deleteAlert(id: Int?)

    suspend fun deleteAlertByWorkManagerId(workManagerId: String?)

    // Current Weather Methods
    suspend fun insertCurrentWeather(weather: CurrentWeather): Long

    suspend fun getCurrentWeather(): CurrentWeather

    suspend fun deleteCurrentWeather(id: Int?)

    suspend fun getAllFavourites(): Flow<List<CurrentWeather>>

    suspend fun deleteFavourite(id: Int?)

    suspend fun insertFavourite(favourite: CurrentWeather)
}