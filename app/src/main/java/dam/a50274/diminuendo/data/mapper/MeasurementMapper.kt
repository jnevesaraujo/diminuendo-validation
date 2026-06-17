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
        pendingSync = this.pendingSync,
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

fun dam.a50274.diminuendo.data.remote.MeasurementDto.toEntity(
    pendingSync: Boolean = false,
    isDeleted: Boolean = false,
    updatedAt: Long = System.currentTimeMillis(),
): MeasurementEntity {
    return MeasurementEntity(
        id = this.id,
        userId = this.userId,
        dbLevel = this.dbLevel,
        waveform = this.waveform.toIntArray(),
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

fun Measurement.toDto(): dam.a50274.diminuendo.data.remote.MeasurementDto {
    return dam.a50274.diminuendo.data.remote.MeasurementDto(
        id = this.id,
        userId = this.userId,
        dbLevel = this.dbLevel,
        waveform = this.waveform.toList(),
        timestamp = this.timestamp,
        latitude = this.latitude,
        longitude = this.longitude,
        contextTag = this.contextTag,
        locationName = this.locationName,
    )
}
