package dam.a50274.diminuendo.data.repository

import android.util.Log
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

private const val TAG = "NoiseZoneRepo"

class NoiseZoneRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val noiseZoneDao: NoiseZoneDao,
) : NoiseZoneRepository {

    override fun getNoiseZones(): Flow<List<NoiseZone>> {
        val syncFlow = callbackFlow {
            val listener = firestore.collection("noise_zones")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Log the real error — this is how you catch Firestore
                        // security rule denials (PERMISSION_DENIED) or network errors.
                        Log.e(TAG, "Firestore snapshot error: ${error.code} — ${error.message}")
                        // Still emit so combine() is not permanently blocked.
                        trySend(Unit)
                        return@addSnapshotListener
                    }

                    if (snapshot == null) {
                        trySend(Unit)
                        return@addSnapshotListener
                    }

                    Log.d(TAG, "Snapshot received: ${snapshot.documents.size} noise_zones documents")

                    val dtos = snapshot.documents.mapNotNull { doc ->
                        try {
                            val rawAverages = doc.get("hourlyAverages")
                            val hourlyAverages: List<Double> = when (rawAverages) {
                                is List<*> -> rawAverages.map { element ->
                                    when (element) {
                                        is Double -> element
                                        is Long -> element.toDouble()
                                        is Int -> element.toDouble()
                                        is Number -> element.toDouble()
                                        else -> 0.0
                                    }
                                }.let { list ->
                                    if (list.size == 24) {
                                        list
                                    } else {
                                        List(24) { i -> list.getOrElse(i) { 0.0 } }
                                    }
                                }
                                else -> List(24) { 0.0 }
                            }

                            val dto = NoiseZoneDto(
                                locationId = doc.getString("locationId") ?: doc.id,
                                centerLatitude = doc.getDouble("centerLatitude") ?: 0.0,
                                centerLongitude = doc.getDouble("centerLongitude") ?: 0.0,
                                locationName = doc.getString("locationName") ?: "",
                                hourlyAverages = hourlyAverages,
                                totalContributions = (doc.getLong("totalContributions") ?: 0L).toInt(),
                            )

                            Log.d(
                                TAG,
                                "Parsed zone: ${dto.locationId} " +
                                    "lat=${dto.centerLatitude} lng=${dto.centerLongitude} " +
                                    "contributions=${dto.totalContributions}",
                            )
                            dto
                        } catch (e: Exception) {
                            Log.w(TAG, "Skipping malformed doc ${doc.id}: ${e.message}")
                            null
                        }
                    }

                    // Write to Room on a stable IO scope, then signal combine().
                    // Using a stable scope (not the callbackFlow scope) so the insert
                    // is not cancelled if the collector is temporarily inactive.
                    CoroutineScope(Dispatchers.IO).launch {
                        noiseZoneDao.insertAll(dtos.map { it.toEntity() })
                        Log.d(TAG, "Inserted ${dtos.size} zones into Room")
                    }

                    // Unblock combine() — Room's getAllNoiseZones() Flow will
                    // emit again automatically when the insert above completes.
                    trySend(Unit)
                }

            awaitClose {
                Log.d(TAG, "Removing Firestore snapshot listener")
                listener.remove()
            }
        }

        return noiseZoneDao.getAllNoiseZones().combine(syncFlow) { entities, _ ->
            Log.d(TAG, "combine() emitting ${entities.size} zones from Room")
            entities.map { it.toDomain() }
        }
    }
}
