package com.example.weatherapp.Alerts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R

class AlertAdapter(
    private val alerts: MutableList<AlertTemp>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val alertTextView: TextView = view.findViewById(R.id.alertTextView)
        val dateTimeTextView: TextView = view.findViewById(R.id.dateTextView)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.alert_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alert = alerts[position]
        holder.dateTimeTextView.text = alert.dateTime
        holder.alertTextView.text = alert.message

        holder.deleteButton.setOnClickListener {

            onDeleteClick(position)
        }
    }

    override fun getItemCount() = alerts.size

    fun removeAlert(position: Int) {
        alerts.removeAt(position)
        notifyItemRemoved(position)
    }
}

data class AlertTemp(var dateTime: String, val message: String)