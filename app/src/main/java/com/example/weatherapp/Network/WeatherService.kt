package com.example.weatherapp.Network

import com.example.weatherapp.Model.ForecastResponse
import com.example.weatherapp.Model.GeoResponse
import com.example.weatherapp.Model.WeatherResponse
import com.example.weatherapp.Network.retrofitHelper.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {



    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String = API_KEY
    ): Response<WeatherResponse>


    @GET("forecast")
    suspend fun getFiveDayForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String = API_KEY
    ): Response<ForecastResponse>


    @GET("geo/1.0/direct")
    suspend fun getCoordinatesFromCity(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String = API_KEY
    ): Response<List<GeoResponse>>

    @GET("geo/1.0/reverse")
    suspend fun getCityFromCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String = API_KEY
    ): Response<List<GeoResponse>>
}
