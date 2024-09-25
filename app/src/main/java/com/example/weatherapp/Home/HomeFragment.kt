package com.example.weatherapp.Home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapp.utils.NotificationUtils
import com.example.weatherapp.BrodcastRecievers.LocationReceiver
import com.example.weatherapp.MapActivity
import com.example.weatherapp.Model.CurrentLocation
import com.example.weatherapp.Model.SettingsInPlace
import com.example.weatherapp.Model.WeatherRepository
import com.example.weatherapp.Network.WeatherRemoteDataSource
import com.example.weatherapp.Settings.setLocale
import com.example.weatherapp.WeatherDatabase.WeatherDatabase
import com.example.weatherapp.WeatherDatabase.WeatherLocalDataSource
import com.example.weatherapp.app_utils.FilterUtils
import com.example.weatherapp.app_utils.InternetConnectionUtil
import com.example.weatherapp.app_utils.SettingsDialogUtils
import com.example.weatherapp.app_utils.SharedPreferencesUtils
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val LOCATION_SETTINGS_REQUEST = 1001
    private val LOCATION_PERMISSION_REQUEST_CODE = 101
    private lateinit var locationReceiver: BroadcastReceiver


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
                WeatherLocalDataSource(
                    WeatherDatabase.getInstance(requireContext()).forecastDao(),
                    WeatherDatabase.getInstance(requireContext()).alertDao(),
                    WeatherDatabase.getInstance(requireContext()).currentWeatherDao()

                ), WeatherRemoteDataSource.getInstance()
            )
        )
        setupRecyclerViews()
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeWeatherViewModel::class.java)


        viewModel.weatherData.observe(viewLifecycleOwner) {
            updateUI()
        }

        viewModel.forecastData.observe(viewLifecycleOwner) {
            adapter.updateData(FilterUtils.filterCurrentDayData(it))
            adapterForecast.updateData(FilterUtils.filterDailyData(it))
        }
        binding.fabChangeLocation.setOnClickListener {
            val intent = Intent(activity, MapActivity::class.java)
            intent.putExtra("isFavourite", false)
            startActivity(intent)
        }
        binding.group.visibility = View.GONE

    }

    override fun onResume() {
        super.onResume()

        locationReceiver = LocationReceiver()
        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        requireActivity().registerReceiver(locationReceiver, intentFilter)
    }
    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(locationReceiver)
    }

    override fun onStart() {
        super.onStart()

        val sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesUtils.PREF_NAME,
            Context.MODE_PRIVATE
        )

        if (SharedPreferencesUtils.isFirstLaunch(sharedPreferences)) {
            SettingsDialogUtils.showInitialSettingsDialog(
                requireContext(),
                sharedPreferences
            ) { locationMethod, notificationsEnabled ->

                SharedPreferencesUtils.saveInitialSettings(
                    requireContext(),
                    locationMethod,
                    notificationsEnabled
                )
                SharedPreferencesUtils.saveSettingsObject(requireContext())
                handleLocationAndPermissions()
                NotificationUtils.handleNotificationsPermissions(requireContext())
            }
        } else {
            SharedPreferencesUtils.saveSettingsObject(requireContext())
            handleLocationAndPermissions()
            setLocale(requireActivity() as AppCompatActivity, SettingsInPlace.language)
        }
    }

    private fun handleLocationAndPermissions() {
        if (SettingsInPlace.locationMethod == "GPS") {
            binding.fabChangeLocation.visibility = View.GONE
            if (checkLocationPermission()) {
                Log.i(TAG, "handleLocationAndPermissions: ")
                checkIfLocationEnabled()
            } else {
                requestPermission()
            }
        } else {
            binding.fabChangeLocation.visibility = View.VISIBLE
            binding.progressBar2.visibility = View.GONE
            val sharedPreferences =
                requireContext().getSharedPreferences("map_preferences", Context.MODE_PRIVATE)
            val latitude = sharedPreferences.getFloat("latitude", -1.0f)
            val longitude = sharedPreferences.getFloat("longitude", -1.0f)
            if (latitude != -1.0f && longitude != -1.0f) {
                if(InternetConnectionUtil.isInternetAvailable(requireContext()))
                {
                    viewModel.fetchWeatherData(latitude.toDouble(), longitude.toDouble())
                    viewModel.fetchForecastData(latitude.toDouble(), longitude.toDouble())
                } else {
                    Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
                    // Handle the case when there is no internet connection get data from local database
                    viewModel.getWeatherDateFromLocal()
                    viewModel.getForecastDataFromLocal()
                }

            } else {
                startActivity(Intent(requireContext(), MapActivity::class.java))
            }
        }
    }

    private fun requestPermission() {
        Log.i(TAG, "requestPermission: ")
        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun checkIfLocationEnabled() {
        Log.i(TAG, "checkIfLocationEnabled: ")
        if (isLocationEnabled()) {
            Log.i(TAG, "checkIfLocationEnabled: Enabled")
            getFreshLocation()
        } else {
            Log.i(TAG, "checkIfLocationEnabled: Disabled")
            enableLocation()
        }
    }


    private fun refreshActivity() {
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionsResult: ")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkIfLocationEnabled()
                Log.i(TAG, "onRequestPermissionsResult: ")
            } else {
                Toast.makeText(
                    context,
                    "Location permission is required for weather updates",
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.getWeatherDateFromLocal()
                viewModel.getForecastDataFromLocal()
            }
        }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        Log.d(TAG, "GPS Enabled: $gpsEnabled, Network Enabled: $networkEnabled")
        return gpsEnabled || networkEnabled
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        if(InternetConnectionUtil.isInternetAvailable(requireContext()))
        {
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.Builder(0).apply {
                    setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                }.build(), object : LocationCallback() {
                    override fun onLocationResult(p0: LocationResult) {
                        super.onLocationResult(p0)

                        val location = p0.locations.last()
                        CurrentLocation.latitude = location.latitude
                        CurrentLocation.longitude = location.longitude
                        val latitude = location.latitude
                        val longitude = location.longitude
                        viewModel.fetchWeatherData(latitude, longitude)
                        viewModel.fetchForecastData(latitude, longitude)
                        binding.progressBar2.visibility = View.GONE
                        binding.group.visibility = View.GONE
                        Log.i("Result", "onLocationResult: $latitude $longitude")
                        fusedLocationProviderClient.removeLocationUpdates(this)
                    }
                },
                Looper.myLooper()
            )

        } else {
            Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
            // Handle the case when there is no internet connection get data from local database
            binding.progressBar2.visibility = View.GONE
            binding.group.visibility = View.GONE
            viewModel.getWeatherDateFromLocal()
            viewModel.getForecastDataFromLocal()
        }

    }

    private fun checkLocationPermission(): Boolean {
        Log.i(TAG, "checkLocationPermission: ")
        return requireContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                requireContext().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    private fun enableLocation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Location Services Not Enabled")
            .setMessage("Please enable location services to get weather updates for your current location.")
            .setPositiveButton("Enable") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                hideAllViews()
            }
            .show()
    }


    private fun setupRecyclerViews() {
        recyclerView = binding.threeHourForecast
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = WeatherAdapter(emptyList())
        recyclerView.adapter = adapter

        recyclerViewForecast = binding.recyclerViewForecast
        recyclerViewForecast.setHasFixedSize(true)
        recyclerViewForecast.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapterForecast = ForecastAdapter(emptyList())
        recyclerViewForecast.adapter = adapterForecast
    }



    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_SETTINGS_REQUEST) {
            refreshActivity()
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
        binding.tvWeatherDescription.text =
            viewModel.weatherData.value?.weather?.get(0)?.description

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

        val iconUrl =
            "https://openweathermap.org/img/wn/${viewModel.weatherData.value?.weather?.get(0)?.icon}@2x.png"
        Glide.with(this).load(iconUrl).into(binding.ivWeatherIcon)
    }

    private fun hideAllViews() {
        binding.group.visibility = View.VISIBLE
        binding.progressBar2.visibility = View.GONE
        binding.fabChangeLocation.visibility = View.GONE
        binding.tvTemperature.visibility = View.GONE
        binding.tvCityName.visibility = View.GONE
        binding.tvDateTime.visibility = View.GONE
        binding.tvWeatherDescription.visibility = View.GONE
        binding.cardMainWeather.visibility = View.GONE
        binding.cardWeatherDetails.visibility = View.GONE
        binding.threeHourForecast.visibility = View.GONE
        binding.recyclerViewForecast.visibility = View.GONE
        binding.tvCloudsLabel.visibility = View.GONE
        binding.ivClouds.visibility = View.GONE
        binding.tvCloudsValue.visibility = View.GONE
        binding.tvWindLabel.visibility = View.GONE
        binding.ivWind.visibility = View.GONE
        binding.tvWindValue.visibility = View.GONE
        binding.tvPressureLabel.visibility = View.GONE
        binding.ivPressure.visibility = View.GONE
        binding.tvPressureValue.visibility = View.GONE
        binding.tvVisibilityLabel.visibility = View.GONE
        binding.ivVisibility.visibility = View.GONE
        binding.tvVisibilityValue.visibility = View.GONE
        binding.tvHumidityLabel.visibility = View.GONE
        binding.ivHumidity.visibility = View.GONE
        binding.tvHumidityValue.visibility = View.GONE
        binding.tvMaxTemp.visibility = View.GONE
        binding.tvMinTemp.visibility = View.GONE


    }
