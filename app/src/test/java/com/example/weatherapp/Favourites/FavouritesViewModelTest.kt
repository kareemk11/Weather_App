package com.example.weatherapp.Favourites

import com.example.weatherapp.Model.CurrentWeather
import com.example.weatherapp.Model.CurrentWeatherState
import com.example.weatherapp.Model.FakeWeatherRepository
import com.example.weatherapp.Model.toWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

@ExperimentalCoroutinesApi
class FavouritesViewModelTest {

    private val favouriteWeather = CurrentWeather(
        id = 1,
        lat = 37.7749,
        lon = -122.4194,
        name = "San Francisco",
        temp = 18.5,
        pressure = 1013,
        humidity = 72,
        temp_min = 15.0,
        temp_max = 20.0,
        description = "Clear sky",
        icon = "01d",
        speed = 5.5,
        country = "US",
        all = 0
    )

    private val favouriteWeather2 = CurrentWeather(
        id = 2,
        lat = 51.5074,
        lon = -0.1278,
        name = "London",
        temp = 12.0,
        pressure = 1010,
        humidity = 85,
        temp_min = 10.0,
        temp_max = 14.0,
        description = "Light rain",
        icon = "09d",
        speed = 4.0,
        country = "GB",
        all = 80
    )

    private lateinit var fakeWeatherRepository: FakeWeatherRepository
    private lateinit var favouritesViewModel: FavouritesViewModel
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeWeatherRepository = FakeWeatherRepository(mutableListOf(), mutableListOf())
        favouritesViewModel = FavouritesViewModel(fakeWeatherRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun deleteFavourite_successfulDeletion() = runTest {
        fakeWeatherRepository.fakeList2 = mutableListOf(favouriteWeather, favouriteWeather2)
       // assertEquals(2, fakeWeatherRepository.fakeList2?.size)

        favouritesViewModel.deleteFavourite(favouriteWeather)

        advanceUntilIdle()

        assertEquals(1, fakeWeatherRepository.fakeList2?.size)
        assertEquals(favouriteWeather2, fakeWeatherRepository.fakeList2?.first())
    }

    @Test
    fun deleteFavourite_nonExistingFavourite_noChange() = runTest {
        fakeWeatherRepository.fakeList2 = mutableListOf(favouriteWeather)
        assertEquals(1, fakeWeatherRepository.fakeList2?.size)

        favouritesViewModel.deleteFavourite(favouriteWeather2)

        assertEquals(1, fakeWeatherRepository.fakeList2?.size)
        assertEquals(favouriteWeather, fakeWeatherRepository.fakeList2?.first())
    }

   @Test
    fun deleteFavourite_nullFavourite_noChange() = runTest {
       fakeWeatherRepository.fakeList2 = mutableListOf(favouriteWeather)

       favouritesViewModel.deleteFavourite(null)
       advanceUntilIdle()

       assertEquals(1, fakeWeatherRepository.fakeList2?.size)
   }

    @Test
    fun deleteFavourite_NullList_noChange() = runTest {
        fakeWeatherRepository.fakeList2 = null
        favouritesViewModel.deleteFavourite(favouriteWeather)
        advanceUntilIdle()
        assertEquals(null, fakeWeatherRepository.fakeList2)
    }



    @Test
    fun getFavouriteWeatherDateFromLocal_updatesWeatherResponse() = runTest {

        favouritesViewModel.getFavouriteWeatherDateFromLocal(favouriteWeather)

        val expectedState = CurrentWeatherState.Success(favouriteWeather.toWeatherResponse())
        assertEquals(expectedState, favouritesViewModel.weather.value)
    }

    @Test
    fun getFavouriteWeatherDateFromLocal_nullInput_doesNotUpdateState() = runTest {


        favouritesViewModel.getFavouriteWeatherDateFromLocal(null)
        assertEquals(CurrentWeatherState.Loading, favouritesViewModel.weather.value)
    }

    @Test
    fun getFavouriteWeatherDateFromLocal_duplicateCall_updatesStateConsistently() = runTest {

        favouritesViewModel.getFavouriteWeatherDateFromLocal(favouriteWeather)
        val firstState = favouritesViewModel.weather.value

        favouritesViewModel.getFavouriteWeatherDateFromLocal(favouriteWeather)
        val secondState = favouritesViewModel.weather.value

        assertEquals(firstState, secondState)
    }

    @Test
    fun getFavouriteWeatherDateFromLocal_multipleFavourites_updatesWeatherResponse() = runTest {


        favouritesViewModel.getFavouriteWeatherDateFromLocal(favouriteWeather)
        val firstState = favouritesViewModel.weather.value

        favouritesViewModel.getFavouriteWeatherDateFromLocal(favouriteWeather2)
        val secondState = favouritesViewModel.weather.value

        assertEquals(CurrentWeatherState.Success(favouriteWeather2.toWeatherResponse()), secondState)
    }

    @Test
    fun getFavouriteWeatherDateFromLocal_staysInLoadingState_ifNotUpdated() = runTest {

        val initialState = favouritesViewModel.weather.value
        favouritesViewModel.getFavouriteWeatherDateFromLocal(null)
        assertEquals(initialState, favouritesViewModel.weather.value)
    }

}
