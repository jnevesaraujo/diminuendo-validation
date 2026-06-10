package dam.a50274.diminuendo.domain.repository

import dam.a50274.diminuendo.domain.model.Measurement
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {
    fun getMeasurementsByUser(userId: String): Flow<List<Measurement>>
    suspend fun saveMeasurement(measurement: Measurement)
    suspend fun deleteMeasurement(id: String)
}
