package com.example.weatherapp.Alerts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.weatherapp.Model.Alert
import com.example.weatherapp.Model.ForecastLocal
import com.example.weatherapp.Model.InterfaceWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class AlertsViewModel(private val repository: InterfaceWeatherRepository) : ViewModel() {

    // MutableStateFlow to hold alerts data
    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    fun getAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllAlerts().collectLatest { alertList ->
                _alerts.value = alertList
            }
        }
    }

    fun deleteAlert(alert: Alert, workManager: WorkManager) {
        viewModelScope.launch(Dispatchers.IO) {
            alert.workManagerId?.let { workManagerId ->
                repository.deleteAlertByWorkManagerId(workManagerId)
                workManager.cancelWorkById(UUID.fromString(workManagerId))
            }
        }
    }

    fun insertAlert(alert: Alert) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlert(alert)
        }
    }

    suspend fun getWeatherDetailsFromDatabase(delayMillis: Long): ForecastLocal? {
        val currentTimeMillis = System.currentTimeMillis()
        val targetTimeMillis = currentTimeMillis + delayMillis
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val forecastList = repository.getForecastDetails()
        return forecastList.find { forecastLocal ->
            Log.d("WeatherAlert", "Forecast Date: ${forecastLocal.dt_txt}")
            val forecastDate = dateFormat.parse(forecastLocal.dt_txt)
            (forecastDate?.time ?: 0L) >= targetTimeMillis
        }
    }

    fun deleteAlert(alert: Alert?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlert(alert)
        }
    }
}
