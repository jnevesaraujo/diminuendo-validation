package dam.a50274.diminuendo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoiseZoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoiseZone(noiseZone: NoiseZoneEntity)

    @Query("SELECT * FROM noise_zones")
    suspend fun getAllNoiseZones(): List<NoiseZoneEntity>
}
