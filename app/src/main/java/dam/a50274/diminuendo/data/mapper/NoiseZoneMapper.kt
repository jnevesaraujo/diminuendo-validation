package dam.a50274.diminuendo.data.mapper

import dam.a50274.diminuendo.data.local.NoiseZoneEntity
import dam.a50274.diminuendo.data.remote.NoiseZoneDto
import dam.a50274.diminuendo.domain.model.NoiseZone

fun NoiseZoneEntity.toDomain(): NoiseZone {
    return NoiseZone(
        locationId = this.locationId,
        centerLatitude = this.centerLatitude,
        centerLongitude = this.centerLongitude,
        hourlyAverages = this.hourlyAverages,
        totalContributions = this.totalContributions
    )
}

fun NoiseZoneDto.toEntity(): NoiseZoneEntity {
    return NoiseZoneEntity(
        locationId = this.locationId,
        centerLatitude = this.centerLatitude,
        centerLongitude = this.centerLongitude,
        hourlyAverages = this.hourlyAverages,
        totalContributions = this.totalContributions
    )
}
