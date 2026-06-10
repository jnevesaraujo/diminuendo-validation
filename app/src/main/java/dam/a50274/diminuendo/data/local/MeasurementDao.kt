package dam.a50274.diminuendo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(measurement: MeasurementEntity)

    @Query("SELECT * FROM measurements WHERE userId = :userId AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getAllByUser(userId: String): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM measurements WHERE pendingSync = 1 AND isDeleted = 0")
    suspend fun getPendingSync(): List<MeasurementEntity>

    @Query("UPDATE measurements SET pendingSync = 0 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("UPDATE measurements SET isDeleted = 1, pendingSync = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, updatedAt: Long)
}
