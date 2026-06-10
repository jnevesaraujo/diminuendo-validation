package dam.a50274.diminuendo.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import dam.a50274.diminuendo.data.local.MeasurementDao
import dam.a50274.diminuendo.data.mapper.toDomain
import dam.a50274.diminuendo.data.mapper.toDto
import dam.a50274.diminuendo.data.mapper.toEntity
import dam.a50274.diminuendo.domain.model.Measurement
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
) : MeasurementRepository {

    override fun getMeasurementsByUser(userId: String): Flow<List<Measurement>> {
        return dao.getAllByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveMeasurement(measurement: Measurement) {
        // Save to Room first as SSOT with pendingSync = true initially
        dao.insertOrReplace(measurement.toEntity(pendingSync = true))

        try {
            withTimeout(5000L) {
                // Upload Measurement
                firestore.collection("users")
                    .document(measurement.userId)
                    .collection("measurements")
                    .document(measurement.id)
                    .set(measurement.toDto())
                    .await()

                // Calculate Geohash/Zone ID (Rounding to 2 decimal places for ~1.1km grid)
                val lat = Math.round(measurement.latitude * 100.0) / 100.0
                val lng = Math.round(measurement.longitude * 100.0) / 100.0
                val locationId = "zone_${lat}_${lng}"

                // Update Noise Zone via Transaction
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
                        "hourlyAverages" to newAverages
                    )

                    transaction.set(zoneRef, updateData)
                }.await()

                // Mark synced in Room
                dao.insertOrReplace(measurement.toEntity(pendingSync = false))
            }
        } catch (e: Exception) {
            // Ignored: Will be picked up by WorkManager later since pendingSync remains true
        }
    }

    override suspend fun deleteMeasurement(id: String) {
        dao.softDelete(id, System.currentTimeMillis())
    }
}
