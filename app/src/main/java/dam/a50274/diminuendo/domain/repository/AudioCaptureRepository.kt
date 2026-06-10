package dam.a50274.diminuendo.domain.repository

import kotlinx.coroutines.flow.Flow

interface AudioCaptureRepository {
    fun startCapture()
    fun stopCapture()
    fun decibelFlow(): Flow<Double>
}
