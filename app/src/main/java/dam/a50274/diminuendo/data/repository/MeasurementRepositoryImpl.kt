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
        // 1. Save to Room first as SSOT — always succeeds regardless of network.
        dao.insertOrReplace(measurement.toEntity(pendingSync = true))

        if (!isOnline()) {
            // Schedule WorkManager to retry when connectivity returns.
            workScheduler()
            return
        }

        try {
            // Increased to 15s — Firestore transactions on mobile networks can be slow.
            withTimeout(15_000L) {
                // 2. Upload the measurement document.
                firestore
                    .collection("users")
                    .document(measurement.userId)
                    .collection("measurements")
                    .document(measurement.id)
                    .set(measurement.toDto())
                    .await()

                // 3. Update (or create) the noise_zones aggregate document.
                if (measurement.latitude != null && measurement.longitude != null) {
                    updateNoiseZone(measurement)
                }

                // 4. Mark as synced in Room only after both Firestore writes succeed.
                dao.insertOrReplace(measurement.toEntity(pendingSync = false))
            }
        } catch (e: Exception) {
            // Leave pendingSync = true in Room so WorkManager can retry.
            throw SyncException("Failed to sync measurement to cloud: ${e.message}", e)
        }
    }

    override suspend fun deleteMeasurement(id: String) {
        dao.softDelete(id, System.currentTimeMillis())
    }

    // ---------------------------------------------------------------------------
    // Internal helpers
    // ---------------------------------------------------------------------------

    /**
     * Creates or updates the noise_zones/{locationId} aggregate document.
     * Uses a Firestore transaction so concurrent writes from different devices
     * don't produce race conditions on totalContributions / hourlyAverages.
     */
    private suspend fun updateNoiseZone(measurement: Measurement) {
        val lat = Math.round(measurement.latitude!! * 100.0) / 100.0
        val lng = Math.round(measurement.longitude!! * 100.0) / 100.0
        val locationId = "zone_${lat}_$lng"
        val zoneRef = firestore.collection("noise_zones").document(locationId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(zoneRef)
            val total = snapshot.getLong("totalContributions") ?: 0L

            val existingAverages = readHourlyAverages(snapshot.get("hourlyAverages"))

            val cal = Calendar.getInstance().apply { timeInMillis = measurement.timestamp }
            val hour = cal.get(Calendar.HOUR_OF_DAY)

            val newTotal = total + 1
            val newAverages = existingAverages.toMutableList()
            newAverages[hour] =
                ((existingAverages[hour] * total) + measurement.dbLevel) / newTotal

            // Always write a typed NoiseZoneDto, never a raw Map.
            // This guarantees Firestore stores consistent field names and types
            // that NoiseZoneRepositoryImpl can safely deserialize.
            val updatedZone = NoiseZoneDto(
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

    /**
     * Safely converts whatever Firestore returns for hourlyAverages into
     * a clean List<Double> of exactly 24 elements.
     *
     * Firestore stores numbers without a Kotlin type — whole-number doubles
     * (e.g. 0.0, 65.0) may come back as Long. We cast defensively.
     */
    private fun readHourlyAverages(raw: Any?): List<Double> {
        val list: List<Double> = when (raw) {
            is List<*> -> raw.map { element ->
                when (element) {
                    is Double -> element
                    is Long -> element.toDouble()
                    is Int -> element.toDouble()
                    is Number -> element.toDouble()
                    else -> 0.0
                }
            }
            else -> emptyList()
        }
        // Pad to 24 or trim to 24
        return if (list.size == 24) list
        else List(24) { i -> list.getOrElse(i) { 0.0 } }
    }

    /**
     * Uses NET_CAPABILITY_VALIDATED (not just NET_CAPABILITY_INTERNET) to match
     * the fixed NetworkMonitorImpl behaviour. VALIDATED means Android has confirmed
     * the network actually reaches the internet — not just that the network claims to.
     */
    private fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
