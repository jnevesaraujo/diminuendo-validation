package dam.a50274.diminuendo.fakes

import dam.a50274.diminuendo.domain.model.NoiseZone
import dam.a50274.diminuendo.domain.repository.NoiseZoneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNoiseZoneRepository : NoiseZoneRepository {
    var noiseZonesState = MutableStateFlow<List<NoiseZone>>(emptyList())
    var shouldThrowError = false

    override fun getNoiseZones(): Flow<List<NoiseZone>> {
        if (shouldThrowError) {
            throw Exception("Simulated repository error")
        }
        return noiseZonesState
    }
}
