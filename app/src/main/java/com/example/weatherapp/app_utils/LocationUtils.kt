package com.example.weatherapp.app_utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weatherapp.Home.HomeWeatherViewModel
import com.example.weatherapp.Model.CurrentLocation
import com.example.weatherapp.R
import com.example.weatherapp.MapActivity
import com.google.android.gms.location.*

object LocationUtils {

    private const val TAG = "LocationUtils"
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient



    fun getLocationUsingMaps(context: Context) {
        val intent = Intent(context, MapActivity::class.java)
        context.startActivity(intent)
        val sharedPreferences = context.getSharedPreferences("map_location", Context.MODE_PRIVATE)
        CurrentLocation.latitude = sharedPreferences.getFloat("latitude", 0.0f).toDouble()
        CurrentLocation.longitude = sharedPreferences.getFloat("longitude", 0.0f).toDouble()
    }

     fun checkLocationPermission(context: Context): Boolean {
         Log.i(TAG, "checkLocationPermission:")
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

     fun isLocationEnabled(context: Context): Boolean {
         Log.i(TAG, "isLocationEnabled: ")
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

     fun enableLocation(context: Context) {
        Toast.makeText(context, R.string.enable_location, Toast.LENGTH_SHORT).show()
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    @SuppressLint("MissingPermission")
    fun getFreshLocation(
        activity: AppCompatActivity,
        viewModel: HomeWeatherViewModel,
        onLocationReceived: (latitude: Double, longitude: Double) -> Unit
    ) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
//        fusedLocationProviderClient.requestLocationUpdates(
//            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).build(),
//            object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    super.onLocationResult(locationResult)
//                    for (location in locationResult.locations) {
//                        Log.i(TAG, "Location received: ${location.latitude}, ${location.longitude}")
//                        onLocationReceived(location.latitude, location.longitude)
//
//                    }
//                }
//            },
//            Looper.getMainLooper()
//        )
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(), object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)

                    val location = p0.locations.last()
                    val latitude = location.latitude
                    val longitude = location.longitude
                    onLocationReceived(latitude, longitude)
                    Log.i("Result", "onLocationResult: $latitude $longitude")
                }
            },
            Looper.myLooper()
        )
    }

    fun requestLocationPermissions(activity: AppCompatActivity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
            101
        )
    }
}



