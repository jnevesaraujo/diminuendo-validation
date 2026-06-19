package dam.a50274.diminuendo.ui.feature.diary

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.a50274.diminuendo.data.local.PreferencesKeys
import dam.a50274.diminuendo.domain.model.Measurement
import dam.a50274.diminuendo.domain.usecase.DeleteMeasurementUseCase
import dam.a50274.diminuendo.domain.usecase.GetMeasurementHistoryUseCase
import dam.a50274.diminuendo.domain.usecase.SaveMeasurementUseCase
import dam.a50274.diminuendo.domain.util.NetworkMonitor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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
    private val dataStore: DataStore<Preferences>,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    val uiState: StateFlow<DiaryUiState> = combine(
        dataStore.data
            .map { it[PreferencesKeys.USER_ID] ?: "" }
            .flatMapLatest { userId ->
                if (userId.isEmpty()) {
                    flowOf(DiaryUiState(isLoading = false, error = "Not authenticated"))
                } else {
                    getMeasurementHistoryUseCase(userId).map { measurements ->
                        DiaryUiState(isLoading = false, measurements = measurements)
                    }
                }
            }
            .catch { e -> emit(DiaryUiState(isLoading = false, error = e.message ?: "Unknown error")) },
        networkMonitor.isOnline,
    ) { diaryState, isOnline ->
        diaryState.copy(isOffline = !isOnline)
    }.stateIn(
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
                        // Fallback for debug insert
                        userId = "debug_user",
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
