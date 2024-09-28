package com.example.weatherapp.Alerts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Model.Alert
import com.example.weatherapp.R

class AlertAdapter(
    private val alerts: MutableList<Alert>,
    private val onDeleteClick: (Alert) -> Unit,
) : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val alertTextView: TextView = view.findViewById(R.id.alertTextView)
        val dateTimeTextView: TextView = view.findViewById(R.id.dateTextView)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
        val card :CardView = view.findViewById(R.id.itemCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.alert_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alert = alerts[position]
        "${alert.alertDate} at ${alert.alertTime}".also { holder.dateTimeTextView.text = it }
        holder.alertTextView.text = alert.alertType

        holder.deleteButton.setOnClickListener {

            onDeleteClick(alert)
        }

    }

    override fun getItemCount() = alerts.size



    fun updateData(it: List<Alert>?) {

        alerts.clear()
        if (it != null) {
            alerts.addAll(it)
        }
        notifyDataSetChanged()
    }
}

