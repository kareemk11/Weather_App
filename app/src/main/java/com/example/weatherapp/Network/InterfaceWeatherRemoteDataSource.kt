package com.example.weatherapp.Network

import com.example.weatherapp.Model.ForecastResponse
import com.example.weatherapp.Model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface InterfaceWeatherRemoteDataSource {
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        units: String = "metric",
        lang: String = "en"
    ): Flow<Response<WeatherResponse>>

    suspend fun getFiveDayForecast(
        latitude: Double,
        longitude: Double,
        units: String = "metric",
        lang: String = "en"
    ): Flow<Response<ForecastResponse>>
}