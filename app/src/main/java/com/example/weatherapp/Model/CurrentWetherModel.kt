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


fun WeatherResponse.toCurrentWeather( lat: Double,  lon: Double, id: Int = 0): CurrentWeather {
    return CurrentWeather(
        id = id,
        lat = lat,
        lon = lon,
        name = this.name,
        temp = this.main.temp,
        pressure = this.main.pressure,
        humidity = this.main.humidity,
        temp_min = this.main.temp_min,
        temp_max = this.main.temp_max,
        description = this.weather.firstOrNull()?.description ?: "",
        icon = this.weather.firstOrNull()?.icon ?: "",
        speed = this.wind.speed,
        country = this.sys.country,
        all = this.clouds.all
    )
}






