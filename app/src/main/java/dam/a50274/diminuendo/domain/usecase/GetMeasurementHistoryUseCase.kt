package dam.a50274.diminuendo.domain.usecase

import dam.a50274.diminuendo.domain.model.Measurement
import dam.a50274.diminuendo.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMeasurementHistoryUseCase @Inject constructor(
    private val repository: MeasurementRepository,
) {
    operator fun invoke(userId: String): Flow<List<Measurement>> {
        return repository.getMeasurementsByUser(userId)
    }
}
