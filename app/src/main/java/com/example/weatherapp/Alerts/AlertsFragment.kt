package com.example.weatherapp.Alerts


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.example.weatherapp.Model.Alert
import com.example.weatherapp.Model.WeatherRepository
import com.example.weatherapp.Network.WeatherRemoteDataSource
import com.example.weatherapp.R
import com.example.weatherapp.WeatherDatabase.WeatherDatabase
import com.example.weatherapp.WeatherDatabase.WeatherLocalDataSource
import com.example.weatherapp.app_utils.DeleteAlerts
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AlertsFragment : Fragment() {

    private lateinit var alertsRecyclerView: RecyclerView
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var floatingActionButton: FloatingActionButton


    private val viewModel: AlertsViewModel by lazy {


        val repository = WeatherRepository.getInstance(
            WeatherLocalDataSource(
                WeatherDatabase.getInstance(requireActivity()).forecastDao(),
                WeatherDatabase.getInstance(requireActivity()).alertDao(),
                WeatherDatabase.getInstance(requireActivity()).currentWeatherDao()
            ), WeatherRemoteDataSource.getInstance()
        )
        val factory = AlertsViewModelFactory(repository)
        ViewModelProvider(this, factory).get(AlertsViewModel::class.java)

    }
    private val weatherAlertManager by lazy { WeatherAlertManager(requireActivity()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        floatingActionButton = view.findViewById(R.id.addAlertFab)
        floatingActionButton.setOnClickListener {

            weatherAlertManager.showWeatherAlertDialog(viewModel)

        }
        alertsRecyclerView = view.findViewById(R.id.alertsRecyclerView)
        alertsRecyclerView.layoutManager = LinearLayoutManager(
            requireActivity(), LinearLayoutManager.VERTICAL, false
        )

        viewModel.getAlerts()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.alerts.collect {
                    alertAdapter.updateData(it)
                }
            }
        }
        //
    //        viewModel.alerts.observe(viewLifecycleOwner)
    //        {
    //            alertAdapter.updateData(it)
    //        }
        //


        alertAdapter = AlertAdapter(emptyList<Alert>().toMutableList()) { alert ->


            CoroutineScope(Dispatchers.Main).launch {
                val result = DeleteAlerts.showDeleteAlert(
                    context = requireActivity(),
                    title = "Delete Item",
                    message = "Are you sure you want to delete item from alerts?"
                )
                if (result) {
                    viewModel.deleteAlert(alert, WorkManager.getInstance(requireContext()))

                    Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Action canceled", Toast.LENGTH_SHORT).show()
                }

            }
        }
        alertsRecyclerView.adapter = alertAdapter
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alerts, container, false)
    }

}