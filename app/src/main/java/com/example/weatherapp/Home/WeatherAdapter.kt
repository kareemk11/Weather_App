package com.example.weatherapp.Home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.Model.ForecastItem
import com.example.weatherapp.Model.SettingsInPlace
import com.example.weatherapp.databinding.ThreeHourRowBinding
import java.text.NumberFormat
import java.util.Locale

class WeatherAdapter(private var weatherList: List<ForecastItem>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    inner class WeatherViewHolder(private val binding: ThreeHourRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(weatherItem: ForecastItem) {
            with(binding) {

                weatherTime.text = calcTime(weatherItem.dt_txt)
                if (SettingsInPlace.unit == "metric"){
                    weatherTemp.text = "${weatherItem.main.temp.toInt()}°C"

                }
                else if (SettingsInPlace.unit == "imperial")
                {
                    weatherTemp.text = "${weatherItem.main.temp.toInt()}°F"
                }
                else {
                    weatherTemp.text = "${weatherItem.main.temp.toInt()}°K"
                }

                weatherDescription.text = weatherItem.weather[0].description

                val iconUrl = "https://openweathermap.org/img/wn/${weatherItem.weather[0].icon}@2x.png"
                Glide.with(itemView.context)
                    .load(iconUrl)
                    .into(weatherIcon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ThreeHourRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weatherItem = weatherList[position]
        holder.bind(weatherItem)
    }

    override fun getItemCount(): Int = weatherList.size

    fun updateData(newWeatherList: List<ForecastItem>) {
       weatherList = newWeatherList
        notifyDataSetChanged()
    }



    fun calcTime(time: String): String {
        val timeIn24Format = time.split(" ")[1]
        val hour = timeIn24Format.split(":")[0].toInt()

        val isArabic = Locale.getDefault().language == "ar"

        val suffix = if (hour >= 12) {
            if (isArabic) "م" else "PM"
        } else {
            if (isArabic) "ص" else "AM"
        }

        val hourIn12Format = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }

        val numberFormat = if (isArabic) {
            NumberFormat.getInstance(Locale("ar"))
        } else {
            NumberFormat.getInstance(Locale.getDefault())
        }

        val formattedHour = numberFormat.format(hourIn12Format)

        return "$formattedHour $suffix"
    }


}
