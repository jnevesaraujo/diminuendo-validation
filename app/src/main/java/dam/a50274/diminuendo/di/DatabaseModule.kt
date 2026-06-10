package dam.a50274.diminuendo.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dam.a50274.diminuendo.data.local.AppDatabase
import dam.a50274.diminuendo.data.local.MeasurementDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "diminuendo_db",
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideMeasurementDao(database: AppDatabase): MeasurementDao {
        return database.measurementDao()
    }

    @Provides
    fun provideNoiseZoneDao(database: AppDatabase): dam.a50274.diminuendo.data.local.NoiseZoneDao {
        return database.noiseZoneDao()
    }
}
