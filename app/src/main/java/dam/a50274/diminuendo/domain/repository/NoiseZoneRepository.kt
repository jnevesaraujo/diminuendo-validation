package dam.a50274.diminuendo.domain.repository

import dam.a50274.diminuendo.domain.model.NoiseZone
import kotlinx.coroutines.flow.Flow

interface NoiseZoneRepository {
    fun getNoiseZones(): Flow<List<NoiseZone>>
}
