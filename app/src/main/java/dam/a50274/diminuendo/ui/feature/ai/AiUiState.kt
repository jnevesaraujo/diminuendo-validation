package dam.a50274.diminuendo.ui.feature.ai

import dam.a50274.diminuendo.domain.model.ChatMessage

data class AiUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val isPremium: Boolean = false,
    val remainingFreePrompts: Int = 3,
    val error: String? = null,
)
