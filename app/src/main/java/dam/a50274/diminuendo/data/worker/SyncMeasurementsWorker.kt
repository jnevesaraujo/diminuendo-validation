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
import kotlinx.coroutines.tasks.await
import java.util.Calendar

@HiltWorker
class SyncMeasurementsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dao: MeasurementDao,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val pending = dao.getPendingSync()
        if (pending.isEmpty()) {
            return Result.success()
        }

        var anyFailed = false

        for (entity in pending) {
            try {
                val measurement = entity.toDomain()
                
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
                        val averages = snapshot.get("hourlyAverages") as? List<Double> ?: List(24) { 0.0 }

                        val cal = Calendar.getInstance().apply { timeInMillis = measurement.timestamp }
                        val hour = cal.get(Calendar.HOUR_OF_DAY)

                        val newTotal = total + 1
                        val newAverages = averages.toMutableList()
                        newAverages[hour] = ((averages[hour] * total) + measurement.dbLevel) / newTotal

                        val updateData = mapOf(
                            "locationId" to locationId,
                            "centerLatitude" to lat,
                            "centerLongitude" to lng,
                            "totalContributions" to newTotal.toInt(),
                            "hourlyAverages" to newAverages,
                        )

                        transaction.set(zoneRef, updateData)
                    }.await()
                }

                dao.markAsSynced(measurement.id)
            } catch (e: Exception) {
                e.printStackTrace()
                anyFailed = true
            }
        }

        return if (anyFailed) Result.retry() else Result.success()
    }
}
