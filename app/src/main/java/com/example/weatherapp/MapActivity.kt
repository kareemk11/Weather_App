package com.example.weatherapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.MainActivity.MainActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var selectedLatLng: LatLng? = null
    private lateinit var confirmButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private val TAG = "MapActivityLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        Places.initialize(applicationContext, "AIzaSyCsuBUEkD-hRJMtP4221JOI_DRF5yp9g7Q");  // Replace with your actual API key
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("map_preferences", MODE_PRIVATE)
        setContentView(R.layout.activity_map)
        confirmButton = findViewById(R.id.confirmLocationButton)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        confirmButton.setOnClickListener {
            if (selectedLatLng != null) {
                // Save the selected location to SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putFloat("latitude", selectedLatLng!!.latitude.toFloat())
                editor.putFloat("longitude", selectedLatLng!!.longitude.toFloat())
                editor.apply()

                Log.i(TAG, "Saved Latitude: ${sharedPreferences.getFloat("latitude", 0f)}")
                Log.i(TAG, "Saved Longitude: ${sharedPreferences.getFloat("longitude", 0f)}")

                // Start MainActivity and finish MapActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()  // End this activity
            } else {
                Log.w(TAG, "No location selected.")
            }
        }


        // Get the AutocompleteSupportFragment
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

// Specify the types of place data to return
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

// Set up a PlaceSelectionListener to handle the response
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // Get info about the selected place.
                val selectedPlaceLatLng = place.latLng
                selectedPlaceLatLng?.let {
                    // Move the map to the selected place
                    moveToPlace(it)
                }
            }

            override fun onError(status: Status) {
                // Handle the error
                Log.e("AutocompleteError", "An error occurred: $status")
            }
        })

    }

    private fun moveToPlace(latLng: LatLng) {
        // Move the Google Maps camera to the selected location
        if (::mMap.isInitialized) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            // Optionally add a marker at the selected place
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))

            // Save the selected location for later use
            selectedLatLng = latLng
        }
    }






    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener { latLng ->
            selectedLatLng = latLng
            mMap.clear()  // Clear any previous markers
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
        }

        val initialLocation = LatLng(-34.0, 151.0)  // Replace with your default location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10f))
    }


}
