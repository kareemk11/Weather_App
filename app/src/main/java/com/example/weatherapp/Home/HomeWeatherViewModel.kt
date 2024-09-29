package com.example.weatherapp.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Model.CurrentWeatherState
import com.example.weatherapp.Model.SettingsInPlace
import com.example.weatherapp.Model.WeatherForecastState
import com.example.weatherapp.Model.WeatherRepository
import com.example.weatherapp.Model.toCurrentWeather
import com.example.weatherapp.Model.toForecastLocalList
import com.example.weatherapp.Model.toForecastResponse
import com.example.weatherapp.Model.toWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeWeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableStateFlow<CurrentWeatherState>(CurrentWeatherState.Loading)
    val weatherData: StateFlow<CurrentWeatherState> = _weatherData
    private val _forecastData = MutableStateFlow<WeatherForecastState>(WeatherForecastState.Loading)
    val forecastData: StateFlow<WeatherForecastState> = _forecastData


    //Settings


    fun fetchWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCurrentWeather(
                latitude, longitude, SettingsInPlace.unit, SettingsInPlace.language, true
            ).collect { weatherResponse ->
                if (weatherResponse.isSuccessful) {
                    _weatherData.value = CurrentWeatherState.Success(weatherResponse.body())
                    weatherResponse.body()?.toCurrentWeather(latitude, longitude, id = 1 )
                        ?.let { repository.insertCurrentWeather(currentWeather = it) }
                } else {
                    _weatherData.value = CurrentWeatherState.Error(weatherResponse.errorBody()?.string() ?: "Unknown error")
                }
            }
        }
    }

    fun fetchForecastData(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFiveDayForecast(
                latitude, longitude, SettingsInPlace.unit, SettingsInPlace.language, true
            ).collect { forecastResponse ->
                if (forecastResponse.isSuccessful) {
                    _forecastData.value = WeatherForecastState.Success(forecastResponse.body())
                    val localForecastList = forecastResponse.body()?.toForecastLocalList()
                    localForecastList?.forEach { forecast ->
                        repository.insertForecast(forecast)
                    }
                } else {
                    _forecastData.value = WeatherForecastState.Error(forecastResponse.errorBody()?.string() ?: "Unknown error")
                }
            }
        }
    }


    fun getWeatherDateFromLocal() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentWeather = repository.getCurrentWeatherFromLocal()
            _weatherData.value = CurrentWeatherState.Success(currentWeather.toWeatherResponse())
        }
    }

    fun getForecastDataFromLocal() {
        viewModelScope.launch(Dispatchers.IO) {

            repository.getForecastByWeatherID(1).collect { forecastList ->
                _forecastData.value =
                    WeatherForecastState.Success(forecastList.toForecastResponse())
            }
        }
    }


}

