package dam.a50274.diminuendo.domain.model

data class NoiseZone(
    val locationId: String,
    val centerLatitude: Double,
    val centerLongitude: Double,
    val locationName: String = "",
    val hourlyAverages: List<Double>,
    val totalContributions: Int,
)
