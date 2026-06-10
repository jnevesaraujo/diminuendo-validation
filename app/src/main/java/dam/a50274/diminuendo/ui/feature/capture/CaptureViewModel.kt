package dam.a50274.diminuendo.ui.feature.capture

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.a50274.diminuendo.data.local.PreferencesKeys
import dam.a50274.diminuendo.domain.model.Measurement
import dam.a50274.diminuendo.domain.repository.AudioCaptureRepository
import dam.a50274.diminuendo.domain.usecase.SaveMeasurementUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val audioCaptureRepository: AudioCaptureRepository,
    private val saveMeasurementUseCase: SaveMeasurementUseCase,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CaptureUiState())
    val uiState: StateFlow<CaptureUiState> = _uiState.asStateFlow()

    private var captureJob: Job? = null
    private var timerJob: Job? = null

    private val waveformSize = 20
    private var dbReadingsSum = 0.0
    private var dbReadingsCount = 0

    private var currentUserId: String = ""

    init {
        viewModelScope.launch {
            dataStore.data.collect { prefs ->
                currentUserId = prefs[PreferencesKeys.USER_ID] ?: ""
            }
        }
    }

    fun onAction(action: CaptureAction) {
        when (action) {
            is CaptureAction.ToggleRecording -> {
                if (_uiState.value.isRecording) {
                    stopRecording()
                } else {
                    startRecording()
                }
            }
            is CaptureAction.SaveMeasurement -> {
                saveMeasurement()
            }
            is CaptureAction.AcknowledgeError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun startRecording() {
        _uiState.update {
            it.copy(
                isRecording = true,
                currentDb = 0.0,
                averageDb = 0.0,
                peakDb = 0.0,
                elapsedSeconds = 0,
                latestWaveform = emptyList(),
                error = null,
            )
        }
        dbReadingsSum = 0.0
        dbReadingsCount = 0

        audioCaptureRepository.startCapture()

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }

        captureJob = viewModelScope.launch {
            audioCaptureRepository.decibelFlow().collect { db ->
                dbReadingsSum += db
                dbReadingsCount++

                _uiState.update { state ->
                    val newWaveform = (state.latestWaveform + db).takeLast(waveformSize)
                    state.copy(
                        currentDb = db,
                        peakDb = max(state.peakDb, db),
                        averageDb = if (dbReadingsCount > 0) dbReadingsSum / dbReadingsCount else 0.0,
                        latestWaveform = newWaveform,
                    )
                }
            }
        }
    }

    private fun stopRecording() {
        audioCaptureRepository.stopCapture()
        captureJob?.cancel()
        timerJob?.cancel()
        _uiState.update { it.copy(isRecording = false) }
    }

    private fun saveMeasurement() {
        if (_uiState.value.isRecording) {
            stopRecording()
        }

        val state = _uiState.value

        // Normalize waveform to 0-100 IntArray
        val maxDb = state.latestWaveform.maxOrNull()?.takeIf { it > 0 } ?: 1.0
        val normalizedWaveform = state.latestWaveform.map { (it / maxDb * 100).toInt() }.toIntArray()

        val measurement = Measurement(
            id = UUID.randomUUID().toString(),
            userId = currentUserId.ifEmpty { "debug_user" },
            dbLevel = state.averageDb,
            waveform = normalizedWaveform,
            timestamp = System.currentTimeMillis(),
            // GPS omitted for MVP
            latitude = 0.0,
            longitude = 0.0,
            contextTag = "Capture",
            locationName = "Captured Location",
        )

        viewModelScope.launch {
            try {
                saveMeasurementUseCase(measurement)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to save measurement") }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioCaptureRepository.stopCapture()
    }
}
