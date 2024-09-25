package com.example.weatherapp.WeatherDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.Model.Alert
import com.example.weatherapp.Model.CurrentWeather
import com.example.weatherapp.Model.ForecastLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: ForecastLocal)

    @Query("SELECT * FROM ForecastLocal")
    suspend fun getAllForecasts(): List<ForecastLocal>

    @Query("DELETE FROM ForecastLocal")
    suspend fun deleteAllForecasts()

    @Query("SELECT * FROM ForecastLocal WHERE currentWeatherId = :currentWeatherId")
    suspend fun getForecastByWeatherID(currentWeatherId: Int): List<ForecastLocal>
}


@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: Alert)

    @Query("SELECT * FROM Alert")
    suspend fun getAllAlerts(): List<Alert>


    @Query("DELETE FROM Alert WHERE id = :alertId")
    suspend fun deleteAlertById(alertId: Int)
}

@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weather: CurrentWeather):Long

    //    @Query("SELECT * FROM CurrentWeather where id != :1")
//    suspend fun getAllCurrentWeather(): List<CurrentWeather>
    @Query("SELECT * FROM CurrentWeather WHERE id != 1")
     fun getAllCurrentWeather(): Flow<List<CurrentWeather>>

    @Query("SELECT * FROM CurrentWeather WHERE id = 1")
    suspend fun getCurrentWeather(): CurrentWeather


    @Query("DELETE FROM CurrentWeather WHERE id = :currentWeatherId")
    suspend fun deleteCurrentWeatherById(currentWeatherId: Int)
}
