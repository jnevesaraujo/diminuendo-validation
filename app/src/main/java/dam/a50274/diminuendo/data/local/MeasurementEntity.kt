package dam.a50274.diminuendo.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "measurements",
    indices = [
        Index("userId"),
        Index("pendingSync"),
    ],
)
data class MeasurementEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val dbLevel: Double,
    val waveform: IntArray,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val contextTag: String,
    val locationName: String,
    val updatedAt: Long,
    val pendingSync: Boolean,
    val isDeleted: Boolean,
)