//
//    fun broadcastReceiver() {
//        locationReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent?) {
//                if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
//                    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//                    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//
//                    if (!isGpsEnabled && !isNetworkEnabled) {
//                        Toast.makeText(context, "Location services are disabled", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(context, "Location services are enabled", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//    }

}


//private fun fetchLocation() {
//    if (LocationUtils.isLocationEnabled(requireContext())) {
//        LocationUtils.getFreshLocation(requireActivity() as AppCompatActivity, viewModel) { lat, lon ->
//
//            CurrentLocation.latitude = lat
//            CurrentLocation.longitude = lon
//            viewModel.fetchWeatherData()
//            viewModel.fetchForecastData()
//
//            Log.i(TAG, "Updated Location: Lat: $lat, Lon: $lon")
//        }
//    } else {
//        LocationUtils.enableLocation(requireContext())
//    }
//}


//    private fun showLocationPermissionExplanation() {
////        AlertDialog.Builder(requireContext())
////            .setTitle("Location Permission Required")
////            .setMessage("We need location permission to provide accurate weather information for your area.")
////            .setPositiveButton("Grant Permission") { _, _ ->
////                requestLocationPermission()
////            }
////            .setNegativeButton("Cancel") { dialog, _ ->
////                dialog.dismiss()
////                // Handle the case when the user doesn't grant permission
////                handlePermissionDenied()
////            }
////            .show()
////    }
//
//
//    companion object {
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 101
//    }
//
//    private fun handleLocationDisabled() {
//        // Implement fallback behavior when location is disabled
//        // For example, ask the user to enter a location manually
//    }
//
//    private fun handlePermissionDenied() {
//        // Implement fallback behavior when permission is denied
//        // For example, ask the user to enter a location manually
//    }
//
//
//    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
//        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
//        try {
//            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
//            if (addresses != null && addresses.isNotEmpty()) {
//                val address: Address = addresses[0]
//                val addressLine: String = address.getAddressLine(0)
//
//
//            } else {
//
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//
//        }
//    }


