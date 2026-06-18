package dam.a50274.diminuendo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        MeasurementEntity::class,
        NoiseZoneEntity::class,
    ],
    version = 4, // bumped from 3 — MeasurementEntity schema changed (pendingSync surfaced to domain)
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDao
    abstract fun noiseZoneDao(): NoiseZoneDao
}
