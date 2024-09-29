package com.example.weatherapp.Model

import com.example.weatherapp.Network.InterfaceWeatherRemoteDataSource
import com.example.weatherapp.WeatherDatabase.InterfaceWeatherLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import retrofit2.Response

class WeatherRepository private constructor(
    private val localDataSource: InterfaceWeatherLocalDataSource,
    private val remoteDataSource: InterfaceWeatherRemoteDataSource
) : InterfaceWeatherRepository {
    companion object {
        @Volatile
        private var instance: WeatherRepository? = null

        fun getInstance(
            localDataSource: InterfaceWeatherLocalDataSource, remoteDataSource: InterfaceWeatherRemoteDataSource
        ): WeatherRepository {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepository(localDataSource, remoteDataSource).also {
                    instance = it
                }
            }
        }
    }
//
//    override suspend fun getCurrentWeather(
//        latitude: Double,
//        longitude: Double,
//        units: String,
//        lang: String,
//        isFavourite: Boolean
//    ): Flow<Response<WeatherResponse>> {
//        return remoteDataSource.getCurrentWeather(latitude, longitude, units, lang).transform { response ->
//            if (response.isSuccessful) {
//                response.body()?.let { weatherResponse ->
//                    if (!isFavourite) {
//                        val currentWeather = weatherResponse.toCurrentWeather(latitude, longitude, 1) // Conversion method
//                        localDataSource.insertCurrentWeather(currentWeather)
//                    }
//                    emit(response)
//                } ?: emit(response)
//            } else {
//                emit(response)
//            }
//        }
//    }
suspend fun insertCurrentWeather(currentWeather: CurrentWeather) {

        localDataSource.insertCurrentWeather(currentWeather)

}

    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String,
        isFavourite: Boolean
    ): Flow<Response<WeatherResponse>> {
        //val currentWeather = remoteDataSource.getCurrentWeather(latitude, longitude, units, lang)

        return remoteDataSource.getCurrentWeather(latitude, longitude, units, lang)
    }

//    override suspend fun getFiveDayForecast(
//        latitude: Double,
//        longitude: Double,
//        units: String,
//        lang: String,
//        isFavourite: Boolean
//    ): Flow<Response<ForecastResponse>> {
//        return remoteDataSource.getFiveDayForecast(latitude, longitude, units, lang).transform { response ->
//            if (response.isSuccessful) {
//                response.body()?.let { forecastResponse ->
//                    if (!isFavourite) {
//                        val forecasts = forecastResponse.toForecastLocalList()
//                        forecasts.forEach { forecast ->
//                            localDataSource.insertForecast(forecast)
//                        }
//                    }
//                    emit(response)
//                } ?: emit(response)
//            } else {
//                emit(response)
//            }
//        }
//    }

    override suspend fun getFiveDayForecast(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String,
        isFavourite: Boolean
    ): Flow<Response<ForecastResponse>> {
        return remoteDataSource.getFiveDayForecast(latitude, longitude, units, lang)
    }





    override suspend fun getAllAlerts(): Flow<List<Alert>> {
        return localDataSource.getAllAlerts()
    }

    override suspend fun insertAlert(alert: Alert?) {
        localDataSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: Alert?) {
        localDataSource.deleteAlert(alert?.id)
    }

    override suspend fun deleteAlertByWorkManagerId(workManagerId: String?) {
        localDataSource.deleteAlertByWorkManagerId(workManagerId)
    }


    override suspend fun getForecastByWeatherID(currentWeatherId: Int): Flow<List<ForecastLocal>> {
        return localDataSource.getForecastByWeatherID(currentWeatherId)
    }

    override suspend fun getForecastDetails(): List<ForecastLocal> {
        return localDataSource.getForecastDetails()
    }


    override suspend fun getCurrentWeatherFromLocal(): CurrentWeather {
        return localDataSource.getCurrentWeather()
    }

    override suspend fun getAllFavourites(): Flow<List<CurrentWeather>> {
        return localDataSource.getAllFavourites()
    }

    override suspend fun deleteFavourite(favourite: CurrentWeather?) {
        localDataSource.deleteFavourite(favourite?.id)

    }

    override suspend fun insertFavourite(favourite: CurrentWeather?): Long {
        return favourite?.let { localDataSource.insertCurrentWeather(it) } ?: 0

    }

    override suspend fun insertForecast(forecast: ForecastLocal) {
        localDataSource.insertForecast(forecast)

    }

//    fun getAlerts(): List<Alert>? {
//
//
//    }

}
