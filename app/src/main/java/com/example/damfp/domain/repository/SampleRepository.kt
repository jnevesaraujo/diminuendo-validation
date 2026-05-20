package com.example.damfp.domain.repository

import com.example.damfp.domain.model.SampleItem
import kotlinx.coroutines.flow.Flow

/**
 * Interface in the DOMAIN. The implementation lives in data/ (Repository Pattern).
 * Rule: the UI depends on this interface, never on Retrofit/Room directly.
 */
interface SampleRepository {
    /** Single Source of Truth: always emits the state of the local database (Room). */
    fun observeItems(): Flow<List<SampleItem>>

    /** Hits the network and updates Room. Without network, returns failure without clearing the cache. */
    suspend fun refresh(): Result<Unit>
}
