package dam.a50274.diminuendo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dam.a50274.diminuendo.data.repository.AudioCaptureRepositoryImpl
import dam.a50274.diminuendo.data.repository.MeasurementRepositoryImpl
import dam.a50274.diminuendo.domain.repository.AudioCaptureRepository
import dam.a50274.diminuendo.domain.repository.MeasurementRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindMeasurementRepository(measurementRepositoryImpl: MeasurementRepositoryImpl): MeasurementRepository

    @Binds
    abstract fun bindAudioCaptureRepository(
        audioCaptureRepositoryImpl: AudioCaptureRepositoryImpl,
    ): AudioCaptureRepository

    @Binds
    abstract fun bindNoiseZoneRepository(
        noiseZoneRepositoryImpl: dam.a50274.diminuendo.data.repository.NoiseZoneRepositoryImpl,
    ): dam.a50274.diminuendo.domain.repository.NoiseZoneRepository
}
