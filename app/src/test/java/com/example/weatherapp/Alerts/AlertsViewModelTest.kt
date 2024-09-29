package com.example.weatherapp.Alerts

import com.example.weatherapp.Model.Alert
import com.example.weatherapp.Model.FakeWeatherRepository
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class AlertsViewModelTest {


    val alert1 = Alert(
        id = 1,
        alertType = "notification",
        alertMessage = "test message",
        alertDate = "test date",
        alertTime = "test time",
        workManagerId = "test work manager id"
    )

    val alert2 = Alert(
        id = 2,
        alertType = "notification",
        alertMessage = "test message",
        alertDate = "test date",
        alertTime = "test time",
        workManagerId = "test work manager id"
    )


    lateinit var fakeRepository: FakeWeatherRepository
    lateinit var alertsViewModel: AlertsViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeWeatherRepository(mutableListOf(), mutableListOf())
        alertsViewModel = AlertsViewModel(fakeRepository)
    }

    @Test
    fun insertAlert_successfulInsertion() = runTest {
        alertsViewModel.insertAlert(alert1)

        advanceUntilIdle()
        val alerts = fakeRepository.fakeList
        assertEquals(1, alerts?.size)
        assertEquals(alert1, alerts?.first())
    }


    @Test
    fun insertAlert_nullAlert_noInsertion() = runTest {

        fakeRepository.insertAlert(null)
        assertEquals(0, fakeRepository.fakeList?.size)
    }


    @Test
    fun insertAlert_multipleInsertions() = runTest {
        fakeRepository.insertAlert(alert1)

        fakeRepository.insertAlert(alert2)

        assertEquals(2, fakeRepository.fakeList?.size)
        assertEquals(alert1, fakeRepository.fakeList?.get(0))
        assertEquals(alert2, fakeRepository.fakeList?.get(1))
    }


    @Test
    fun insertAlert_listIsNull_createsNewListAndInserts() = runTest {
        val repositoryWithNullList = FakeWeatherRepository(null, mutableListOf())

        repositoryWithNullList.insertAlert(alert1)

        assertEquals(1, repositoryWithNullList.fakeList?.size)
        assertEquals(alert1, repositoryWithNullList.fakeList?.first())
    }


    @Test
    fun insertAlert_insertingDuplicate() = runTest {
        fakeRepository.insertAlert(alert1)
        fakeRepository.insertAlert(alert1)

        assertEquals(1, fakeRepository.fakeList?.size)
        assertEquals(alert1, fakeRepository.fakeList?.get(0))
    }


    @Test
    fun deleteAlert_successfulDeletion() = runTest {
        alertsViewModel.insertAlert(alert1)

        alertsViewModel.deleteAlert(alert1)


        advanceUntilIdle()

        val alerts = fakeRepository.fakeList
        assertEquals(0, alerts?.size)
    }

    @Test
    fun deleteAlert_nonExistingAlert_noChange() = runTest {
        alertsViewModel.insertAlert(alert1)

        alertsViewModel.deleteAlert(alert2)

        advanceUntilIdle()

        val alerts = fakeRepository.fakeList
        assertEquals(1, alerts?.size)
        assertEquals(alert1, alerts?.first())
    }

    @Test
    fun deleteAlert_nullAlert_noChange() = runTest {
        alertsViewModel.insertAlert(alert1)

        alertsViewModel.deleteAlert(null)
        advanceUntilIdle()

        val alerts = fakeRepository.fakeList
        assertEquals(1, alerts?.size)
        assertEquals(alert1, alerts?.first())
    }

    @Test
    fun deleteAlert_emptyList_noChange() = runTest {
        alertsViewModel.deleteAlert(alert1)

        advanceUntilIdle()
        val alerts = fakeRepository.fakeList
        assertEquals(0, alerts?.size)
    }


}