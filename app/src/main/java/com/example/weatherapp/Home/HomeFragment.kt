package com.example.weatherapp.Home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.Model.CurrentLocation

import com.example.weatherapp.Model.SettingsInPlace
import com.example.weatherapp.Model.WeatherRepository
import com.example.weatherapp.Network.WeatherRemoteDataSource
import com.example.weatherapp.Settings.setLocale
import com.example.weatherapp.app_utils.FilterUtils
import com.example.weatherapp.app_utils.SettingsDialogUtils
import com.example.weatherapp.app_utils.SharedPreferencesUtils
import com.example.weatherapp.WeatherDatabase.WeatherLocalDataSource
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.app_utils.LocationUtils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {

    private val TAG = "HomeFragmentLog"

    // Weather
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeWeatherViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WeatherAdapter
    private lateinit var recyclerViewForecast: RecyclerView
    private lateinit var adapterForecast: ForecastAdapter

    // Date & Time
    @RequiresApi(Build.VERSION_CODES.O)
    private val currentDateTime: LocalDateTime = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    @RequiresApi(Build.VERSION_CODES.O)
    private val formattedDateTime = currentDateTime.format(formatter)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val viewModelFactory = HomeWeatherViewModelFactory(
            WeatherRepository.getInstance(
                WeatherLocalDataSource(), WeatherRemoteDataSource.getInstance()
            )
        )
        setupRecyclerViews()
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeWeatherViewModel::class.java)




        val sharedPreferences = requireContext().getSharedPreferences(SharedPreferencesUtils.PREF_NAME, Context.MODE_PRIVATE)
        if (SharedPreferencesUtils.isFirstLaunch(sharedPreferences)) {
            SettingsDialogUtils.showInitialSettingsDialog(requireContext(), sharedPreferences) { locationMethod, notificationsEnabled ->
                SharedPreferencesUtils.saveInitialSettings(requireContext(), locationMethod, notificationsEnabled)
                saveSettingsObject()
            }
        } else {
            saveSettingsObject()
        }



        viewModel.fetchWeatherData()
        viewModel.fetchForecastData()

        viewModel.weatherData.observe(viewLifecycleOwner) {
            updateUI()
        }

        viewModel.forecastData.observe(viewLifecycleOwner) {
            Log.i(TAG, "onViewCreated: $it")
            adapter.updateData(FilterUtils.filterCurrentDayData(it))
            adapterForecast.updateData(FilterUtils.filterDailyData(it))
        }


    }


    private fun setupRecyclerViews() {
        recyclerView = binding.threeHourForecast
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = WeatherAdapter(emptyList())
        recyclerView.adapter = adapter

        recyclerViewForecast = binding.recyclerViewForecast
        recyclerViewForecast.setHasFixedSize(true)
        recyclerViewForecast.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapterForecast = ForecastAdapter(emptyList())
        recyclerViewForecast.adapter = adapterForecast
    }

    private fun saveSettingsObject() {
        val settings = SharedPreferencesUtils.saveSettingsObject(requireContext())
        setLocale(requireActivity() as AppCompatActivity, settings.language)
        SettingsInPlace.apply {
            unit = settings.unit
            language = settings.language
            locationMethod = settings.locationMethod
            notificationsEnabled = settings.notificationsEnabled
            Log.i(TAG, "saveSettingsObject: $unit")
        }

        // Handle location method
        if (settings.locationMethod == "GPS") {

            getLocationUsingGps()
            viewModel.fetchWeatherData()
            viewModel.fetchForecastData()
            Log.i(TAG, "saveSettingsObject: "+ CurrentLocation.latitude.toString())
            Log.i(TAG, "saveSettingsObject: "+ CurrentLocation.longitude.toString())

        } else {
            //LocationUtils.getLocationUsingMaps(requireContext())
        }
    }

    private fun getLocationUsingGps() {
        if (LocationUtils.checkLocationPermission(requireContext())) {
            if (LocationUtils.isLocationEnabled(requireContext())) {
                LocationUtils.getFreshLocation(requireActivity() as AppCompatActivity, viewModel) { lat, lon ->
                    CurrentLocation.latitude = lat
                    CurrentLocation.longitude = lon

                    viewModel.fetchWeatherData()
                    viewModel.fetchForecastData()

                    Log.i(TAG, "Updated Location: Lat: $lat, Lon: $lon")
                }
            } else {
                LocationUtils.enableLocation(requireContext())
            }
        } else {
            LocationUtils.requestLocationPermissions(requireActivity() as AppCompatActivity)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI() {
        if (SettingsInPlace.unit == "metric") {
            binding.tvTemperature.text = "${viewModel.weatherData.value?.main?.temp?.toInt()}°C"
            binding.tvMaxTemp.text = "${viewModel.weatherData.value?.main?.temp_max?.toInt()}°C"
            binding.tvMinTemp.text = "${viewModel.weatherData.value?.main?.temp_min?.toInt()}°C"
            binding.tvWindValue.text = "${viewModel.weatherData.value?.wind?.speed.toString()} m/s"
        } else if (SettingsInPlace.unit == "imperial") {
            binding.tvTemperature.text = "${viewModel.weatherData.value?.main?.temp?.toInt()}°F"
            binding.tvMaxTemp.text = "${viewModel.weatherData.value?.main?.temp_max?.toInt()}°F"
            binding.tvMinTemp.text = "${viewModel.weatherData.value?.main?.temp_min?.toInt()}°F"
            binding.tvWindValue.text = "${viewModel.weatherData.value?.wind?.speed.toString()} m/h"
        } else {
            binding.tvTemperature.text = "${viewModel.weatherData.value?.main?.temp?.toInt()}°K"
            binding.tvMaxTemp.text = "${viewModel.weatherData.value?.main?.temp_max?.toInt()}°K"
            binding.tvMinTemp.text = "${viewModel.weatherData.value?.main?.temp_min?.toInt()}°K"
            binding.tvWindValue.text = "${viewModel.weatherData.value?.wind?.speed.toString()} m/s"
        }

        binding.tvCityName.text = viewModel.weatherData.value?.name
        binding.tvDateTime.text = formattedDateTime
        binding.tvWeatherDescription.text = viewModel.weatherData.value?.weather?.get(0)?.description

        "${viewModel.weatherData.value?.visibility.toString()} m".also {
            binding.tvVisibilityValue.text = it
        }
        "${viewModel.weatherData.value?.main?.humidity}%".also { binding.tvHumidityValue.text = it }
        "${viewModel.weatherData.value?.main?.pressure.toString()} hPa".also {
            binding.tvPressureValue.text = it
        }
        "${viewModel.weatherData.value?.clouds?.all.toString()}%".also {
            binding.tvCloudsValue.text = it
        }

        val iconUrl = "https://openweathermap.org/img/wn/${viewModel.weatherData.value?.weather?.get(0)?.icon}@2x.png"
        Glide.with(this).load(iconUrl).into(binding.ivWeatherIcon)
    }
}
