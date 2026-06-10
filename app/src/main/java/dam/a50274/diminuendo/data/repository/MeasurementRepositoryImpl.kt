package dam.a50274.diminuendo.data.repository

import dam.a50274.diminuendo.data.local.MeasurementDao
import dam.a50274.diminuendo.data.mapper.toDomain
import dam.a50274.diminuendo.data.mapper.toEntity
import dam.a50274.diminuendo.domain.model.Measurement
import dam.a50274.diminuendo.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MeasurementRepositoryImpl @Inject constructor(
    private val dao: MeasurementDao,
) : MeasurementRepository {

    override fun getMeasurementsByUser(userId: String): Flow<List<Measurement>> {
        return dao.getAllByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveMeasurement(measurement: Measurement) {
        dao.insertOrReplace(measurement.toEntity())
    }

    override suspend fun deleteMeasurement(id: String) {
        dao.softDelete(id, System.currentTimeMillis())
    }
}
