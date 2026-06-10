package dam.a50274.diminuendo.ui.feature.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DiaryScreenRoot(viewModel: DiaryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DiaryScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
fun DiaryScreen(state: DiaryUiState, onAction: (DiaryAction) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            state.error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { onAction(DiaryAction.Refresh) }) {
                        Text("Retry")
                    }
                }
            }
            state.measurements.isEmpty() -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("No measurements yet. Start capturing!")
                    Spacer(modifier = Modifier.height(16.dp))
                    // TODO: remove before final build
                    Button(onClick = { onAction(DiaryAction.InsertDebugEntry) }) {
                        Text("Insert Test Entry (Debug)")
                    }
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // TODO: remove before final build
                    Button(
                        onClick = { onAction(DiaryAction.InsertDebugEntry) },
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp),
                    ) {
                        Text("Insert Test Entry (Debug)")
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.measurements, key = { it.id }) { measurement ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column {
                                        Text(
                                            text = "${measurement.dbLevel.toInt()} dB",
                                            style = MaterialTheme.typography.titleLarge,
                                        )
                                        Text(
                                            text = measurement.locationName ?: "Unknown Location",
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                    IconButton(onClick = { onAction(DiaryAction.Delete(measurement.id)) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
