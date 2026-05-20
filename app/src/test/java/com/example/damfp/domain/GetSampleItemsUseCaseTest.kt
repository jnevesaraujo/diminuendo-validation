package com.example.damfp.domain

import app.cash.turbine.test
import com.example.damfp.domain.model.SampleItem
import com.example.damfp.domain.repository.SampleRepository
import com.example.damfp.domain.usecase.GetSampleItemsUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example UseCase unit test with a FAKE Repository
 * (prefer fakes over mocks for repositories — see docs/11).
 */
class GetSampleItemsUseCaseTest {
    private val sample = SampleItem("1", "T", "D", null, isPremium = false)

    private val fakeRepo =
        object : SampleRepository {
            override fun observeItems() = flowOf(listOf(sample))

            override suspend fun refresh() = Result.success(Unit)
        }

    @Test
    fun `invoke emits items from the repository`() = runTest {
        val useCase = GetSampleItemsUseCase(fakeRepo)

        useCase().test {
            assertEquals(listOf(sample), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `refresh delegates to the repository`() = runTest {
        val useCase = GetSampleItemsUseCase(fakeRepo)
        assertEquals(Result.success(Unit), useCase.refresh())
    }
}
