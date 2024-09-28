package com.example.weatherapp.Home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Model.CurrentLocation
import com.example.weatherapp.Model.CurrentWeatherState
import com.example.weatherapp.Model.ForecastResponse
import com.example.weatherapp.Model.SettingsInPlace
import com.example.weatherapp.Model.WeatherForecastState
import com.example.weatherapp.Model.WeatherRepository
import com.example.weatherapp.Model.WeatherResponse
import com.example.weatherapp.Model.toForecastResponse
import com.example.weatherapp.Model.toWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeWeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    //Weather Home
    private val TAG = "HomeWeatherViewModelLog"
    private val _weatherData = MutableStateFlow<CurrentWeatherState>(CurrentWeatherState.Loading)
    val weatherData: StateFlow<CurrentWeatherState> = _weatherData
    private val _forecastData = MutableStateFlow<WeatherForecastState>(WeatherForecastState.Loading)
    val forecastData: StateFlow<WeatherForecastState> = _forecastData
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    //Settings


    fun fetchWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
//            val weatherResponse = repository.getCurrentWeather(
//                latitude,
//                longitude,
//                SettingsInPlace.unit,
//                SettingsInPlace.language
//            )
//            if (weatherResponse.isSuccessful) {
//                _weatherData.postValue(weatherResponse.body())
//            } else {
//
//                Log.i(TAG, "fetchWeatherData: " + weatherResponse.errorBody()?.string())
//
//                // Handle error
//            }

            try {
                val weatherResponse = repository.getCurrentWeather(
                    latitude,
                    longitude,
                    SettingsInPlace.unit,
                    SettingsInPlace.language,
                    true)
                _weatherData.value = CurrentWeatherState.Success(weatherResponse.body())


            } catch (
                e: Exception
            ) {
                _weatherData.value = CurrentWeatherState.Error(e.message?: "Unknown error" )

            }
        }

    }

    fun fetchForecastData(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val weatherResponse = repository.getFiveDayForecast(
                    latitude,
                    longitude,
                    SettingsInPlace.unit,
                    SettingsInPlace.language,
                    true)

                _forecastData.value = WeatherForecastState.Success(weatherResponse.body())

            } catch (e: Exception) {

                _forecastData.value = WeatherForecastState.Error(e.message?: "Unknown error")

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
                _forecastData.value = WeatherForecastState.Success(forecastList.toForecastResponse())
            }
//            val forecastList = repository.getForecastByWeatherID(1)
//
//            _forecastData.postValue(forecastList.toForecastResponse())
        }
    }




}

