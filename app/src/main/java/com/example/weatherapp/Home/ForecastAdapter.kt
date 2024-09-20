package com.example.weatherapp.Home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.Model.ForecastItem
import com.example.weatherapp.databinding.ItemForecastDayBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ForecastAdapter(
    private var forecastList: List<ForecastItem>
) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {
    private val TAG = "ForecastAdapter"

    inner class ForecastViewHolder(private val binding: ItemForecastDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(forecastItem: ForecastItem) {
            binding.tvDate.text = getDayName(forecastItem.dt_txt)
            binding.tvTemperature.text = "${forecastItem.main.temp.toInt()}°C"
            binding.tvDescription.text = forecastItem.weather[0].description
            Glide.with(binding.imgWeatherIcon.context)
                .load("https://openweathermap.org/img/wn/${forecastItem.weather[0].icon}@2x.png")
                .into(binding.imgWeatherIcon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding =
            ItemForecastDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {

        holder.bind(forecastList[position])

    }

    override fun getItemCount(): Int {
        return forecastList.size
    }

    fun updateData(newForecastList: List<ForecastItem>) {

        forecastList = newForecastList
        Log.i(TAG, "updateData: $forecastList")
        notifyDataSetChanged()
    }


    fun getDayName(timestamp: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val targetDate = Calendar.getInstance().apply {
            time = dateFormat.parse(timestamp) ?: return "Invalid Date"
        }

        val now = Calendar.getInstance()
        val diffInMillis = targetDate.timeInMillis - now.timeInMillis
        val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

        val isArabic = Locale.getDefault().language == "ar"

        return when (diffInDays) {
            1 -> if (isArabic) "غداً" else "Tomorrow"
            0 -> if (isArabic) "اليوم" else "Today"
            else -> {
                val dayOfWeek = targetDate.get(Calendar.DAY_OF_WEEK)
                val days = if (isArabic) {
                    arrayOf(
                        "الأحد",
                        "الإثنين",
                        "الثلاثاء",
                        "الأربعاء",
                        "الخميس",
                        "الجمعة",
                        "السبت"
                    )
                } else {
                    arrayOf(
                        "Sunday",
                        "Monday",
                        "Tuesday",
                        "Wednesday",
                        "Thursday",
                        "Friday",
                        "Saturday"
                    )
                }
                days[dayOfWeek - 1]
            }
        }
    }



}
