package com.example.damfp.ui.feature.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.damfp.core.ConnectivityObserver
import com.example.damfp.domain.usecase.GetSampleItemsUseCase
import com.example.damfp.domain.usecase.ObservePremiumUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Example MVVM ViewModel:
 * - combines items (SSOT Room), premium state (DataStore) and connectivity
 * - exposes a single StateFlow<SampleUiState>
 * - stays thin: the logic lives in the UseCases.
 */
@HiltViewModel
class SampleViewModel
@Inject
constructor(
    private val getItems: GetSampleItemsUseCase,
    private val observePremium: ObservePremiumUseCase,
    connectivity: ConnectivityObserver,
) : ViewModel() {
    // Locally controlled state (loading/sync error).
    private val localState = MutableStateFlow(SampleUiState(isLoading = true))

    val uiState: StateFlow<SampleUiState> =
        combine(
            getItems(),
            observePremium(),
            connectivity.isOnline(),
            localState,
        ) { items, isPremium, isOnline, local ->
            local.copy(
                items = items,
                isPremium = isPremium,
                isOffline = !isOnline,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = localState.value,
        )

    init {
        refresh()
        // When connectivity is restored, re-sync (offline strategy, docs/06).
        connectivity.isOnline()
            .onEach { online -> if (online) refresh() }
            .launchIn(viewModelScope)
    }

    fun onAction(action: SampleAction) {
        when (action) {
            SampleAction.Refresh -> refresh()
            SampleAction.TogglePremium -> togglePremium()
            is SampleAction.OpenDetail -> Unit // navigation is handled in the Composable
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            localState.update { it.copy(isLoading = true, error = null) }
            val result = getItems.refresh()
            localState.update {
                it.copy(
                    isLoading = false,
                    error =
                    result.exceptionOrNull()
                        ?.let { e -> "Failed to sync: ${e.message}" },
                )
            }
        }
    }

    private fun togglePremium() {
        viewModelScope.launch {
            observePremium.setPremium(!uiState.value.isPremium)
        }
    }
}
