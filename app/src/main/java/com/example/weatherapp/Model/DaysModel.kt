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




fun ForecastResponse.toForecastLocalList(id: Int = 1): List<ForecastLocal> {
    return this.list.map { forecastItem ->
        ForecastLocal(
            currentWeatherId = id,
            dt_txt = forecastItem.dt_txt,
            temp = forecastItem.main.temp,
            pressure = forecastItem.main.pressure,
            humidity = forecastItem.main.humidity,
            temp_min = forecastItem.main.temp_min,
            temp_max = forecastItem.main.temp_max,
            description = forecastItem.weather.firstOrNull()?.description ?: "",
            icon = forecastItem.weather.firstOrNull()?.icon ?: "",
            speed = forecastItem.wind.speed,
            all = forecastItem.clouds.all
        )
    }
}





