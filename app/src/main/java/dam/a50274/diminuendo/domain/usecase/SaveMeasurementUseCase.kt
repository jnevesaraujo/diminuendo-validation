package dam.a50274.diminuendo.domain.usecase

import dam.a50274.diminuendo.domain.model.Measurement
import dam.a50274.diminuendo.domain.repository.MeasurementRepository
import javax.inject.Inject

class SaveMeasurementUseCase @Inject constructor(
    private val repository: MeasurementRepository,
) {
    suspend operator fun invoke(measurement: Measurement) {
        repository.saveMeasurement(measurement)
    }
}
