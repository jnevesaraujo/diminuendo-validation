package com.example.damfp.domain.usecase

import com.example.damfp.domain.model.SampleItem
import com.example.damfp.domain.repository.SampleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase: encapsulates the item-fetching rule. Keeps the ViewModel thin and testable.
 * Replace/expand with the real logic of your project.
 */
class GetSampleItemsUseCase
@Inject
constructor(
    private val repository: SampleRepository,
) {
    operator fun invoke(): Flow<List<SampleItem>> = repository.observeItems()

    suspend fun refresh(): Result<Unit> = repository.refresh()
}
