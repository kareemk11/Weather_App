package com.example.weatherapp.BrodcastRecievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.View
import com.example.weatherapp.app_utils.InternetConnectionUtil
import com.google.android.material.snackbar.Snackbar

class InternetConnectivityReceiver(private val rootView: View) : BroadcastReceiver() {

    var hasTheInternetBeenDown = false

    override fun onReceive(context: Context, intent: Intent) {

        if (!InternetConnectionUtil.isInternetAvailable(context)) {
            Snackbar.make(rootView, "No internet connection", Snackbar.LENGTH_SHORT).show()
            hasTheInternetBeenDown = true
        } else if (hasTheInternetBeenDown) {
            Snackbar.make(rootView, "Internet connection restored", Snackbar.LENGTH_SHORT).show()
            hasTheInternetBeenDown = false
        }
    }
}