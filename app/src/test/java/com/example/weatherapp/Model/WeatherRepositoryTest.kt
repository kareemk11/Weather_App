import com.example.weatherapp.Model.Alert
import com.example.weatherapp.Model.CurrentWeather
import com.example.weatherapp.Model.WeatherRepository
import com.example.weatherapp.Network.FakeRemoteDataSource
import com.example.weatherapp.WeatherDatabase.FakeLocalDataSource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {

    // Sample alerts and current weather objects
    val alert1 = Alert(
        id = 1,
        alertMessage = "Heavy Rain Expected",
        alertType = "Notification",
        alertDate = "2024-10-01",
        alertTime = "08:30 AM",
        workManagerId = "work_id_001"
    )

    val alert2 = Alert(
        id = 2,
        alertMessage = "Storm Warning",
        alertType = "Alarm",
        alertDate = "2024-10-02",
        alertTime = "12:00 PM",
        workManagerId = "work_id_002"
    )
    val currentWeather1 = CurrentWeather(
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

    val currentWeather2 = CurrentWeather(
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

    val currentWeather3 = CurrentWeather(
        id = 3,
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


    private lateinit var fakeLocalDatabase: FakeLocalDataSource
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var weatherRepository: WeatherRepository

    @Before
    fun setup() {
        fakeLocalDatabase = FakeLocalDataSource(mutableListOf(), mutableListOf())
        fakeRemoteDataSource = FakeRemoteDataSource()
        weatherRepository = WeatherRepository.getInstance(fakeLocalDatabase, fakeRemoteDataSource)
    }

    @Test
    fun insertAlert_successfulInsertion() = runTest {
        weatherRepository.insertAlert(alert1)

        val alerts = fakeLocalDatabase.alerts
        assertThat(alerts?.size, IsEqual(1))
        assertThat(alerts?.first(), IsEqual(alert1))
    }

    @Test
    fun insertAlert_multipleInserts() = runTest {
        weatherRepository.insertAlert(alert1)
        weatherRepository.insertAlert(alert2)

        val alerts = fakeLocalDatabase.alerts
        assertThat(alerts?.size, IsEqual(2))
        assertThat(alerts?.get(0), IsEqual(alert1))
        assertThat(alerts?.get(1), IsEqual(alert2))
    }

    @Test
    fun insertAlert_duplicateAlert() = runTest {
        weatherRepository.insertAlert(alert1)
        weatherRepository.insertAlert(alert1)

        val alerts = fakeLocalDatabase.alerts
        assertThat(alerts?.size, IsEqual(2))
        assertThat(alerts?.count { it == alert1 }, IsEqual(2))
    }

    @Test
    fun insertAlert_whenListIsNull() = runTest {
        fakeLocalDatabase.alerts = null
        weatherRepository.insertAlert(alert1)

        val alerts = fakeLocalDatabase.alerts
        assertThat(alerts?.size, IsEqual(1))
        assertThat(alerts?.first(), IsEqual(alert1))
    }


    @Test
    fun testAlertListSizeAfterInsertion() = runTest {
        val originalSize = fakeLocalDatabase.alerts?.size ?: 0
        weatherRepository.insertAlert(alert1)

        val alerts = fakeLocalDatabase.alerts
        assertThat(alerts?.size, IsEqual(originalSize + 1))
    }

    @Test
    fun insertNullAlert() = runTest {
        val originalSize = fakeLocalDatabase.alerts?.size ?: 0
        weatherRepository.insertAlert(null)

        val alerts = fakeLocalDatabase.alerts
        assertThat(alerts?.size, IsEqual(originalSize))
    }

    @Test
    fun deleteAlertByWorkManagerId_successfulDeletion() = runTest {
        weatherRepository.insertAlert(alert1)
        weatherRepository.insertAlert(alert2)

        weatherRepository.deleteAlertByWorkManagerId(alert1.workManagerId)

        val alerts = fakeLocalDatabase.alerts
        assertThat(alerts?.size, IsEqual(1))
        assertThat(alerts?.contains(alert1), IsEqual(false))
        assertThat(alerts?.contains(alert2), IsEqual(true))
    }

    @Test
    fun deleteAlertByWorkManagerId_nonExistingId() = runTest {

        weatherRepository.insertAlert(alert1)
        weatherRepository.deleteAlertByWorkManagerId("non_existing_id")

        val alerts = fakeLocalDatabase.alerts
        assertThat(alerts?.size, IsEqual(1))
        assertThat(alerts?.contains(alert1), IsEqual(true))
    }

    @Test
    fun deleteAlertByWorkManagerId_emptyList() = runTest {
        weatherRepository.deleteAlertByWorkManagerId(alert1.workManagerId)

        val alerts = fakeLocalDatabase.alerts
        assertThat(alerts?.isEmpty(), IsEqual(true))
    }

    @Test
    fun deleteAlertByWorkManagerId_nullAlerts() = runTest {

        weatherRepository.insertAlert(alert1)
        weatherRepository.insertAlert(alert2)


        fakeLocalDatabase.alerts = null
        weatherRepository.deleteAlertByWorkManagerId(alert1.workManagerId)


        val alerts = fakeLocalDatabase.alerts
        assertThat(alerts, IsEqual(null))
    }




        @Test
        fun getAllAlerts_noAlerts() = runTest {
            val alertsFlow = weatherRepository.getAllAlerts()

            val alerts = alertsFlow.first()

            assertEquals(emptyList<Alert>(), alerts)
        }

        @Test
        fun getAllAlerts_multipleAlerts() = runTest {
            weatherRepository.insertAlert(alert1)
            weatherRepository.insertAlert(alert2)

            val alertsFlow = weatherRepository.getAllAlerts()
            val alerts = alertsFlow.first()

            assertEquals(listOf(alert1, alert2), alerts)
        }

        @Test
        fun getAllAlerts_afterDeletingAlert() = runTest {
            weatherRepository.insertAlert(alert1)
            weatherRepository.insertAlert(alert2)
            weatherRepository.deleteAlertByWorkManagerId(alert1.workManagerId)

            val alertsFlow = weatherRepository.getAllAlerts()
            val alerts = alertsFlow.first()

            assertEquals(listOf(alert2), alerts)
        }

    @Test
    fun getAllAlerts_nullAlerts() = runTest {
        fakeLocalDatabase.alerts = null

        val alertsFlow = weatherRepository.getAllAlerts()
        val alerts = alertsFlow.first()

        assertEquals(emptyList<Alert>(), alerts)
    }

}
