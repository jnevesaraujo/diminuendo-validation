package dam.a50274.diminuendo.ui.feature.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.a50274.diminuendo.domain.model.Measurement
import dam.a50274.diminuendo.domain.usecase.DeleteMeasurementUseCase
import dam.a50274.diminuendo.domain.usecase.GetMeasurementHistoryUseCase
import dam.a50274.diminuendo.domain.usecase.SaveMeasurementUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    getMeasurementHistoryUseCase: GetMeasurementHistoryUseCase,
    private val deleteMeasurementUseCase: DeleteMeasurementUseCase,
    private val saveMeasurementUseCase: SaveMeasurementUseCase,
) : ViewModel() {

    // TODO: Uncomment for final implementation
    // private val userId = dataStore.data.map { preferences -> preferences[USER_ID_KEY] ?: "" }

    // Using a mock userId for testing purposes
    private val mockUserId = "mock_user_123"

    val uiState: StateFlow<DiaryUiState> = getMeasurementHistoryUseCase(mockUserId)
        .map { measurements ->
            DiaryUiState(
                isLoading = false,
                measurements = measurements,
                error = null,
            )
        }
        .catch { e ->
            emit(DiaryUiState(isLoading = false, error = e.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DiaryUiState(isLoading = true),
        )

    fun onAction(action: DiaryAction) {
        when (action) {
            is DiaryAction.Delete -> {
                viewModelScope.launch {
                    try {
                        deleteMeasurementUseCase(action.id)
                    } catch (e: Exception) {
                        // Silently catch in this MVP
                    }
                }
            }
            is DiaryAction.Refresh -> {
                // Not needed for Flow stream
            }
            is DiaryAction.InsertDebugEntry -> {
                viewModelScope.launch {
                    val dummy = Measurement(
                        id = UUID.randomUUID().toString(),
                        userId = mockUserId,
                        dbLevel = (40..100).random().toDouble(),
                        waveform = intArrayOf(),
                        timestamp = System.currentTimeMillis(),
                        latitude = 38.7,
                        longitude = -9.1,
                        contextTag = "Debug",
                        locationName = "Test Location",
                    )
                    saveMeasurementUseCase(dummy)
                }
            }
        }
    }
}
