package com.example.weatherapp.Model

import com.example.weatherapp.Network.WeatherRemoteDataSource
import com.example.weatherapp.WeatherDatabase.WeatherLocalDataSource
import retrofit2.Response

class WeatherRepository private constructor(
    private val localDataSource: WeatherLocalDataSource,
    private val remoteDataSource: WeatherRemoteDataSource
) {
    companion object {
        @Volatile
        private var instance: WeatherRepository? = null
        fun getInstance(
            localDataSource: WeatherLocalDataSource,
            remoteDataSource: WeatherRemoteDataSource
        ): WeatherRepository {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepository(localDataSource, remoteDataSource).also {
                    instance = it
                }
            }
        }

    }

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        units: String = "metric",
        lang: String = "en"
    ): Response<WeatherResponse> {
        return remoteDataSource.getCurrentWeather(latitude, longitude, units, lang)
    }

    suspend fun getFiveDayForecast(
        latitude: Double,
        longitude: Double,
        units: String = "metric",
        lang: String = "en"
    ): Response<ForecastResponse> {
        return remoteDataSource.getFiveDayForecast(latitude, longitude, units, lang)

    }

}