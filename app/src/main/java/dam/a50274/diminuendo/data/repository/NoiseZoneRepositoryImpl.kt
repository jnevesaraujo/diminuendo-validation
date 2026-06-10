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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoiseZoneRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val noiseZoneDao: NoiseZoneDao,
) : NoiseZoneRepository {

    init {
        // Start listening to the noise_zones collection
        firestore.collection("noise_zones")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val dtos = snapshot.toObjects(NoiseZoneDto::class.java)
                    CoroutineScope(Dispatchers.IO).launch {
                        noiseZoneDao.insertAll(dtos.map { it.toEntity() })
                    }
                }
            }
    }

    override fun getNoiseZones(): Flow<List<NoiseZone>> {
        return noiseZoneDao.getAllNoiseZones().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
