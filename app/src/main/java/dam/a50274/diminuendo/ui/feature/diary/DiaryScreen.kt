package dam.a50274.diminuendo.ui.feature.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dam.a50274.diminuendo.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DiaryScreenRoot(viewModel: DiaryViewModel = hiltViewModel(), onNavigateToProfile: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DiaryScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToProfile = onNavigateToProfile,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(state: DiaryUiState, onAction: (DiaryAction) -> Unit, onNavigateToProfile: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp).size(28.dp),
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter action */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                },
                windowInsets = WindowInsets(0),
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.diary_error_prefix, state.error),
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { onAction(DiaryAction.Refresh) }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
                state.measurements.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(stringResource(R.string.diary_no_measurements))
                    }
                }
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(state.measurements, key = { it.id }) { measurement ->
                                val location = measurement.locationName
                                    ?: stringResource(R.string.diary_unknown_location)
                                val itemDesc = "Measurement of ${measurement.dbLevel.toInt()} dB at $location"
                                val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                                val formattedDate = formatter.format(Date(measurement.timestamp))
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics { contentDescription = itemDesc },
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
                                                text = location,
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                            Text(
                                                text = formattedDate,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        IconButton(onClick = { onAction(DiaryAction.Delete(measurement.id)) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = stringResource(R.string.diary_delete_desc),
                                            )
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
}
