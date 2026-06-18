package dam.a50274.diminuendo.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dam.a50274.diminuendo.data.local.MeasurementDao
import dam.a50274.diminuendo.data.mapper.toDomain
import dam.a50274.diminuendo.data.mapper.toDto
import dam.a50274.diminuendo.data.remote.NoiseZoneDto
import kotlinx.coroutines.tasks.await
import java.util.Calendar

@HiltWorker
class SyncMeasurementsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dao: MeasurementDao,
    private val firestore: FirebaseFirestore,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val pending = dao.getPendingSync()
        if (pending.isEmpty()) return Result.success()

        var anyFailed = false

        for (entity in pending) {
            try {
                val measurement = entity.toDomain()

                // 1. Upload the measurement document.
                firestore
                    .collection("users")
                    .document(measurement.userId)
                    .collection("measurements")
                    .document(measurement.id)
                    .set(measurement.toDto())
                    .await()

                // 2. Update the noise_zones aggregate if we have coordinates.
                if (measurement.latitude != null && measurement.longitude != null) {
                    val lat = Math.round(measurement.latitude * 100.0) / 100.0
                    val lng = Math.round(measurement.longitude * 100.0) / 100.0
                    val locationId = "zone_${lat}_$lng"
                    val zoneRef = firestore.collection("noise_zones").document(locationId)

                    firestore.runTransaction { transaction ->
                        val snapshot = transaction.get(zoneRef)
                        val total = snapshot.getLong("totalContributions") ?: 0L

                        val existingAverages = readHourlyAverages(snapshot.get("hourlyAverages"))

                        val cal = Calendar.getInstance().apply {
                            timeInMillis = measurement.timestamp
                        }
                        val hour = cal.get(Calendar.HOUR_OF_DAY)

                        val newTotal = total + 1
                        val newAverages = existingAverages.toMutableList()
                        newAverages[hour] =
                            ((existingAverages[hour] * total) + measurement.dbLevel) / newTotal

                        // Running average centre — same pattern as MeasurementRepositoryImpl
                        val existingLat = snapshot.getDouble("centerLatitude") ?: measurement.latitude
                        val existingLng = snapshot.getDouble("centerLongitude") ?: measurement.longitude
                        val newCenterLat = (((existingLat ?: lat) * total) + measurement.latitude!!) / newTotal
                        val newCenterLng = (((existingLng ?: lng) * total) + measurement.longitude!!) / newTotal

                        val updatedZone = NoiseZoneDto(
                            locationId = locationId,
                            centerLatitude = newCenterLat,
                            centerLongitude = newCenterLng,
                            locationName = measurement.locationName.ifBlank { "Zone $locationId" },
                            hourlyAverages = newAverages,
                            totalContributions = newTotal.toInt(),
                        )
                        transaction.set(zoneRef, updatedZone)
                    }.await()
                }

                // 3. Mark synced in Room only after both writes succeed.
                dao.markAsSynced(measurement.id)
            } catch (e: Exception) {
                e.printStackTrace()
                anyFailed = true
                // Continue processing remaining entries — don't abort the whole batch.
            }
        }

        return if (anyFailed) Result.retry() else Result.success()
    }

    /**
     * Safely converts whatever Firestore returns for hourlyAverages into
     * a clean List<Double> of exactly 24 elements.
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
        return if (list.size == 24) list
        else List(24) { i -> list.getOrElse(i) { 0.0 } }
    }
}
