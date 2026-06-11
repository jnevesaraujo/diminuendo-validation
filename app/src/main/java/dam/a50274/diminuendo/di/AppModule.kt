package dam.a50274.diminuendo.di

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context,
    ): dam.a50274.diminuendo.domain.util.NetworkMonitor {
        return dam.a50274.diminuendo.data.util.NetworkMonitorImpl(context)
    }

    @Provides
    @Singleton
    fun provideSubscriptionRepository(
        dataStore: androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences>,
    ): dam.a50274.diminuendo.domain.repository.SubscriptionRepository {
        return dam.a50274.diminuendo.data.repository.SubscriptionRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context,
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context,
        fusedLocationProviderClient: FusedLocationProviderClient,
    ): dam.a50274.diminuendo.domain.repository.LocationRepository {
        return dam.a50274.diminuendo.data.repository.LocationRepositoryImpl(context, fusedLocationProviderClient)
    }

    @Provides
    @Singleton
    fun provideAiRepository(): dam.a50274.diminuendo.domain.repository.AiRepository {
        return dam.a50274.diminuendo.data.repository.AiRepositoryImpl()
    }
}
