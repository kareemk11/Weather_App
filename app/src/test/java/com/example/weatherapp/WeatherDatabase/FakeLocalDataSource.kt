package com.example.weatherapp.WeatherDatabase

import com.example.weatherapp.Model.Alert
import com.example.weatherapp.Model.CurrentWeather
import com.example.weatherapp.Model.ForecastLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FakeLocalDataSource(
    var alerts: MutableList<Alert>? = mutableListOf(),
    val currentWeather: MutableList<CurrentWeather>? = mutableListOf()
) : InterfaceWeatherLocalDataSource {
    override suspend fun insertForecast(forecast: ForecastLocal) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllForecasts(): List<ForecastLocal> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllForecasts() {
        TODO("Not yet implemented")
    }

    override suspend fun getForecastByWeatherID(currentWeatherId: Int): Flow<List<ForecastLocal>> {
        TODO("Not yet implemented")
    }

    override suspend fun getForecastDetails(): List<ForecastLocal> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alert: Alert?) {
        if (alerts == null) {
            alerts = mutableListOf()
        }
        alert?.let { alerts?.add(it) }
    }

    override suspend fun getAllAlerts(): Flow<List<Alert>> {
        return flow {
            emit(alerts ?: emptyList())
        }

    }

    override suspend fun deleteAlert(id: Int?) {

        if (alerts == null) {
            alerts = mutableListOf()
        }

        alerts?.removeIf { it.id == id }


    }

    override suspend fun deleteAlertByWorkManagerId(workManagerId: String?) {
        alerts?.removeIf { it.workManagerId == workManagerId }

    }

    override suspend fun insertCurrentWeather(weather: CurrentWeather): Long {
        currentWeather?.add(weather)
        return currentWeather?.size?.toLong() ?: 0
    }

    override suspend fun getCurrentWeather(): CurrentWeather {
        return currentWeather?.get(0) ?: throw Exception("No current weather found")
    }

    override suspend fun deleteCurrentWeather(id: Int?) {

        currentWeather?.removeAt(0)
    }

    override suspend fun getAllFavourites(): Flow<List<CurrentWeather>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavourite(id: Int?) {

        currentWeather?.removeIf { it.id == id }
    }

    override suspend fun insertFavourite(favourite: CurrentWeather) {
        currentWeather?.add(favourite)
    }
}