//private fun promptEnableLocation() {
//    AlertDialog.Builder(requireContext())
//        .setTitle("Location Services Not Enabled")
//        .setMessage("Please enable location services to get weather updates for your current location.")
//        .setPositiveButton("Enable") { _, _ ->
//            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            startActivityForResult(intent, LOCATION_SETTINGS_REQUEST)
//        }
//        .setNegativeButton("Use Default Location") { _, _ ->
//            useDefaultLocation()
//        }
//        .show()
//}

//private fun useDefaultLocation() {
//
//        CurrentLocation.latitude = 40.7128
//        CurrentLocation.longitude = -74.0060
//        updateWeather()
//    }
//
//    private fun updateWeather() {
//        viewModel.fetchWeatherData()
//        viewModel.fetchForecastData()
//    }


/*
      if (settings.locationMethod == "GPS") {
           binding.fabChangeLocation.visibility = View.GONE
           if (checkLocationPermission()) {
               if (isLocationEnabled()) {
                   getFreshLocation()
               } else {
                   enableLocation()
               }
           } else {
               ActivityCompat.requestPermissions(
                   requireActivity(),
                   arrayOf(
                       android.Manifest.permission.ACCESS_FINE_LOCATION,
                       android.Manifest.permission.ACCESS_COARSE_LOCATION
                   ), LOCATION_PERMISSION_REQUEST_CODE
               )
           }
       } else {
           binding.fabChangeLocation.visibility = View.VISIBLE
           val sharedPreferences =
               requireContext().getSharedPreferences("map_preferences", Context.MODE_PRIVATE)
           val latitude = sharedPreferences.getFloat("latitude", -1.0f)
           val longitude = sharedPreferences.getFloat("longitude", -1.0f)
           if (latitude != -1.0f && longitude != -1.0f) {
               CurrentLocation.latitude = latitude.toDouble()
               CurrentLocation.longitude = longitude.toDouble()
               viewModel.fetchWeatherData()
               viewModel.fetchForecastData()
           } else {
               startActivity(Intent(requireContext(), MapActivity::class.java))
           }
       }

    */
