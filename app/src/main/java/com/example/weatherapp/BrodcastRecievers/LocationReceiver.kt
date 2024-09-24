package com.example.weatherapp.BrodcastRecievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.widget.Toast
import com.example.weatherapp.MainActivity.MainActivity

class LocationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGpsEnabled && !isNetworkEnabled) {
                Toast.makeText(context, "Location services are disabled", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(context, "Location services are enabled", Toast.LENGTH_SHORT).show()
                if (context is MainActivity) {
                    context.refreshActivity()
                }
            }
        }
    }
}

