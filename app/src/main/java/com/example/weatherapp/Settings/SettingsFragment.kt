package com.example.weatherapp.Settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SwitchCompat
import com.example.myapp.utils.NotificationUtils
import com.example.weatherapp.Model.SettingsInPlace
import com.example.weatherapp.R

class SettingsFragment : Fragment() {
    private val TAG = "SettingsFragmentLog"
    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "app_settings"

    private lateinit var locationGroup: RadioGroup
    private lateinit var languageGroup: RadioGroup
    private lateinit var unitGroup: RadioGroup
    private lateinit var notificationsSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        locationGroup = view.findViewById(R.id.locationGroup)
        languageGroup = view.findViewById(R.id.languageGroup)
        unitGroup = view.findViewById(R.id.unitGroup)
        notificationsSwitch = view.findViewById(R.id.notificationSwitch)

        loadSettings()

        locationGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedLocationMethod = when (checkedId) {
                R.id.gpsOption -> "GPS"
                R.id.mapOption -> "Map"
                else -> "GPS"
            }
            saveSetting("location_method", selectedLocationMethod)
        }

        languageGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedLanguage = when (checkedId) {
                R.id.arabicOption -> "ar"
                R.id.englishOption -> "en"
                else -> "en"
            }
            saveSetting("language", selectedLanguage)
            setLocale(requireActivity() as AppCompatActivity, selectedLanguage)
            requireActivity().recreate()
        }

        unitGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedUnit = when (checkedId) {
                R.id.mscelsiusOption -> "metric"
                R.id.mphcelsiusOption -> "imperial"
                R.id.mskelvinOption -> "kelvin"
                else -> "metric"
            }
            saveSetting("unit", selectedUnit)
        }

        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("notifications_enabled", isChecked)
            if (isChecked) {
                NotificationUtils.checkAndPromptEnableNotifications(requireContext())
            }
            else {
                Toast.makeText(context, R.string.notifications_disabled, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSettings() {
        val locationMethod = sharedPreferences.getString("location_method", "GPS")
        val language = sharedPreferences.getString("language", "en")
        val unit = sharedPreferences.getString("unit", "metric")
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)

        when (locationMethod) {
            "GPS" -> locationGroup.check(R.id.gpsOption)
            "Map" -> locationGroup.check(R.id.mapOption)
        }

        when (language) {
            "ar" -> languageGroup.check(R.id.arabicOption)
            "en" -> languageGroup.check(R.id.englishOption)
        }

        when (unit) {
            "metric" -> unitGroup.check(R.id.mscelsiusOption)
            "imperial" -> unitGroup.check(R.id.mphcelsiusOption)
            "kelvin" -> unitGroup.check(R.id.mskelvinOption)
        }

        notificationsSwitch.isChecked = notificationsEnabled
    }

    private fun saveSetting(key: String, value: Any) {
        sharedPreferences.edit().apply {
            when (value) {
                is Boolean -> {
                    putBoolean(key, value)
                    Log.i(TAG, "saveSetting: "+sharedPreferences.getBoolean(key, true))
                }
                is String -> {
                    putString(key, value)
                }
                else -> throw IllegalArgumentException("Unsupported type")
            }
            apply()
        }
    }
}
