package com.example.weatherapp.Network

import com.example.weatherapp.Model.ForecastResponse
import com.example.weatherapp.Model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class FakeRemoteDataSource : InterfaceWeatherRemoteDataSource  {
    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<Response<WeatherResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun getFiveDayForecast(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<Response<ForecastResponse>> {
        TODO("Not yet implemented")
    }
}