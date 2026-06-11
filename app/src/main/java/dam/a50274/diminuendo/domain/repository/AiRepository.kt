package dam.a50274.diminuendo.domain.repository

import dam.a50274.diminuendo.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    fun sendMessage(prompt: String, context: String, history: List<ChatMessage>): Flow<String>
}
