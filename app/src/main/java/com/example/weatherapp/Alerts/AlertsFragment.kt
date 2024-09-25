package com.example.weatherapp.Alerts


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.app_utils.DeleteAlerts
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AlertsFragment : Fragment() {

    private lateinit var alertsRecyclerView: RecyclerView
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var floatingActionButton: FloatingActionButton
    private val weatherAlertManager by lazy { WeatherAlertManager(requireActivity()) }

//    private var selectedDate: Calendar = Calendar.getInstance()
//    private var notificationType: String = "notification"
    private val alerts = mutableListOf(
        AlertTemp("2023-09-23 15:30", "This is the first alert."),
        AlertTemp("2023-09-24 09:15", "This is another important notification."),
        AlertTemp("2023-09-25 18:45", "Reminder: Don't forget to bring your umbrella!"),
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AlertsFragment", "Alerts: $alerts")

        floatingActionButton = view.findViewById(R.id.addAlertFab)
        floatingActionButton.setOnClickListener {

            weatherAlertManager.showWeatherAlertDialog()
        }
        alertsRecyclerView = view.findViewById(R.id.alertsRecyclerView)
        alertsRecyclerView.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )


        alertAdapter = AlertAdapter(alerts) { position ->

            Log.d("AlertsFragment", "Alert deleted at position: $position")

            CoroutineScope(Dispatchers.Main).launch {
                Log.d("AlertsFragment", "Alert deleted at position: $position")
                val result = DeleteAlerts.showDeleteAlert(
                    context = requireActivity(),
                    title = "Delete Item",
                    message = "Are you sure you want to delete item from alerts?"
                )
                if (result) {
                    alertAdapter.removeAlert(position)
                    Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Action canceled", Toast.LENGTH_SHORT).show()
                }

            }
        }
        alertsRecyclerView.adapter = alertAdapter
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alerts, container, false)
    }

//    private fun showDatePickerDialog() {
//        val alertDialog = AlertDialog.Builder(context)
//        alertDialog.setTitle("Set Weather Alert")
//        val view = layoutInflater.inflate(R.layout.dialog_weather_alert, null)
//        alertDialog.setView(view)
//
//        val durationInput = view.findViewById<EditText>(R.id.duration_input)
//        val alarmTypeRadioGroup = view.findViewById<RadioGroup>(R.id.alarm_type_group)
//        val stopSwitch = view.findViewById<SwitchCompat>(R.id.stop_alarm_switch)
//
//        alertDialog.setPositiveButton("Set Alert") { dialog, _ ->
//            val duration = durationInput.text.toString().toLongOrNull() ?: 0L
//            val alarmType = when (alarmTypeRadioGroup.checkedRadioButtonId) {
//                R.id.notification_radio -> "notification"
//                else -> "default"
//            }
//            val stopAlarm = stopSwitch.isChecked
//
//            // Call the function to set the alarm
//            setWeatherAlert(duration, alarmType, stopAlarm)
//        }
//        alertDialog.setNegativeButton("Cancel", null)
//        alertDialog.show()
//
//    }
//
//    fun setWeatherAlert(duration: Long, alarmType: String, stopAlarm: Boolean) {
//        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(requireActivity(), WeatherAlertReceiver::class.java).apply {
//            putExtra("alarm_type", alarmType)
//            putExtra("stop_alarm", stopAlarm)
//        }
//        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + duration, pendingIntent)
//    }




}


//    private fun showDateTimePicker() {
//        val datePicker = MaterialDatePicker.Builder.datePicker()
//            .setTitleText("Select Date")
//            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//            //.setTheme(R.style.YourMaterialCalendarTheme)
//            .setCalendarConstraints(
//                CalendarConstraints.Builder()
//                    .setStart(MaterialDatePicker.todayInUtcMilliseconds())
//                    .build()
//            )
//            .build()
//
//        datePicker.show(parentFragmentManager, "DATE_PICKER")
//
//        datePicker.addOnPositiveButtonClickListener { selectedDate ->
//            val calendar = Calendar.getInstance()
//            calendar.timeInMillis = selectedDate
//            showTimePicker(calendar)
//        }
//    }
//
//
//
//
//    private fun showTimePicker(selectedDate: Calendar) {
//        val currentTime = Calendar.getInstance()
//
//        var hour = if (selectedDate.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR)) {
//            currentTime.get(Calendar.HOUR_OF_DAY)
//        } else {
//            0
//        }
//
//        var minute = if (selectedDate.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR)) {
//            currentTime.get(Calendar.MINUTE)
//        } else {
//            0
//        }
//
//        val timePicker = TimePickerDialog(
//            requireActivity(),
//            { _, selectedHour, selectedMinute ->
//                selectedDate.set(Calendar.HOUR_OF_DAY, selectedHour)
//                selectedDate.set(Calendar.MINUTE, selectedMinute)
//
//                if (selectedDate.timeInMillis < System.currentTimeMillis()) {
//                    Toast.makeText(requireActivity(), "Please select a future time", Toast.LENGTH_SHORT).show()
//                    return@TimePickerDialog
//                }
//
//                showAlertOrNotificationDialog(selectedDate)
//            },
//            hour, minute, true
//        )
//
//        if (selectedDate.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR)) {
//            timePicker.updateTime(hour, minute)
//        }
//
//        timePicker.setOnShowListener {
//            if (selectedDate.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR)) {
//                val timePickerView = timePicker.findViewById<View>(Resources.getSystem().getIdentifier("timePicker", "id", "android"))
//                if (timePickerView != null) {
//                    timePickerView.findViewById<NumberPicker>(Resources.getSystem().getIdentifier("hour", "id", "android")).minValue = currentTime.get(Calendar.HOUR_OF_DAY)
//                    timePickerView.findViewById<NumberPicker>(Resources.getSystem().getIdentifier("minute", "id", "android")).minValue = currentTime.get(Calendar.MINUTE)
//                }
//            }
//        }
//
//        timePicker.show()
//    }
//
//
//    private fun showAlertOrNotificationDialog(selectedDate: Calendar) {
//        val options = arrayOf("Notification", "Alert")
//
//        AlertDialog.Builder(requireActivity())
//            .setTitle("Choose Option")
//            .setSingleChoiceItems(options, -1) { dialog, which ->
//                val selectedOption = options[which]
//                Toast.makeText(
//                    requireActivity(),
//                    "Selected: $selectedOption for ${selectedDate.time}",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                alerts.add(Alert(selectedDate.time.toString(), selectedOption))
//                Log.d("AlertsFragment", "Alerts: $alerts")
//                Log.d(
//                    "AlertsFragment",
//                    "Alerts: ${Alert(selectedDate.time.toString(), selectedOption)}"
//                )
//                dialog.dismiss()
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }


