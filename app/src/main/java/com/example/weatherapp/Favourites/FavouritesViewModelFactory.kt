package com.example.weatherapp.Favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.Model.WeatherRepository

class FavouritesViewModelFactory (private val repository: WeatherRepository):ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return if ( modelClass.isAssignableFrom(FavouritesViewModel::class.java)) {
            FavouritesViewModel(repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }



}