package dam.a50274.diminuendo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoiseZoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoiseZone(noiseZone: NoiseZoneEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(noiseZones: List<NoiseZoneEntity>)

    @Query("SELECT * FROM noise_zones")
    fun getAllNoiseZones(): Flow<List<NoiseZoneEntity>>
}