//private fun showDatePickerDialog() {
//    val currentDate = Calendar.getInstance()
//    val datePickerDialog = DatePickerDialog(
//        requireActivity(),
//        { _, year, month, dayOfMonth ->
//            selectedDate.set(year, month, dayOfMonth)
//            showTimePickerDialog()
//        },
//        currentDate.get(Calendar.YEAR),
//        currentDate.get(Calendar.MONTH),
//        currentDate.get(Calendar.DAY_OF_MONTH)
//    )
//
//    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
//    datePickerDialog.show()
//}
//
//private fun showTimePickerDialog() {
//    val currentTime = Calendar.getInstance()
//    TimePickerDialog(
//        requireActivity(),
//        { _, hourOfDay, minute ->
//            selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
//            selectedDate.set(Calendar.MINUTE, minute)
//            showNotificationTypeDialog()
//        },
//        currentTime.get(Calendar.HOUR_OF_DAY),
//        currentTime.get(Calendar.MINUTE),
//        false
//    ).show()
//}
//
//private fun showNotificationTypeDialog() {
//    val dialogView = layoutInflater.inflate(R.layout.dialog_notification_type, null)
//    val radioGroup = dialogView.findViewById<RadioGroup>(R.id.notificationTypeRadioGroup)
//
//    AlertDialog.Builder(requireActivity())
//        .setTitle("Choose Notification Type")
//        .setView(dialogView)
//        .setPositiveButton("Save") { _, _ ->
//            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
//            notificationType = when (selectedRadioButtonId) {
//                R.id.radioNotification -> "notification"
//                R.id.radioAlert -> "alert"
//                else -> "notification"
//            }
//            saveResult()
//        }
//        .setNegativeButton("Cancel", null)
//        .show()
//}
//
//private fun saveResult() {
//    val result = """
//        Date: ${selectedDate.get(Calendar.YEAR)}-${selectedDate.get(Calendar.MONTH) + 1}-${selectedDate.get(Calendar.DAY_OF_MONTH)}
//        Time: ${selectedDate.get(Calendar.HOUR_OF_DAY)}:${selectedDate.get(Calendar.MINUTE)}
//        Type: $notificationType
//    """.trimIndent()
//
//    Toast.makeText(requireActivity(), "Saved: $result", Toast.LENGTH_LONG).show()
//
//    if (notificationType == "notification") {
//        scheduleNotification()
//    } else if (notificationType == "alert") {
//        scheduleRealAlarm()
//    }
//}
//
//
//private fun scheduleRealAlarm() {
//    val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//    val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
//        putExtra(AlarmClock.EXTRA_HOUR, selectedDate.get(Calendar.HOUR_OF_DAY))
//        putExtra(AlarmClock.EXTRA_MINUTES, selectedDate.get(Calendar.MINUTE))
//        putExtra(AlarmClock.EXTRA_MESSAGE, "Scheduled Alarm")
//    }
//
//    if (intent.resolveActivity(requireContext().packageManager) != null) {
//        startActivity(intent)
//    } else {
//        Toast.makeText(requireContext(), "No app found to set the alarm", Toast.LENGTH_SHORT).show()
//    }
//}
//
//private fun scheduleNotification() {
//    val delay = selectedDate.timeInMillis - System.currentTimeMillis()
//
//    val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
//        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
//        .build()
//
//    WorkManager.getInstance(requireContext()).enqueue(notificationWork)
//}