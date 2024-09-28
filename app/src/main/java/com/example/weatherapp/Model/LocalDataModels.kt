package com.example.weatherapp.Model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(
    foreignKeys = [ForeignKey(
        entity = CurrentWeather::class,
        parentColumns = ["id"],
        childColumns = ["currentWeatherId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ForecastLocal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Auto-generated ID
    var currentWeatherId: Int,  // This column references CurrentWeather(id)
    val dt_txt: String,
    val temp: Double,
    val pressure: Int,
    val humidity: Int,
    val temp_min: Double,
    val temp_max: Double,
    val description: String,
    val icon: String,
    val speed: Double,
    val all: Int
)


@Entity
data class Alert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Auto-generated ID
    val alertMessage: String,
    val alertType: String,
    val alertDate: String,
    val alertTime: String,
    var workManagerId: String? = null
)

@Entity
data class CurrentWeather(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Auto-generated ID
    val lat: Double,
    val lon: Double,
    val name: String,
    val temp: Double,
    val pressure: Int,
    val humidity: Int,
    val temp_min: Double,
    val temp_max: Double,
    val description: String,
    val icon: String,
    val speed: Double,
    val country: String,
    val all: Int
): Serializable




fun CurrentWeather.toWeatherResponse(): WeatherResponse {
    return WeatherResponse(
        name = this.name,
        main = Main(
            temp = this.temp,
            pressure = this.pressure,
            humidity = this.humidity,
            feels_like = this.temp,
            temp_min = this.temp_min,
            temp_max = this.temp_max,
            sea_level = 0,
            grnd_level = 0
        ),
        weather = listOf(
            Weather(
                description = this.description,
                icon = this.icon
            )
        ),
        wind = Wind(speed = this.speed),
        sys = Sys(country = this.country),
        visibility = 0,
        clouds = Cloud(all = this.all)
    )
}

fun List<ForecastLocal>.toForecastResponse(): ForecastResponse {
    return ForecastResponse(
        list = this.map { forecastLocal ->
            ForecastItem(
                dt_txt = forecastLocal.dt_txt,
                main = Main(
                    temp = forecastLocal.temp,
                    pressure = forecastLocal.pressure,
                    humidity = forecastLocal.humidity,
                    feels_like = forecastLocal.temp,
                    temp_min = forecastLocal.temp_min,
                    temp_max = forecastLocal.temp_max,
                    sea_level = 0,
                    grnd_level = 0
                ),
                weather = listOf(
                    Weather(
                        description = forecastLocal.description,
                        icon = forecastLocal.icon
                    )
                ),
                wind = Wind(speed = forecastLocal.speed),
                clouds = Cloud(all = forecastLocal.all),
                visibility = 0
            )
        }
    )
}
