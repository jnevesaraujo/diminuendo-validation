package dam.a50274.diminuendo.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import dam.a50274.diminuendo.data.local.NoiseZoneDao
import dam.a50274.diminuendo.data.mapper.toDomain
import dam.a50274.diminuendo.data.mapper.toEntity
import dam.a50274.diminuendo.data.remote.NoiseZoneDto
import dam.a50274.diminuendo.domain.model.NoiseZone
import dam.a50274.diminuendo.domain.repository.NoiseZoneRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoiseZoneRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val noiseZoneDao: NoiseZoneDao,
) : NoiseZoneRepository {

    override fun getNoiseZones(): Flow<List<NoiseZone>> {
        val syncFlow = callbackFlow {
            val listener = firestore.collection("noise_zones")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener

                    // Use manual mapping instead of toObjects() to safely handle
                    // Firestore's Number type ambiguity for List<Double> fields.
                    val dtos = snapshot.documents.mapNotNull { doc ->
                        try {
                            val rawAverages = doc.get("hourlyAverages")
                            // Firestore may deserialize whole-number doubles as Long.
                            // Explicitly cast each element to Double regardless of source type.
                            val hourlyAverages: List<Double> = when (rawAverages) {
                                is List<*> -> rawAverages.map { element ->
                                    when (element) {
                                        is Double -> element
                                        is Long -> element.toDouble()
                                        is Int -> element.toDouble()
                                        is Number -> element.toDouble()
                                        else -> 0.0
                                    }
                                }
                                else -> List(24) { 0.0 }
                            }

                            NoiseZoneDto(
                                locationId = doc.getString("locationId") ?: doc.id,
                                centerLatitude = doc.getDouble("centerLatitude") ?: 0.0,
                                centerLongitude = doc.getDouble("centerLongitude") ?: 0.0,
                                locationName = doc.getString("locationName") ?: "",
                                hourlyAverages = hourlyAverages,
                                totalContributions = (doc.getLong("totalContributions") ?: 0L).toInt(),
                            )
                        } catch (e: Exception) {
                            // Log and skip malformed documents rather than crashing
                            android.util.Log.w("NoiseZoneRepo", "Skipping malformed doc ${doc.id}: ${e.message}")
                            null
                        }
                    }

                    // Use a stable scope not tied to the collector's lifetime
                    CoroutineScope(Dispatchers.IO).launch {
                        noiseZoneDao.insertAll(dtos.map { it.toEntity() })
                    }
                    trySend(Unit)
                }

            awaitClose { listener.remove() }
        }

        return noiseZoneDao.getAllNoiseZones().combine(syncFlow) { entities, _ ->
            entities.map { it.toDomain() }
        }
    }
}
