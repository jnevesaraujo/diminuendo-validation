package dam.a50274.diminuendo.data.remote

data class MeasurementDto(
    val id: String = "",
    val userId: String = "",
    val dbLevel: Double = 0.0,
    val waveform: List<Int> = emptyList(),
    val timestamp: Long = 0L,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val contextTag: String = "",
    val locationName: String = "",
)
