package com.example.weatherapp.Model

import com.example.weatherapp.Network.WeatherRemoteDataSource
import com.example.weatherapp.WeatherDatabase.WeatherLocalDataSource
import kotlinx.coroutines.flow.Flow
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
        lang: String = "en",
        isFavourite: Boolean = false
    ): Response<WeatherResponse> {
        val response = remoteDataSource.getCurrentWeather(latitude, longitude, units, lang)
        if (response.isSuccessful) {
            if (!isFavourite) {
                response.body()?.let { weatherResponse ->
                    val currentWeather = weatherResponse.toCurrentWeather(
                        latitude,
                        longitude,
                        1
                    ) // Conversion method
                    localDataSource.insertCurrentWeather(currentWeather)
                }
            }
        }

        return response

    }

    suspend fun getFiveDayForecast(
        latitude: Double,
        longitude: Double,
        units: String = "metric",
        lang: String = "en",
        isFavourite: Boolean = false
    ): Response<ForecastResponse> {
        val response = remoteDataSource.getFiveDayForecast(latitude, longitude, units, lang)
        if (response.isSuccessful) {
            if (!isFavourite) {
                response.body()?.let { forecastResponse ->
                    val forecasts = forecastResponse.toForecastLocalList()
                    forecasts.forEach { forecast ->
                        localDataSource.insertForecast(forecast)
                    }
                }
            }

        }
        return response
    }

    suspend fun getAllAlerts(): Flow<List<Alert>> {
        return localDataSource.getAllAlerts()
    }

    suspend fun insertAlert(alert: Alert) {
        localDataSource.insertAlert(alert)
    }

    suspend fun deleteAlert(alert: Alert) {
        localDataSource.deleteAlert(alert.id)
    }


    suspend fun getForecastByWeatherID(currentWeatherId: Int): List<ForecastLocal> {
        return localDataSource.getForecastByWeatherID(currentWeatherId)
    }

    /*
    suspend fun getAllCurrentWeather(): List<CurrentWeather> {
    return localDataSource.getAllCurrentWeather()
    }
    */

    suspend fun getCurrentWeatherFromLocal(): CurrentWeather {
        return localDataSource.getCurrentWeather()
    }

    suspend fun getAllFavourites(): Flow<List<CurrentWeather>> {
        return localDataSource.getAllFavourites()
    }

    suspend fun deleteFavourite(favourite: CurrentWeather) {

        localDataSource.deleteFavourite(favourite.id)

    }

    suspend fun insertFavourite(favourite: CurrentWeather): Long {
        return localDataSource.insertCurrentWeather(favourite)

    }

    suspend fun insertForecast(forecast: ForecastLocal) {
        localDataSource.insertForecast(forecast)

    }

//    fun getAlerts(): List<Alert>? {
//
//
//    }

}
