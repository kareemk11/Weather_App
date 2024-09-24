package com.example.weatherapp.Alerts

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.app_utils.DeleteAlerts
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar


class AlertsFragment : Fragment() {

    private lateinit var alertsRecyclerView: RecyclerView
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var floatingActionButton: FloatingActionButton
    private val alerts = mutableListOf(
        Alert("2023-09-23 15:30", "This is the first alert."),
        Alert("2023-09-24 09:15", "This is another important notification."),
        Alert("2023-09-25 18:45", "Reminder: Don't forget to bring your umbrella!"),
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AlertsFragment", "Alerts: $alerts")

        floatingActionButton = view.findViewById(R.id.addAlertFab)
        floatingActionButton.setOnClickListener {

            showDateTimePicker()
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

    private fun showDateTimePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            //.setTheme(R.style.YourMaterialCalendarTheme)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setStart(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            )
            .build()

        datePicker.show(parentFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selectedDate ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDate
            showTimePicker(calendar)
        }
    }




    private fun showTimePicker(selectedDate: Calendar) {
        val currentTime = Calendar.getInstance()

        var hour = if (selectedDate.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR)) {
            currentTime.get(Calendar.HOUR_OF_DAY)
        } else {
            0
        }

        var minute = if (selectedDate.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR)) {
            currentTime.get(Calendar.MINUTE)
        } else {
            0
        }

        val timePicker = TimePickerDialog(
            requireActivity(),
            { _, selectedHour, selectedMinute ->
                selectedDate.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedDate.set(Calendar.MINUTE, selectedMinute)

                if (selectedDate.timeInMillis < System.currentTimeMillis()) {
                    Toast.makeText(requireActivity(), "Please select a future time", Toast.LENGTH_SHORT).show()
                    return@TimePickerDialog
                }

                showAlertOrNotificationDialog(selectedDate)
            },
            hour, minute, true
        )

        if (selectedDate.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR)) {
            timePicker.updateTime(hour, minute)
        }

        timePicker.setOnShowListener {
            if (selectedDate.get(Calendar.DAY_OF_YEAR) == currentTime.get(Calendar.DAY_OF_YEAR)) {
                val timePickerView = timePicker.findViewById<View>(Resources.getSystem().getIdentifier("timePicker", "id", "android"))
                if (timePickerView != null) {
                    timePickerView.findViewById<NumberPicker>(Resources.getSystem().getIdentifier("hour", "id", "android")).minValue = currentTime.get(Calendar.HOUR_OF_DAY)
                    timePickerView.findViewById<NumberPicker>(Resources.getSystem().getIdentifier("minute", "id", "android")).minValue = currentTime.get(Calendar.MINUTE)
                }
            }
        }

        timePicker.show()
    }


    private fun showAlertOrNotificationDialog(selectedDate: Calendar) {
        val options = arrayOf("Notification", "Alert")

        AlertDialog.Builder(requireActivity())
            .setTitle("Choose Option")
            .setSingleChoiceItems(options, -1) { dialog, which ->
                val selectedOption = options[which]
                Toast.makeText(
                    requireActivity(),
                    "Selected: $selectedOption for ${selectedDate.time}",
                    Toast.LENGTH_SHORT
                ).show()

                alerts.add(Alert(selectedDate.time.toString(), selectedOption))
                Log.d("AlertsFragment", "Alerts: $alerts")
                Log.d(
                    "AlertsFragment",
                    "Alerts: ${Alert(selectedDate.time.toString(), selectedOption)}"
                )
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}