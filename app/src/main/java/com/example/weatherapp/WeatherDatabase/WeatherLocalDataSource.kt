package com.example.weatherapp.WeatherDatabase

import com.example.weatherapp.Model.Alert
import com.example.weatherapp.Model.CurrentWeather
import com.example.weatherapp.Model.ForecastLocal
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(
    private val forecastDao: ForecastDao,
    private val alertDao: AlertDao,
    private val currentWeatherDao: CurrentWeatherDao
) {

    // Forecast Methods
    suspend fun insertForecast(forecast: ForecastLocal) {
        forecastDao.insertForecast(forecast)
    }

    suspend fun getAllForecasts(): List<ForecastLocal> {
        return forecastDao.getAllForecasts()
    }

    suspend fun deleteAllForecasts() {
        forecastDao.deleteAllForecasts()
    }

    suspend fun getForecastByWeatherID(currentWeatherId: Int): Flow<List<ForecastLocal>> {
        return forecastDao.getForecastByWeatherID(currentWeatherId)
    }

    suspend fun getForecastDetails(): List<ForecastLocal> {
        return forecastDao.getForecastDetails()
    }

    // Alert Methods
    suspend fun insertAlert(alert: Alert) {
        alertDao.insertAlert(alert)
    }

    suspend fun getAllAlerts(): Flow<List<Alert>> {
        return alertDao.getAllAlerts()
    }

    suspend fun deleteAlert(id : Int) {
        alertDao.deleteAlertById(id)
    }

    suspend fun deleteAlertByWorkManagerId(workManagerId: String) {
        alertDao.deleteAlertByWorkManagerId(workManagerId)
    }

    // Current Weather Methods
    suspend fun insertCurrentWeather(weather: CurrentWeather):Long {
        return currentWeatherDao.insertCurrentWeather(weather)
    }

//    suspend fun getAllCurrentWeather(): List<CurrentWeather> {
//        return currentWeatherDao.getAllCurrentWeather()
//    }

    suspend fun getCurrentWeather(): CurrentWeather {
        return currentWeatherDao.getCurrentWeather()
    }

    suspend fun deleteCurrentWeather(id : Int) {
        currentWeatherDao.deleteCurrentWeatherById(id)
    }

    suspend fun getAllFavourites(): Flow<List<CurrentWeather>> {
        return currentWeatherDao.getAllCurrentWeather()

    }

    suspend fun deleteFavourite(id: Int) {

        currentWeatherDao.deleteCurrentWeatherById(id)

    }

    suspend fun insertFavourite(favourite: CurrentWeather) {

        currentWeatherDao.insertCurrentWeather(favourite)

    }



}
