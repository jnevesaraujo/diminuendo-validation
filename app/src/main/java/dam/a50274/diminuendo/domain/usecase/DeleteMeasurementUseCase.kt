package dam.a50274.diminuendo.domain.usecase

import dam.a50274.diminuendo.domain.repository.MeasurementRepository
import javax.inject.Inject

class DeleteMeasurementUseCase @Inject constructor(
    private val repository: MeasurementRepository,
) {
    suspend operator fun invoke(id: String) {
        repository.deleteMeasurement(id)
    }
}
