package dam.a50274.diminuendo.ui.feature.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dam.a50274.diminuendo.R
import dam.a50274.diminuendo.domain.model.ChatMessage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiConsultantScreen(viewModel: AiViewModel = hiltViewModel(), onNavigateToPaywall: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AiEvent.NavigateToPaywall -> onNavigateToPaywall()
            }
        }
    }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (state.isOffline) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(R.string.ai_offline_error),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            state = listState,
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            if (state.messages.isEmpty() && !state.isOffline) {
                item {
                    Text(
                        stringResource(R.string.ai_ask_placeholder),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SuggestionChip(
                            onClick = { viewModel.onAction(AiAction.SendMessage("Was my route today safe?")) },
                            label = { Text(stringResource(R.string.ai_chip_route_safety)) },
                            modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                        )
                        SuggestionChip(
                            onClick = { viewModel.onAction(AiAction.SendMessage("Am I listening to music too loud?")) },
                            label = { Text(stringResource(R.string.ai_chip_loud_music)) },
                            modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                        )
                        SuggestionChip(
                            onClick = { viewModel.onAction(AiAction.SendMessage("What is 85dB equivalent to?")) },
                            label = { Text(stringResource(R.string.ai_chip_85db)) },
                            modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                        )
                    }
                }
            }

            items(state.messages) { message ->
                ChatBubble(message)
            }

            if (state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }

            state.error?.let { error ->
                item {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }

        if (!state.isPremium && !state.isOffline) {
            Text(
                text = stringResource(R.string.ai_free_prompts_remaining, state.remainingFreePrompts, 3),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(4.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.ai_type_message)) },
                enabled = !state.isOffline && !state.isLoading,
                shape = RoundedCornerShape(24.dp),
            )
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.onAction(AiAction.SendMessage(inputText))
                        inputText = ""
                    }
                },
                enabled = !state.isOffline && !state.isLoading && inputText.isNotBlank(),
            ) {
                Icon(Icons.Default.Send, contentDescription = stringResource(R.string.ai_send_message_desc))
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        val sender = if (isUser) "you" else "Diminuendo AI"
        val bubbleDesc = "Message from $sender: ${message.text}"
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .semantics { contentDescription = bubbleDesc }
                .background(
                    color = if (isUser) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(16.dp).copy(
                        bottomEnd = if (isUser) {
                            RoundedCornerShape(0.dp).bottomEnd
                        } else {
                            RoundedCornerShape(16.dp).bottomEnd
                        },
                        bottomStart = if (!isUser) {
                            RoundedCornerShape(0.dp).bottomStart
                        } else {
                            RoundedCornerShape(16.dp).bottomStart
                        },
                    ),
                )
                .padding(12.dp),
        ) {
            Text(
                text = message.text,
                color = if (isUser) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
