package dam.a50274.diminuendo.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.room.Room
import dam.a50274.diminuendo.data.local.AppDatabase
import dam.a50274.diminuendo.data.local.MeasurementDao
import dam.a50274.diminuendo.domain.model.Measurement
import dam.a50274.diminuendo.utils.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MeasurementRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: MeasurementDao
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCapabilities: NetworkCapabilities

    private val firestore = mockk<com.google.firebase.firestore.FirebaseFirestore>(relaxed = true)

    private lateinit var repository: MeasurementRepositoryImpl

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.measurementDao()

        connectivityManager = mockk(relaxed = true)
        networkCapabilities = mockk(relaxed = true)

        val spiedContext = mockk<Context>(relaxed = true)
        every { spiedContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager

        repository = MeasurementRepositoryImpl(dao, firestore, spiedContext).apply {
            workScheduler = {} // No-op for tests
        }
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun setOnline(isOnline: Boolean) {
        val network = mockk<Network>()
        if (isOnline) {
            every { connectivityManager.activeNetwork } returns network
            every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
            // FIX: isOnline() checks hasCapability(), not hasTransport().
            // The old mock used hasTransport(any()) = true which never matched
            // the NET_CAPABILITY_VALIDATED / NET_CAPABILITY_INTERNET checks,
            // causing isOnline() to always return false in tests.
            every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true
            every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        } else {
            every { connectivityManager.activeNetwork } returns null
        }
    }

    @Test
    fun saveMeasurement_whenOnline_writesBothRoomAndFirestore() = runBlocking {
        setOnline(true)
        val measurement = Measurement(
            id = UUID.randomUUID().toString(),
            userId = "user_123",
            dbLevel = 80.0,
            waveform = intArrayOf(),
            timestamp = System.currentTimeMillis(),
            latitude = null,
            longitude = null,
            contextTag = "",
            locationName = "",
        )

        // Mock the Firestore Task to resolve immediately via the fast path in .await().
        // await() checks task.isComplete first — if true, it returns task.result
        // directly without registering an addOnCompleteListener, so no looper needed.
        val task = mockk<com.google.android.gms.tasks.Task<Void>>(relaxed = true)
        every { task.isComplete } returns true
        every { task.isSuccessful } returns true
        every { task.isCanceled } returns false
        every { task.exception } returns null
        every { task.result } returns null

        val collRefUsers = mockk<com.google.firebase.firestore.CollectionReference>(relaxed = true)
        val docRefUser = mockk<com.google.firebase.firestore.DocumentReference>(relaxed = true)
        val collRefMeas = mockk<com.google.firebase.firestore.CollectionReference>(relaxed = true)
        val docRefMeas = mockk<com.google.firebase.firestore.DocumentReference>(relaxed = true)

        every { firestore.collection(any()) } returns collRefUsers
        every { collRefUsers.document(any()) } returns docRefUser
        every { docRefUser.collection(any()) } returns collRefMeas
        every { collRefMeas.document(any()) } returns docRefMeas
        every { docRefMeas.set(any()) } returns task
        every { docRefMeas.set(any(), any<com.google.firebase.firestore.SetOptions>()) } returns task
        every { docRefMeas.set(any(), any()) } returns task

        repository.saveMeasurement(measurement)

        val pending = dao.getPendingSync()
        assertTrue(pending.isEmpty())

        val all = dao.getAllByUser("user_123").first()
        assertEquals(1, all.size)
        assertFalse(all.first().pendingSync)
    }

    @Test
    fun saveMeasurement_whenOffline_writesRoomOnlyWithPendingSyncTrue() = runTest {
        setOnline(false)
        val measurement = Measurement(
            id = UUID.randomUUID().toString(),
            userId = "user_123",
            dbLevel = 80.0,
            waveform = intArrayOf(),
            timestamp = System.currentTimeMillis(),
            latitude = null,
            longitude = null,
            contextTag = "",
            locationName = "",
        )

        repository.saveMeasurement(measurement)

        val all = dao.getAllByUser("user_123").first()
        assertEquals(1, all.size)
        assertTrue(all.first().pendingSync)
    }

    @Test
    fun getPendingSync_afterOfflineSave_returnsQueuedEntry() = runTest {
        setOnline(false)
        val measurement = Measurement(
            id = UUID.randomUUID().toString(),
            userId = "user_123",
            dbLevel = 80.0,
            waveform = intArrayOf(),
            timestamp = System.currentTimeMillis(),
            latitude = null,
            longitude = null,
            contextTag = "",
            locationName = "",
        )

        repository.saveMeasurement(measurement)

        val unsynced = dao.getPendingSync()
        assertEquals(1, unsynced.size)
        assertEquals(measurement.id, unsynced.first().id)
        assertTrue(unsynced.first().pendingSync)
    }
}
