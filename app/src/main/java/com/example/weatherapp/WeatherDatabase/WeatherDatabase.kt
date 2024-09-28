package com.example.weatherapp.WeatherDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.weatherapp.Model.Alert
import com.example.weatherapp.Model.CurrentWeather
import com.example.weatherapp.Model.ForecastLocal


@Database(entities = [ForecastLocal::class, Alert::class, CurrentWeather::class], version = 2, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun forecastDao(): ForecastDao
    abstract fun alertDao(): AlertDao
    abstract fun currentWeatherDao(): CurrentWeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Alert ADD COLUMN workManagerId TEXT")
            }
        }



        fun getInstance(ctx: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}




/*
@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun getProductDao(): ProductsDao
    companion object {
        @Volatile
        private var INSTANCE: ProductDatabase? = null
        fun getInstance(ctx: Context): ProductDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    ProductDatabase::class.java,
                    "product_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
 */