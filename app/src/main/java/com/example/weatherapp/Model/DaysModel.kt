package com.example.weatherapp.Model

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt_txt: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val clouds: Cloud,
    val visibility: Int
)
