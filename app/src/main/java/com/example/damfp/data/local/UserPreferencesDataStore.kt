package com.example.damfp.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

/**
 * DataStore: lightweight, persistent state. Here we store the SIMULATED
 * subscription state (freemium). See docs/02, docs/06, docs/08.
 */
@Singleton
class UserPreferencesDataStore
@Inject
constructor(
    private val context: Context,
) {
    private val keyPremium = booleanPreferencesKey("is_premium")

    val isPremium: Flow<Boolean> =
        context.dataStore.data.map { it[keyPremium] ?: false }

    suspend fun setPremium(value: Boolean) {
        context.dataStore.edit { it[keyPremium] = value }
    }
}
