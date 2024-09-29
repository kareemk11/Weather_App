package com.example.weatherapp.WeatherDatabase

import com.example.weatherapp.Model.Alert
import com.example.weatherapp.Model.CurrentWeather
import com.example.weatherapp.Model.ForecastLocal
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(
    private val forecastDao: ForecastDao,
    private val alertDao: AlertDao,
    private val currentWeatherDao: CurrentWeatherDao
) : InterfaceWeatherLocalDataSource {

    // Forecast Methods
    override suspend fun insertForecast(forecast: ForecastLocal) {
        forecastDao.insertForecast(forecast)
    }

    override suspend fun getAllForecasts(): List<ForecastLocal> {
        return forecastDao.getAllForecasts()
    }

    override suspend fun deleteAllForecasts() {
        forecastDao.deleteAllForecasts()
    }

    override suspend fun getForecastByWeatherID(currentWeatherId: Int): Flow<List<ForecastLocal>> {
        return forecastDao.getForecastByWeatherID(currentWeatherId)
    }

    override suspend fun getForecastDetails(): List<ForecastLocal> {
        return forecastDao.getForecastDetails()
    }

    // Alert Methods
    override suspend fun insertAlert(alert: Alert?) {
        if (alert != null) {
            alertDao.insertAlert(alert)
        }
    }

    override suspend fun getAllAlerts(): Flow<List<Alert>> {
        return alertDao.getAllAlerts()
    }

    override suspend fun deleteAlert(id : Int?) {
        if (id != null) {
            alertDao.deleteAlertById(id)
        }
    }

    override suspend fun deleteAlertByWorkManagerId(workManagerId: String?) {
        if (workManagerId != null) {
            alertDao.deleteAlertByWorkManagerId(workManagerId)
        }
    }

    // Current Weather Methods
    override suspend fun insertCurrentWeather(weather: CurrentWeather):Long {
        return currentWeatherDao.insertCurrentWeather(weather)
    }



    override suspend fun getCurrentWeather(): CurrentWeather {
        return currentWeatherDao.getCurrentWeather()
    }

    override suspend fun deleteCurrentWeather(id : Int?) {

        id?.let { currentWeatherDao.deleteCurrentWeatherById(it) }
    }

    override suspend fun getAllFavourites(): Flow<List<CurrentWeather>> {
        return currentWeatherDao.getAllCurrentWeather()

    }

    override suspend fun deleteFavourite(id: Int?) {

        id?.let { currentWeatherDao.deleteCurrentWeatherById(it) }

    }

    override suspend fun insertFavourite(favourite: CurrentWeather) {

        currentWeatherDao.insertCurrentWeather(favourite)

    }



}
