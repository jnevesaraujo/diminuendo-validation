package dam.a50274.diminuendo.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dam.a50274.diminuendo.data.local.PreferencesKeys
import dam.a50274.diminuendo.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SubscriptionRepository {

    override fun isPremium(): Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.IS_PREMIUM] ?: false
    }

    override suspend fun setPremium(isPremium: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_PREMIUM] = isPremium
        }
    }

    override fun getAiDailyCount(): Flow<Int> = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.AI_DAILY_COUNT] ?: 0
    }

    override suspend fun incrementAiDailyCount(newDate: Long?) {
        dataStore.edit { prefs ->
            val currentCount = prefs[PreferencesKeys.AI_DAILY_COUNT] ?: 0

            // If newDate is provided, it's a new day, so reset to 1.
            // Else, just increment the current count.
            if (newDate != null) {
                prefs[PreferencesKeys.AI_DAILY_COUNT] = 1
                prefs[PreferencesKeys.AI_COUNT_RESET_DATE] = newDate
            } else {
                prefs[PreferencesKeys.AI_DAILY_COUNT] = currentCount + 1
            }
        }
    }

    override fun getAiCountResetDate(): Flow<Long> = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.AI_COUNT_RESET_DATE] ?: 0L
    }
}
