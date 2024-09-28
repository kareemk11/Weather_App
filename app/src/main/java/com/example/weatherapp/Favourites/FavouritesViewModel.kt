package com.example.weatherapp.Favourites

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Model.CurrentWeather
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

class FavouritesViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _favourites = MutableLiveData<List<CurrentWeather>>()
    val favourites: MutableLiveData<List<CurrentWeather>> = _favourites
    //private val _weatherResponse = MutableLiveData<WeatherResponse>()
    private val _weatherResponse = MutableStateFlow<CurrentWeatherState>(CurrentWeatherState.Loading)
    val weather:StateFlow<CurrentWeatherState> = _weatherResponse
    private val _forecastResponse =MutableStateFlow<WeatherForecastState>(WeatherForecastState.Loading)
    val forecast: StateFlow<WeatherForecastState> = _forecastResponse

    fun fetchFavourites() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllFavourites().collect {
                _favourites.postValue(it)
                //Log.i("FavouritesViewModel", "fetchFavourites: ${it.size}")
            }


        }
    }

    fun deleteFavourite(favourite: CurrentWeather) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavourite(favourite)
        }
    }

//    fun insertFavourite(favourite: CurrentWeather) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.insertFavourite(favourite)
//
//        }
//    }

    fun saveFavouriteLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            var id: Long = 0

            Log.i("FavouritesViewModel", "saveFavouriteLocation: $latitude $longitude")

            val response = repository.getCurrentWeather(
                latitude,
                longitude,
                SettingsInPlace.unit,
                SettingsInPlace.language,
                true)
            if (response.isSuccessful) {
                val currentWeather = response.body()
                if (currentWeather != null) {
                    id = repository.insertFavourite(
                        currentWeather.toCurrentWeather(
                            latitude,
                            longitude
                        )
                    )
                    Log.i("FavouritesViewModel", "Inserted Favourite ID: $id")
                }
            } else {
                Log.i("FavouritesViewModel", "Error fetching current weather: ${response.errorBody()}")
            }

            Log.i("FavouritesViewModel", "Inserted Favourite ID: $id")

            if (id > 0) {
                val forecastResponse = repository.getFiveDayForecast(
                    latitude,
                    longitude,
                    SettingsInPlace.unit,
                    SettingsInPlace.language,
                    true)
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
                    Log.i("FavouritesViewModel", "Error fetching forecast: ${forecastResponse.errorBody()}")
                }
            } else {
                Log.i("FavouritesViewModel", "Current weather insertion failed, skipping forecast insertion.")
            }
        }
    }


    fun fetchWeatherDataFavourites(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {

//            val weatherResponse = repository.getCurrentWeather(
//                latitude,
//                longitude,
//                SettingsInPlace.unit,
//                SettingsInPlace.language,
//                true,
//            )
//            if (weatherResponse.isSuccessful) {
//                _weatherResponse.postValue(weatherResponse.body())
//
//            } else {
//                // Handle error
//            }

            try {
                val weatherResponse = repository.getCurrentWeather(
                    latitude,
                    longitude,
                    SettingsInPlace.unit,
                    SettingsInPlace.language,
                    true)
                _weatherResponse.value =CurrentWeatherState.Success(weatherResponse.body())


            } catch (
                e: Exception
            ) {
                _weatherResponse.value = CurrentWeatherState.Error(e.message?: "Unknown error" )

            }



        }

    }

    fun fetchForecastDataFavourites(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
//            val forecastResponse = repository.getFiveDayForecast(
//                latitude,
//                longitude,
//                SettingsInPlace.unit,
//                SettingsInPlace.language,
//                true
//            )
//            if (forecastResponse.isSuccessful) {
//                _forecastResponse.postValue(forecastResponse.body())
//            } else {
//                // Handle error
//            }
            try {

                val weatherResponse = repository.getFiveDayForecast(
                    latitude,
                    longitude,
                    SettingsInPlace.unit,
                    SettingsInPlace.language,
                    true)

                _forecastResponse.value = WeatherForecastState.Success(weatherResponse.body())

            } catch (e: Exception) {

                _forecastResponse.value = WeatherForecastState.Error(e.message?: "Unknown error")

            }
        }

    }

    fun getFavouriteForecastDataFromLocal(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getForecastByWeatherID(id).collect{
                _forecastResponse.value = WeatherForecastState.Success(it.toForecastResponse())
            }
        }
    }

    fun getFavouriteWeatherDateFromLocal(favourite: CurrentWeather) {

        _weatherResponse.value = CurrentWeatherState.Success(favourite.toWeatherResponse())
        //_weatherResponse.value = favourite.toWeatherResponse()
    }
}