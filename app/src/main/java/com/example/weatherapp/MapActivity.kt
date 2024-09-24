package com.example.weatherapp

import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.Favourites.FavouriteWeatherObject
import com.example.weatherapp.Favourites.TempRepo
import com.example.weatherapp.Model.FavouriteLocation
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.io.IOException
import java.util.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var selectedLatLng: LatLng? = null
    private lateinit var confirmButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private val TAG = "MapActivityLog"


    override fun onCreate(savedInstanceState: Bundle?) {
        Places.initialize(applicationContext, BuildConfig.API_KEY);
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("map_preferences", MODE_PRIVATE)
        setContentView(R.layout.activity_map)
        confirmButton = findViewById(R.id.confirmLocationButton)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Log.i(TAG, "onCreate: " + intent.getBooleanExtra("isFavourite", false))
        confirmButton.setOnClickListener {
            if (selectedLatLng != null) {
                val isFavourites = intent.getBooleanExtra("isFavourite", false)
                if (isFavourites) {

                    //save selected location to database TODO






                    Log.i(TAG, "onCreate: " + selectedLatLng!!.latitude.toString())
                    val cityName = getAddressFromCoordinates(selectedLatLng!!.latitude, selectedLatLng!!.longitude)
                    TempRepo.addFavourite(FavouriteWeatherObject(cityName,selectedLatLng!!.latitude, selectedLatLng!!.longitude))
                    Log.i(TAG, "onCreate: " + TempRepo.favourites.size)
                    Log.i(TAG, "onCreate: " + FavouriteLocation.name)
                    finish()
                }
                else {
                    val editor = sharedPreferences.edit()
                    editor.putFloat("latitude", selectedLatLng!!.latitude.toFloat())
                    editor.putFloat("longitude", selectedLatLng!!.longitude.toFloat())
                    editor.apply()
                    finish()

                }

            } else {
                Log.w(TAG, "No location selected.")
            }
        }


        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val selectedPlaceLatLng = place.latLng
                selectedPlaceLatLng?.let {
                    moveToPlace(it)
                }
            }

            override fun onError(status: Status) {
                Log.e("AutocompleteError", "An error occurred: $status")
            }
        })

    }

    private fun moveToPlace(latLng: LatLng) {
        if (::mMap.isInitialized) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            selectedLatLng = latLng
        }
    }






    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener { latLng ->
            selectedLatLng = latLng
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
        }

        val initialLocation = LatLng(-34.0, 151.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10f))
    }




        private fun getAddressFromCoordinates(latitude: Double, longitude: Double):String {
            val geocoder = Geocoder(this, Locale.getDefault())
            var address = Address(Locale.getDefault())
            try {
                val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                     address = addresses[0]

//                    val city: String? = address.locality
//                    val adminArea: String? = address.adminArea
//                    val country: String? = address.countryName

                } else {
                    println("No address found for the provided coordinates.")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return address.locality
        }




}
