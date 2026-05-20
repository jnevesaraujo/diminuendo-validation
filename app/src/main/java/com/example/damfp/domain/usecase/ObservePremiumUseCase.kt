package com.example.damfp.domain.usecase

import com.example.damfp.data.local.UserPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * CENTRALIZED "entitlement" check (freemium) — do not scatter it across the UI.
 * The subscription is SIMULATED: the state lives in the DataStore (see docs/02 and docs/06).
 */
class ObservePremiumUseCase
@Inject
constructor(
    private val prefs: UserPreferencesDataStore,
) {
    operator fun invoke(): Flow<Boolean> = prefs.isPremium

    suspend fun setPremium(value: Boolean) = prefs.setPremium(value)
}
