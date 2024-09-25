package com.example.weatherapp.Home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Model.CurrentLocation
import com.example.weatherapp.Model.ForecastResponse
import com.example.weatherapp.Model.SettingsInPlace
import com.example.weatherapp.Model.WeatherRepository
import com.example.weatherapp.Model.WeatherResponse
import com.example.weatherapp.Model.toForecastResponse
import com.example.weatherapp.Model.toWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeWeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    //Weather Home
    private val TAG = "HomeWeatherViewModelLog"
    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData
    private val _forecastData = MutableLiveData<ForecastResponse>()
    val forecastData: LiveData<ForecastResponse> = _forecastData
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    //Settings


    fun fetchWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherResponse = repository.getCurrentWeather(
                latitude,
                longitude,
                SettingsInPlace.unit,
                SettingsInPlace.language
            )
            Log.i(TAG, "fetchWeatherData: " + CurrentLocation.latitude.toString())
            Log.i(TAG, "fetchWeatherData: " + CurrentLocation.longitude.toString())

            Log.i(TAG, "fetchWeatherData: " + weatherResponse.body())
            if (weatherResponse.isSuccessful) {
                _weatherData.postValue(weatherResponse.body())

            } else {

                Log.i(TAG, "fetchWeatherData: " + weatherResponse.errorBody()?.string())

                // Handle error
            }

        }

    }

    fun fetchForecastData(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val forecastResponse = repository.getFiveDayForecast(
                latitude,
                longitude,
                SettingsInPlace.unit,
                SettingsInPlace.language
            )
            if (forecastResponse.isSuccessful) {
                _forecastData.postValue(forecastResponse.body())
            } else {
                // Handle error
            }
        }

    }

    fun getWeatherDateFromLocal() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentWeather = repository.getCurrentWeatherFromLocal()
            _weatherData.postValue(currentWeather.toWeatherResponse())
        }
    }

    fun getForecastDataFromLocal() {
        viewModelScope.launch(Dispatchers.IO) {
            val forecastList = repository.getForecastByWeatherID(1)
            _forecastData.postValue(forecastList.toForecastResponse())
        }
    }




}

