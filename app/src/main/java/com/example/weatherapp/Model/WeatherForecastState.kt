package com.example.weatherapp.Model

sealed class WeatherForecastState {

    object Loading : WeatherForecastState()
    data class Success(val forecastResponse: ForecastResponse?) : WeatherForecastState()
    data class Error(val errorMessage: String) : WeatherForecastState()
}


sealed class CurrentWeatherState {

    object Loading : CurrentWeatherState()
    data class Success(val currentWeatherResponse: WeatherResponse?) : CurrentWeatherState()
    data class Error(val errorMessage: String) : CurrentWeatherState()

}