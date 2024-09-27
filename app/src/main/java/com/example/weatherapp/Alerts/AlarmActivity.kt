package com.example.weatherapp.Alerts


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.app_utils.AlarmUtils
import com.example.weatherapp.databinding.ActivityAlarmBinding

class AlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dismissButton.setOnClickListener {
            dismissAlarm()
            finish()
        }
    }

    private fun dismissAlarm() {

        val dismissIntent = Intent(this, NotificationReceiver::class.java).apply {
            action = "DISMISS_NOTIFICATION"
        }

        sendBroadcast(dismissIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmUtils.stopAlarm()
    }
}
