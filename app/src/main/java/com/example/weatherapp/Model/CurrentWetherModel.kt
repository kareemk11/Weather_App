package com.example.weatherapp.Model

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val sys: Sys,
    val visibility: Int,
    val clouds: Cloud

)

data class Main(
    val temp: Double,
    val pressure: Int,
    val humidity: Int,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val sea_level: Int,
    val grnd_level: Int
)

data class Weather(
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double
)

data class Sys(
    val country: String
)

data class Cloud(
    val all: Int
)

/*
"temp": 283.61,
        "feels_like": 282.8,
        "temp_min": 282.71,
        "temp_max": 284.8,
        "pressure": 1014,
        "humidity": 80,
        "sea_level": 1014,
        "grnd_level": 946
    },
    "visibility": 10000,
 */
