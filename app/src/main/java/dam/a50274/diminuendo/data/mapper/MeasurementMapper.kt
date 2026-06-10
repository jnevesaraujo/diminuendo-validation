package dam.a50274.diminuendo.data.mapper

import dam.a50274.diminuendo.data.local.MeasurementEntity
import dam.a50274.diminuendo.domain.model.Measurement

fun MeasurementEntity.toDomain(): Measurement {
    return Measurement(
        id = this.id,
        userId = this.userId,
        dbLevel = this.dbLevel,
        waveform = this.waveform,
        timestamp = this.timestamp,
        latitude = this.latitude,
        longitude = this.longitude,
        contextTag = this.contextTag,
        locationName = this.locationName,
    )
}

fun Measurement.toEntity(
    pendingSync: Boolean = true,
    isDeleted: Boolean = false,
    updatedAt: Long = System.currentTimeMillis(),
): MeasurementEntity {
    return MeasurementEntity(
        id = this.id,
        userId = this.userId,
        dbLevel = this.dbLevel,
        waveform = this.waveform,
        timestamp = this.timestamp,
        latitude = this.latitude,
        longitude = this.longitude,
        contextTag = this.contextTag,
        locationName = this.locationName,
        updatedAt = updatedAt,
        pendingSync = pendingSync,
        isDeleted = isDeleted,
    )
}
