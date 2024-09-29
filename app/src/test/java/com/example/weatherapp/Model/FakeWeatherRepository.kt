package com.example.weatherapp.Model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeWeatherRepository(
    var fakeList: MutableList<Alert>?, var fakeList2: MutableList<CurrentWeather>?
) : InterfaceWeatherRepository {


    override suspend fun getAllAlerts(): Flow<List<Alert>> {
        return flow {
            emit(fakeList ?: emptyList())
        }
    }

    override suspend fun insertAlert(alert: Alert?) {
        alert?.let {
            if (fakeList == null) {
                fakeList = mutableListOf()
            }

            if (!fakeList!!.contains(alert)) {
                fakeList!!.add(alert)
            }
        }
    }



    override suspend fun deleteAlert(alert: Alert?) {
        fakeList?.removeIf { it.id == alert?.id }
    }

    override suspend fun deleteFavourite(favourite: CurrentWeather?) {

        fakeList2?.removeIf { it.id == favourite?.id }
    }

    override suspend fun insertFavourite(favourite: CurrentWeather?): Long {
        if (fakeList2 == null) {
            fakeList2 = mutableListOf()
        }
        if (favourite != null) {
            fakeList2?.add(favourite)
        }
        return fakeList2?.size?.toLong() ?: 0
    }


    override suspend fun deleteAlertByWorkManagerId(workManagerId: String?) {
        TODO("Not yet implemented")
    }

    override suspend fun getForecastByWeatherID(currentWeatherId: Int): Flow<List<ForecastLocal>> {
        TODO("Not yet implemented")
    }

    override suspend fun getForecastDetails(): List<ForecastLocal> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentWeatherFromLocal(): CurrentWeather {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFavourites(): Flow<List<CurrentWeather>> {
        return flow {
            emit(fakeList2 ?: emptyList())
        }
    }




    override suspend fun insertForecast(forecast: ForecastLocal) {
        TODO("Not yet implemented")
    }
    override suspend fun getCurrentWeather(
        latitude: Double, longitude: Double, units: String, lang: String, isFavourite: Boolean
    ): Flow<Response<WeatherResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun getFiveDayForecast(
        latitude: Double, longitude: Double, units: String, lang: String, isFavourite: Boolean
    ): Flow<Response<ForecastResponse>> {
        TODO("Not yet implemented")
    }
}