package dam.a50274.diminuendo.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import dam.a50274.diminuendo.data.local.MeasurementDao
import dam.a50274.diminuendo.data.mapper.toDomain
import dam.a50274.diminuendo.data.mapper.toDto
import dam.a50274.diminuendo.data.mapper.toEntity
import dam.a50274.diminuendo.data.remote.NoiseZoneDto
import dam.a50274.diminuendo.data.worker.SyncMeasurementsWorker
import dam.a50274.diminuendo.domain.model.Measurement
import dam.a50274.diminuendo.domain.model.SyncException
import dam.a50274.diminuendo.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.util.Calendar
import javax.inject.Inject

class MeasurementRepositoryImpl @Inject constructor(
    private val dao: MeasurementDao,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context,
) : MeasurementRepository {

    var workScheduler: () -> Unit = {
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<SyncMeasurementsWorker>()
            .setConstraints(
                androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build(),
            )
            .build()
        androidx.work.WorkManager.getInstance(context).enqueue(workRequest)
    }

    override fun getMeasurementsByUser(userId: String): Flow<List<Measurement>> {
        return dao.getAllByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveMeasurement(measurement: Measurement) {
        // Save to Room first as SSOT with pendingSync = true initially
        dao.insertOrReplace(measurement.toEntity(pendingSync = true))

        if (!isOnline()) {
            workScheduler()
            return
        }

        try {
            withTimeout(5000L) {
                // Upload Measurement
                firestore.collection("users")
                    .document(measurement.userId)
                    .collection("measurements")
                    .document(measurement.id)
                    .set(measurement.toDto())
                    .await()

                if (measurement.latitude != null && measurement.longitude != null) {
                    val lat = Math.round(measurement.latitude * 100.0) / 100.0
                    val lng = Math.round(measurement.longitude * 100.0) / 100.0
                    val locationId = "zone_${lat}_$lng"

                    val zoneRef = firestore.collection("noise_zones").document(locationId)

                    firestore.runTransaction { transaction ->
                        val snapshot = transaction.get(zoneRef)
                        val total = snapshot.getLong("totalContributions") ?: 0L

                        // Safely read hourlyAverages — Firestore may store whole-number doubles as Long
                        val rawAverages = snapshot.get("hourlyAverages")
                        val existingAverages: List<Double> = when (rawAverages) {
                            is List<*> -> rawAverages.map { element ->
                                when (element) {
                                    is Double -> element
                                    is Long   -> element.toDouble()
                                    is Int    -> element.toDouble()
                                    is Number -> element.toDouble()
                                    else      -> 0.0
                                }
                            }.let { list ->
                                // Pad or trim to exactly 24 slots
                                if (list.size == 24) list else List(24) { i -> list.getOrElse(i) { 0.0 } }
                            }
                            else -> List(24) { 0.0 }
                        }

                        val cal = java.util.Calendar.getInstance().apply { timeInMillis = measurement.timestamp }
                        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)

                        val newTotal = total + 1
                        val newAverages = existingAverages.toMutableList()
                        newAverages[hour] = ((existingAverages[hour] * total) + measurement.dbLevel) / newTotal

                        // Write a typed NoiseZoneDto — NOT a raw Map — so Firestore stores
                        // consistent types that toObjects() can deserialize correctly.
                        val updatedZone = dam.a50274.diminuendo.data.remote.NoiseZoneDto(
                            locationId = locationId,
                            centerLatitude = lat,
                            centerLongitude = lng,
                            locationName = measurement.locationName.ifBlank { "Zone $locationId" },
                            hourlyAverages = newAverages,
                            totalContributions = newTotal.toInt(),
                        )
                        transaction.set(zoneRef, updatedZone)
                    }.await()
                }

                // After the withTimeout block succeeds, mark as synced:
                dao.insertOrReplace(measurement.toEntity(pendingSync = false))
            }
        } catch (e: Exception) {
            // Throw custom domain exception instead of ignoring it
            throw SyncException("Failed to upload measurement to cloud: ${e.message}", e)
        }
    }

    override suspend fun deleteMeasurement(id: String) {
        dao.softDelete(id, System.currentTimeMillis())
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(network) ?: return false
        return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}
