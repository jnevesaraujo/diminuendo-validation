package com.example.damfp.ui.feature.sample

import com.example.damfp.domain.model.SampleItem

/**
 * Immutable screen state (docs/08). The Composable is a function of this state.
 * One-shot events (navigate, snackbar) do NOT go here.
 */
data class SampleUiState(
    val isLoading: Boolean = false,
    val items: List<SampleItem> = emptyList(),
    val isOffline: Boolean = false,
    val isPremium: Boolean = false,
    val error: String? = null,
)

/** Actions the UI sends to the ViewModel. */
sealed interface SampleAction {
    data object Refresh : SampleAction

    data object TogglePremium : SampleAction

    data class OpenDetail(val id: String) : SampleAction
}
