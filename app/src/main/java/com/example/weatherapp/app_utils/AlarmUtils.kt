package com.example.weatherapp.app_utils

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator


object AlarmUtils {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    fun playAlarmSound(context: Context) {
        if (mediaPlayer == null) {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer = MediaPlayer.create(context, alarmUri)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }
    }

    fun stopAlarm() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
            mediaPlayer = null
        }
        stopVibration()
    }

    fun vibratePhone(context: Context) {
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(0, 1000, 500, 1000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            vibrator?.vibrate(pattern, 0)
        }
    }

    fun stopVibration() {
        vibrator?.cancel()
        vibrator = null
    }
}





