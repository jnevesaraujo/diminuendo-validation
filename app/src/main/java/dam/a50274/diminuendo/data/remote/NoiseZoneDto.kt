package dam.a50274.diminuendo.data.remote

data class NoiseZoneDto(
    val locationId: String = "",
    val centerLatitude: Double = 0.0,
    val centerLongitude: Double = 0.0,
    val hourlyAverages: List<Double> = List(24) { 0.0 },
    val totalContributions: Int = 0,
)
