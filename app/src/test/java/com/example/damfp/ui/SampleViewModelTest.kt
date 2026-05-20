package com.example.damfp.ui

import app.cash.turbine.test
import com.example.damfp.MainDispatcherRule
import com.example.damfp.core.ConnectivityObserver
import com.example.damfp.domain.model.SampleItem
import com.example.damfp.domain.usecase.GetSampleItemsUseCase
import com.example.damfp.domain.usecase.ObservePremiumUseCase
import com.example.damfp.ui.feature.sample.SampleViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Example ViewModel test: verifies that the state combines items (Room),
 * premium (DataStore) and connectivity. Uses MockK for the collaborators.
 */
class SampleViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val item = SampleItem("1", "Title", "Desc", null, isPremium = false)

    @Test
    fun `uiState reflects items and offline state`() = runTest {
        val getItems =
            mockk<GetSampleItemsUseCase>(relaxed = true) {
                every { this@mockk.invoke() } returns flowOf(listOf(item))
                coEvery { refresh() } returns Result.success(Unit)
            }
        val premium =
            mockk<ObservePremiumUseCase>(relaxed = true) {
                every { this@mockk.invoke() } returns flowOf(false)
            }
        val connectivity =
            mockk<ConnectivityObserver> {
                every { isOnline() } returns flowOf(false)
            }

        val vm = SampleViewModel(getItems, premium, connectivity)

        vm.uiState.test {
            val state = expectMostRecentItem()
            assertEquals(listOf(item), state.items)
            assertTrue(state.isOffline)
            assertEquals(false, state.isPremium)
        }
    }
}
