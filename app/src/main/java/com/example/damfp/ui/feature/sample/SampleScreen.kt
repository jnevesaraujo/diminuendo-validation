package com.example.damfp.ui.feature.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.damfp.domain.model.SampleItem
import com.example.damfp.ui.components.ErrorView
import com.example.damfp.ui.components.LoadingIndicator
import com.example.damfp.ui.components.OfflineBanner
import com.example.damfp.ui.theme.DamFpTheme

/** Stateful entry point (connects ViewModel). Stays thin. */
@Composable
fun SampleRoute(onOpenDetail: (String) -> Unit, viewModel: SampleViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SampleScreen(
        state = state,
        onAction = { action ->
            if (action is SampleAction.OpenDetail) onOpenDetail(action.id)
            viewModel.onAction(action)
        },
    )
}

/** STATELESS Composable — testable and previewable. A function of the state (docs/08). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleScreen(state: SampleUiState, onAction: (SampleAction) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Items (example)") },
                actions = {
                    IconButton(onClick = { onAction(SampleAction.TogglePremium) }) {
                        Text(if (state.isPremium) "PRO" else "FREE")
                    }
                    IconButton(onClick = { onAction(SampleAction.Refresh) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            if (state.isOffline) {
                OfflineBanner("No connection — showing cached data")
            }
            when {
                state.isLoading && state.items.isEmpty() -> LoadingIndicator()
                state.error != null && state.items.isEmpty() ->
                    ErrorView(state.error, onRetry = { onAction(SampleAction.Refresh) })
                state.items.isEmpty() ->
                    Text(
                        "Nothing to show",
                        modifier = Modifier.padding(24.dp),
                    )
                else ->
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(state.items, key = { it.id }) { item ->
                            SampleItemCard(
                                item = item,
                                locked = item.isPremium && !state.isPremium,
                                onClick = { onAction(SampleAction.OpenDetail(item.id)) },
                            )
                        }
                    }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SampleItemCard(item: SampleItem, locked: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        // a premium item is locked on the free plan (simulated paywall)
        enabled = !locked,
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = if (locked) "🔒 ${item.title}" else item.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text =
                if (locked) {
                    "Subscriber-only content (tap PRO/FREE to simulate)"
                } else {
                    item.description
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SampleScreenPreview() {
    DamFpTheme {
        SampleScreen(
            state =
            SampleUiState(
                items =
                listOf(
                    SampleItem("1", "Free item", "Description", null, isPremium = false),
                    SampleItem("2", "Premium item", "Locked", null, isPremium = true),
                ),
                isOffline = true,
            ),
            onAction = {},
        )
    }
}
