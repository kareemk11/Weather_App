package com.example.weatherapp.Favourites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Favourites.FavouritesWeather.FavouritesWeatherActivity
import com.example.weatherapp.MapActivity.MapActivity
import com.example.weatherapp.Model.CurrentWeather
import com.example.weatherapp.Model.FavouritesState
import com.example.weatherapp.Model.WeatherRepository
import com.example.weatherapp.Network.WeatherRemoteDataSource
import com.example.weatherapp.WeatherDatabase.WeatherDatabase
import com.example.weatherapp.WeatherDatabase.WeatherLocalDataSource
import com.example.weatherapp.app_utils.DeleteAlerts
import com.example.weatherapp.databinding.FragmentFavouritsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FavouritesFragment : Fragment() {
    private lateinit var binding: FragmentFavouritsBinding
    private lateinit var favouritesAdapter: WeatherLocationAdapter
    private val favouritesViewModel: FavouritesViewModel by lazy {
        val factory = FavouritesViewModelFactory(
            WeatherRepository.getInstance(
                WeatherLocalDataSource(
                    WeatherDatabase.getInstance(requireContext()).forecastDao(),
                    WeatherDatabase.getInstance(requireContext()).alertDao(),
                    WeatherDatabase.getInstance(requireContext()).currentWeatherDao()
                ),
                WeatherRemoteDataSource.getInstance()
            )
        )
        ViewModelProvider(this, factory).get(FavouritesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.addFavoriteButton.setOnClickListener {

            val intent = Intent(activity, MapActivity::class.java)
            intent.putExtra("isFavourite", true)
            startActivity(intent)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                favouritesViewModel.favourites.collect { favouritesState ->
                    when (favouritesState) {
                        is FavouritesState.Loading -> {
                            // Show loading state
                        }

                        is FavouritesState.Success -> {
                            favouritesAdapter.updateData(favouritesState.data)
                        }

                        is FavouritesState.Error -> {
                            // Show error state
                        }
                    }

                }
            }
        }
//        favouritesViewModel.favourites.observe(viewLifecycleOwner) { favourites ->
//            favouritesAdapter.updateData(favourites)
//        }

        binding.searchEditText.addTextChangedListener {
            lifecycleScope.launch {
                favouritesViewModel.updateSearchQuery(it.toString())
            }
        }
    }

    private fun setupRecyclerView() {
        binding.favoritesRecyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.favoritesRecyclerView.setHasFixedSize(true)

        favouritesAdapter = WeatherLocationAdapter(
            emptyList(),
            { favourite -> deleteFavourite(favourite) },
            { favourite -> navigateToFavouriteWeatherActivity(favourite) }
        )

        binding.favoritesRecyclerView.adapter = favouritesAdapter
    }

    private fun deleteFavourite(favourite: CurrentWeather) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = DeleteAlerts.showDeleteAlert(
                context = requireActivity(),
                title = "Delete Item",
                message = "Are you sure you want to delete ${favourite.name} from favourites?"
            )
            if (result) {
                favouritesViewModel.deleteFavourite(favourite)
                Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToFavouriteWeatherActivity(favouriteWeatherObject: CurrentWeather) {
        val intent = Intent(activity, FavouritesWeatherActivity::class.java)
        intent.putExtra("cords", favouriteWeatherObject)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        favouritesViewModel.fetchFavourites()
    }
}


