package com.example.weatherapp.Favourites

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Model.CurrentWeather
import com.example.weatherapp.R
import java.io.Serializable

class WeatherLocationAdapter(
    private var favouriteWeatherLocations: List<CurrentWeather>,
    private val onDeleteClick: (CurrentWeather) -> Unit,
    private val onItemClick: (CurrentWeather) -> Unit
) : RecyclerView.Adapter<WeatherLocationAdapter.WeatherLocationViewHolder>() {

    inner class WeatherLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName: TextView = itemView.findViewById(R.id.placeName)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        val cardView : CardView = itemView.findViewById(R.id.weatherLocationCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherLocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favoutite_item, parent, false)
        return WeatherLocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherLocationViewHolder, position: Int) {
        val weatherLocation = favouriteWeatherLocations[position]


        holder.placeName.text = weatherLocation.name


        holder.deleteButton.setOnClickListener {
            onDeleteClick(weatherLocation)
        }

        holder.cardView.setOnClickListener {
            onItemClick(weatherLocation)
        }
    }

    override fun getItemCount(): Int {
        return favouriteWeatherLocations.size
    }

    fun updateData(newFavouriteWeatherLocations: List<CurrentWeather>) {
        favouriteWeatherLocations = newFavouriteWeatherLocations
        notifyDataSetChanged()
        Log.i("WeatherLocationAdapter", "updateData: $favouriteWeatherLocations")
    }
}
