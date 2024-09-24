package com.example.weatherapp.MainActivity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weatherapp.Model.SettingsInPlace.PREF_NAME
import com.example.weatherapp.R
import com.example.weatherapp.Settings.setLocale
import com.google.android.material.navigation.NavigationView



class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbarTitle: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        loadSettingsAndApplyLocale()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toolbarTitleLayout = layoutInflater.inflate(R.layout.toolbar_layout, null)
        toolbarTitle = toolbarTitleLayout.findViewById(R.id.toolbar_title)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.addView(toolbarTitleLayout)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.settingsFragment,
                R.id.alertsFragment,
                R.id.favouritsFragment
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbarTitle.text = destination.label?.toString() ?: getString(R.string.app_name)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun loadSettingsAndApplyLocale() {
        val sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val language = sharedPreferences.getString("language", "en").toString()

        setLocale(this, language)
    }

    fun refreshActivity() {

        val intent = intent
        finish()
        startActivity(intent)

    }
}







