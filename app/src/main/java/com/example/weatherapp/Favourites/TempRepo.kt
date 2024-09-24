package com.example.weatherapp.Favourites

object TempRepo {

     val favourites = mutableListOf<FavouriteWeatherObject>()

    fun addFavourite(favourite: FavouriteWeatherObject) {
        favourites.add(favourite)
    }

    fun removeFavourite(favourite: FavouriteWeatherObject) {
        favourites.remove(favourite)
    }
}