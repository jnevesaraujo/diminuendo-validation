package dam.a50274.diminuendo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "noise_zones")
data class NoiseZoneEntity(
    @PrimaryKey val locationId: String,
    val centerLatitude: Double,
    val centerLongitude: Double,
    val hourlyAverages: List<Double>,
    val totalContributions: Int,
)
