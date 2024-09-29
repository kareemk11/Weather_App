package com.example.weatherapp.Model

import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface InterfaceWeatherRepository {
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        units: String = "metric",
        lang: String = "en",
        isFavourite: Boolean = false
    ): Flow<Response<WeatherResponse>>

    suspend fun getFiveDayForecast(
        latitude: Double,
        longitude: Double,
        units: String = "metric",
        lang: String = "en",
        isFavourite: Boolean = false
    ): Flow<Response<ForecastResponse>>

    suspend fun getAllAlerts(): Flow<List<Alert>>

    suspend fun insertAlert(alert: Alert?)

    suspend fun deleteAlert(alert: Alert?)

    suspend fun deleteAlertByWorkManagerId(workManagerId: String?)

    suspend fun getForecastByWeatherID(currentWeatherId: Int): Flow<List<ForecastLocal>>

    suspend fun getForecastDetails(): List<ForecastLocal>

    suspend fun getCurrentWeatherFromLocal(): CurrentWeather

    suspend fun getAllFavourites(): Flow<List<CurrentWeather>>

    suspend fun deleteFavourite(favourite: CurrentWeather?)

    suspend fun insertFavourite(favourite: CurrentWeather?): Long

    suspend fun insertForecast(forecast: ForecastLocal)
}