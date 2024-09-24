package com.example.weatherapp.Favourites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Favourites.FavouritesWeather.FavouritesWeatherActivity
import com.example.weatherapp.MapActivity
import com.example.weatherapp.app_utils.DeleteAlerts
import com.example.weatherapp.databinding.FragmentFavouritsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FavouritesFragment : Fragment() {
    private lateinit var binding: FragmentFavouritsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addFavoriteButton.setOnClickListener {
            val intent = Intent(activity, MapActivity::class.java)
            intent.putExtra("isFavourite", true)
            startActivity(intent)
            Log.i("FavouritesFragment", "onViewCreated: ${TempRepo.favourites}")
        }
        recyclerViewInit()

    }

    private fun recyclerViewInit() {
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.favoritesRecyclerView.setHasFixedSize(true)
        binding.favoritesRecyclerView.adapter = WeatherLocationAdapter(
            TempRepo.favourites,
            {



                CoroutineScope(Dispatchers.Main).launch {
                    val result = DeleteAlerts.showDeleteAlert(
                        context = requireActivity(),
                        title = "Delete Item",
                        message = "Are you sure you want to delete ${it.name} from favourites?"
                    )
                    if (result) {
                        TempRepo.removeFavourite(it)
                        binding.favoritesRecyclerView.adapter?.notifyDataSetChanged()
                        Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Action canceled", Toast.LENGTH_SHORT).show()
                    }

                }



                //showDeleteAlert("Delete", "Are you sure you want to delete ${it.name} from favourites?", it)
            },
            { favouriteWeatherObject ->
                navigateToFavouriteWeatherActivity(favouriteWeatherObject)
            }
        )
    }

    private fun navigateToFavouriteWeatherActivity(favouriteWeatherObject: FavouriteWeatherObject) {

        val intent = Intent(activity , FavouritesWeatherActivity::class.java )
        intent.putExtra("cords", favouriteWeatherObject)
        startActivity(intent)
    }

    private fun showDeleteAlert(title: String, message: String, favouriteWeatherObject: FavouriteWeatherObject) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Delete") { _, _ ->
                onDeleteConfirmed(true, favouriteWeatherObject)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                onDeleteConfirmed(false, favouriteWeatherObject)
            }
            .setOnCancelListener {
                onDeleteConfirmed(false,favouriteWeatherObject)
            }
            .create()

        dialog.show()
    }

    private fun onDeleteConfirmed(isDeleted: Boolean, favouriteWeatherObject: FavouriteWeatherObject) {
        if (isDeleted) {
            Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
            TempRepo.removeFavourite(favouriteWeatherObject)
        } else {
            Toast.makeText(requireContext(), "Action canceled", Toast.LENGTH_SHORT).show()
        }
    }
}
