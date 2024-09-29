package com.example.weatherapp.Favourites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Model.CurrentWeather
import com.example.weatherapp.Model.CurrentWeatherState
import com.example.weatherapp.Model.FavouritesState
import com.example.weatherapp.Model.InterfaceWeatherRepository
import com.example.weatherapp.Model.SettingsInPlace
import com.example.weatherapp.Model.WeatherForecastState
import com.example.weatherapp.Model.toCurrentWeather
import com.example.weatherapp.Model.toForecastLocalList
import com.example.weatherapp.Model.toForecastResponse
import com.example.weatherapp.Model.toWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class FavouritesViewModel(private val repository: InterfaceWeatherRepository) : ViewModel() {

    private val _favourites = MutableStateFlow<FavouritesState>(FavouritesState.Loading)
    val favourites: StateFlow<FavouritesState> = _favourites

    private val _weatherResponse =
        MutableStateFlow<CurrentWeatherState>(CurrentWeatherState.Loading)
    val weather: StateFlow<CurrentWeatherState> = _weatherResponse
    private val _forecastResponse =
        MutableStateFlow<WeatherForecastState>(WeatherForecastState.Loading)
    val forecast: StateFlow<WeatherForecastState> = _forecastResponse
    private val _searchQuery = MutableSharedFlow<String>(replay = 1)
    val searchQuery: SharedFlow<String> = _searchQuery

    init {
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _favourites.value = FavouritesState.Loading
            try {
                searchQuery.collectLatest { query ->
                    repository.getAllFavourites().collect { allFavourites ->
                        val filteredFavourites = allFavourites.filter {
                            it.name.startsWith(query, ignoreCase = true)
                        }
                        _favourites.value = FavouritesState.Success(filteredFavourites)
                    }
                }
            } catch (e: Exception) {
                _favourites.value = FavouritesState.Error(e.message ?: "Unknown error")
            }
        }
    }




    suspend fun updateSearchQuery(query: String) {
        _searchQuery.emit(query)
    }

    fun fetchFavourites() {
        viewModelScope.launch(Dispatchers.IO) {
            _favourites.value = FavouritesState.Loading
            try {
                repository.getAllFavourites().collect {
                    _favourites.value = FavouritesState.Success(it)
                }
            } catch (e: Exception) {
                _favourites.value = FavouritesState.Error(e.message ?: "Unknown error")
            }
        }
    }



    fun deleteFavourite(favourite: CurrentWeather?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavourite(favourite)
        }
    }


    fun saveFavouriteLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            var id: Long = 0

            // Collecting the flow for current weather
            repository.getCurrentWeather(
                latitude, longitude, SettingsInPlace.unit, SettingsInPlace.language, true
            ).collect { response ->
                if (response.isSuccessful) {
                    val currentWeather = response.body()
                    if (currentWeather != null) {
                        id = repository.insertFavourite(
                            currentWeather.toCurrentWeather(latitude, longitude)
                        )
                    }
                } else {
                    // Handle error if needed
                }

                if (id > 0) {
                    repository.getFiveDayForecast(
                        latitude, longitude, SettingsInPlace.unit, SettingsInPlace.language, true
                    ).collect { forecastResponse ->
                        if (forecastResponse.isSuccessful) {
                            val forecastResponseTemp = forecastResponse.body()
                            if (forecastResponseTemp != null) {
                                val forecasts = forecastResponseTemp.toForecastLocalList(id.toInt())

                                forecasts.forEach { forecast ->
                                    forecast.currentWeatherId = id.toInt()
                                    repository.insertForecast(forecast)
                                    Log.i("FavouritesViewModel", "Inserted forecast: $forecast")
                                }
                                Log.i("FavouritesViewModel", "Inserted ${forecasts.size} forecast entries")
                            }
                        } else {
                            Timber.tag("FavouritesViewModel")
                                .i("Error fetching forecast: %s", forecastResponse.errorBody())
                        }
                    }
                } else {
                    Log.i(
                        "FavouritesViewModel",
                        "Current weather insertion failed, skipping forecast insertion."
                    )
                }
            }
        }
    }

    fun fetchWeatherDataFavourites(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Collecting the flow for current weather
                repository.getCurrentWeather(
                    latitude, longitude, SettingsInPlace.unit, SettingsInPlace.language, true
                ).collect { response ->
                    if (response.isSuccessful) {
                        _weatherResponse.value = CurrentWeatherState.Success(response.body())
                    } else {
                        _weatherResponse.value = CurrentWeatherState.Error(response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            } catch (e: Exception) {
                _weatherResponse.value = CurrentWeatherState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchForecastDataFavourites(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Collecting the flow for five-day forecast
                repository.getFiveDayForecast(
                    latitude, longitude, SettingsInPlace.unit, SettingsInPlace.language, true
                ).collect { forecastResponse ->
                    if (forecastResponse.isSuccessful) {
                        _forecastResponse.value = WeatherForecastState.Success(forecastResponse.body())
                    } else {
                        _forecastResponse.value = WeatherForecastState.Error(forecastResponse.errorBody()?.string() ?: "Unknown error")
                    }
                }
            } catch (e: Exception) {
                _forecastResponse.value = WeatherForecastState.Error(e.message ?: "Unknown error")
            }
        }
    }


    fun getFavouriteForecastDataFromLocal(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getForecastByWeatherID(id).collect {
                _forecastResponse.value = WeatherForecastState.Success(it.toForecastResponse())
            }
        }
    }


    fun getFavouriteWeatherDateFromLocal(favourite: CurrentWeather?) {
        if (favourite == null) {
            return
        }
        _weatherResponse.value = CurrentWeatherState.Success(favourite.toWeatherResponse())
    }

}