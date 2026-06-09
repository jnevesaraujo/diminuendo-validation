package dam.a50274.diminuendo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MeasurementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: MeasurementEntity)

    @Query("SELECT * FROM measurements WHERE userId = :userId")
    suspend fun getMeasurementsByUserId(userId: String): List<MeasurementEntity>
}
