package dam.a50274.diminuendo.domain.model

data class Measurement(
    val id: String,
    val userId: String,
    val dbLevel: Double,
    val waveform: IntArray,
    val timestamp: Long,
    val latitude: Double?,
    val longitude: Double?,
    val contextTag: String,
    val locationName: String,
    val pendingSync: Boolean = false // defaults to false; set true by repository
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Measurement

        if (id != other.id) return false
        if (userId != other.userId) return false
        if (dbLevel != other.dbLevel) return false
        if (!waveform.contentEquals(other.waveform)) return false
        if (timestamp != other.timestamp) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        if (contextTag != other.contextTag) return false
        if (locationName != other.locationName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + dbLevel.hashCode()
        result = 31 * result + waveform.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + contextTag.hashCode()
        result = 31 * result + locationName.hashCode()
        return result
    }
}
