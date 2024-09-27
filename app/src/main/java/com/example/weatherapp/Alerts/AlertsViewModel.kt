package com.example.weatherapp.Alerts

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Model.Alert
import com.example.weatherapp.Model.ForecastLocal
import com.example.weatherapp.Model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AlertsViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _alerts = MutableLiveData<List<Alert>>()
    val alerts: MutableLiveData<List<Alert>> = _alerts


    fun getAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllAlerts().collect {
                _alerts.postValue(it)
            }
        }
    }

    fun deleteAlert(alert: Alert) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlert(alert)
        }
    }

    fun insertAlert(alert: Alert) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlert(alert)
            Log.d("AlertsViewModel", "Alert inserted: $alert")
        }
    }


    suspend fun getWeatherDetailsFromDatabase(delayMillis: Long): ForecastLocal? {
        val currentTimeMillis = System.currentTimeMillis()
        val targetTimeMillis = currentTimeMillis + delayMillis
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val forecastDetails = repository.getForecastByWeatherID(1)
        val selectedForecast = forecastDetails.find { forecast ->
            val forecastDate = dateFormat.parse(forecast.dt_txt)
            (forecastDate?.time ?: 0L) >= targetTimeMillis
        }

        return selectedForecast
    }


}


