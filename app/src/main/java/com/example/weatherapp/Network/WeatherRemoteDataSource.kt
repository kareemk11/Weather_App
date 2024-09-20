package com.example.weatherapp.Network

import com.example.weatherapp.Model.ForecastResponse
import com.example.weatherapp.Model.WeatherResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object retrofitHelper {

    val API_KEY: String = "2691d2dd0b02f1b661082da55e826d04"

    val BASE_URL: String = "https://api.openweathermap.org/data/2.5/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}

class WeatherRemoteDataSource private constructor() {
    private val retrofit = retrofitHelper.getInstance()
    private val weatherService = retrofit.create(WeatherService::class.java)

    companion object {
        @Volatile
        private var instance: WeatherRemoteDataSource? = null

        fun getInstance(): WeatherRemoteDataSource {
            return instance ?: synchronized(this) {
                instance ?: WeatherRemoteDataSource().also { instance = it }
            }
        }
    }

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        units: String = "metric",
        lang: String = "en"
    ): Response<WeatherResponse> {
        return weatherService.getCurrentWeather(latitude, longitude, units, lang)
    }

    suspend fun getFiveDayForecast(
        latitude: Double,
        longitude: Double,
        units: String = "metric",
        lang: String = "en"
    ): Response<ForecastResponse> {
        return weatherService.getFiveDayForecast(latitude, longitude, units, lang)
    }


}