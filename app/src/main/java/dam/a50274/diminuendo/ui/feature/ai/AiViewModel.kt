package dam.a50274.diminuendo.ui.feature.ai

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.a50274.diminuendo.data.local.PreferencesKeys
import dam.a50274.diminuendo.domain.model.ChatMessage
import dam.a50274.diminuendo.domain.repository.AiRepository
import dam.a50274.diminuendo.domain.repository.MeasurementRepository
import dam.a50274.diminuendo.domain.usecase.CheckEntitlementUseCase
import dam.a50274.diminuendo.domain.util.NetworkMonitor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed class AiEvent {
    object NavigateToPaywall : AiEvent()
}

sealed class AiAction {
    data class SendMessage(val prompt: String) : AiAction()
    object AcknowledgeError : AiAction()
}

@HiltViewModel
class AiViewModel @Inject constructor(
    private val aiRepository: AiRepository,
    private val measurementRepository: MeasurementRepository,
    private val checkEntitlementUseCase: CheckEntitlementUseCase,
    private val networkMonitor: NetworkMonitor,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    private val _events = Channel<AiEvent>()
    val events = _events.receiveAsFlow()

    private val messagesFlow = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val isLoadingFlow = MutableStateFlow(false)
    private val errorFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AiUiState> = combine(
        combine(messagesFlow, isLoadingFlow, errorFlow) { m, l, e -> Triple(m, l, e) },
        networkMonitor.isOnline,
        checkEntitlementUseCase.isPremium,
        checkEntitlementUseCase.getRemainingFreePrompts(),
    ) { (messages, isLoading, error), isOnline, isPremium, remainingPrompts ->
        AiUiState(
            messages = messages,
            isLoading = isLoading,
            isOffline = !isOnline,
            isPremium = isPremium,
            remainingFreePrompts = remainingPrompts,
            error = error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AiUiState(isLoading = true),
    )

    fun onAction(action: AiAction) {
        when (action) {
            is AiAction.SendMessage -> sendMessage(action.prompt)
            is AiAction.AcknowledgeError -> errorFlow.value = null
        }
    }

    private fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        viewModelScope.launch {
            if (!checkEntitlementUseCase.checkAndConsumeAiPrompt()) {
                _events.send(AiEvent.NavigateToPaywall)
                return@launch
            }

            val userMessage = ChatMessage(text = prompt, isUser = true)
            messagesFlow.update { it + userMessage }
            isLoadingFlow.value = true
            errorFlow.value = null

            try {
                val userId = dataStore.data.firstOrNull()?.get(PreferencesKeys.USER_ID) ?: ""
                val measurements = measurementRepository.getMeasurementsByUser(userId).firstOrNull() ?: emptyList()
                val last10 = measurements.take(10)

                val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                val contextString = last10.joinToString(separator = "\n") { m ->
                    "- ${sdf.format(
                        Date(m.timestamp),
                    )}: ${m.dbLevel.toInt()} dB at ${m.locationName ?: "Unknown Location"}"
                }

                val history = messagesFlow.value.dropLast(1) // exclude current prompt
                val responseFlow = aiRepository.sendMessage(prompt, contextString, history)

                var botMessage = ChatMessage(text = "", isUser = false)
                messagesFlow.update { it + botMessage }

                responseFlow.collect { chunk ->
                    botMessage = botMessage.copy(text = botMessage.text + chunk)
                    messagesFlow.update { current ->
                        current.map { if (it.id == botMessage.id) botMessage else it }
                    }
                }
            } catch (e: Exception) {
                errorFlow.value = "Failed to communicate with AI: ${e.message}"
            } finally {
                isLoadingFlow.value = false
            }
        }
    }
}
