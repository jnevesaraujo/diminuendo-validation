package com.example.damfp.data.repository

import com.example.damfp.data.local.SampleDao
import com.example.damfp.data.mapper.toDomain
import com.example.damfp.data.mapper.toEntity
import com.example.damfp.data.remote.SampleApi
import com.example.damfp.domain.model.SampleItem
import com.example.damfp.domain.repository.SampleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository implementation (data layer).
 *
 * Offline strategy (docs/06): **Single Source of Truth = Room**.
 * - `observeItems()` always emits whatever is in the local DB.
 * - `refresh()` hits the network and updates the DB; if it fails (e.g. offline),
 *   it returns `Result.failure` WITHOUT clearing the cache — the UI keeps showing data.
 */
class SampleRepositoryImpl
@Inject
constructor(
    private val api: SampleApi,
    private val dao: SampleDao,
) : SampleRepository {
    override fun observeItems(): Flow<List<SampleItem>> = dao.observeAll().map {
            list ->
        list.map { it.toDomain() }
    }

    override suspend fun refresh(): Result<Unit> = runCatching {
        val remote = api.getItems()
        dao.upsertAll(remote.map { it.toEntity() })
    }
}
