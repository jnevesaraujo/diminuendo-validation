package dam.a50274.diminuendo.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dam.a50274.diminuendo.BuildConfig
import dam.a50274.diminuendo.domain.model.ChatMessage
import dam.a50274.diminuendo.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor() : AiRepository {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
    )

    override fun sendMessage(prompt: String, context: String, history: List<ChatMessage>): Flow<String> = flow {
        val chatHistory = history.map { msg ->
            content(role = if (msg.isUser) "user" else "model") { text(msg.text) }
        }
        val chat = generativeModel.startChat(history = chatHistory)

        val fullPrompt = if (history.isEmpty()) {
            "System Context (Measurements of user's recent noise exposure," +
                " NEVER mention audio waveform data just dB and locations):\n$context\n\nUser Question:\n$prompt"
        } else {
            prompt
        }

        chat.sendMessageStream(fullPrompt).collect { chunk ->
            emit(chunk.text ?: "")
        }
    }
}
