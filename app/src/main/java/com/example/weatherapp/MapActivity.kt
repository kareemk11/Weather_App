package com.example.weatherapp

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var selectedLatLng: LatLng? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        Places.initialize(applicationContext, "AIzaSyB-blNBYVx-4Z2WAVa5miyOOoTjhk7BQXA")
        sharedPreferences = getSharedPreferences("map_location", MODE_PRIVATE)


        // Set up the map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize the FusedLocationProviderClient for potential location services
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Confirm the selected location when the confirm button is clicked
        findViewById<Button>(R.id.confirmLocationButton).setOnClickListener {
            selectedLatLng?.let {
                sharedPreferences.edit().apply {
                    putString("latitude", it.latitude.toString())
                    putString("longitude", it.longitude.toString())
                    apply()
                }
                finish()
            }
        }

        // Set up the place autocomplete search
        setupAutocompleteSearch()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Set a click listener for selecting a location on the map
        googleMap.setOnMapClickListener { latLng ->
            selectedLatLng = latLng
            googleMap.clear()  // Clear previous marker
            googleMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))  // Zoom in on the selected location
        }
    }

    // Set up the autocomplete search functionality using the Places API
    private fun setupAutocompleteSearch() {
        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Specify the fields to be returned by the autocomplete search
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        // Set up the listener to handle the selected place
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // Move the map to the selected place and drop a marker
                place.latLng?.let {
                    googleMap.clear()
                    googleMap.addMarker(MarkerOptions().position(it).title(place.name))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                    selectedLatLng = it
                }
            }

            override fun onError(status: Status) {
                Log.e("MapActivity", "An error occurred: $status")
            }
        })
    }
}
