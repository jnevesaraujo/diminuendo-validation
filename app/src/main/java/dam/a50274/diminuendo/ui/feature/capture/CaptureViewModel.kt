package dam.a50274.diminuendo.ui.feature.capture

import android.content.Context
import android.location.Geocoder
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dam.a50274.diminuendo.data.local.PreferencesKeys
import dam.a50274.diminuendo.domain.model.Measurement
import dam.a50274.diminuendo.domain.model.SyncException
import dam.a50274.diminuendo.domain.repository.AudioCaptureRepository
import dam.a50274.diminuendo.domain.repository.LocationRepository
import dam.a50274.diminuendo.domain.usecase.SaveMeasurementUseCase
import dam.a50274.diminuendo.domain.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val audioCaptureRepository: AudioCaptureRepository,
    private val saveMeasurementUseCase: SaveMeasurementUseCase,
    private val locationRepository: LocationRepository,
    private val dataStore: DataStore<Preferences>,
    private val networkMonitor: NetworkMonitor,
    @ApplicationContext private val context: Context,
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
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                _uiState.update { it.copy(isOffline = !isOnline) }
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
            is CaptureAction.ConsumeSaveSuccess -> {
                _uiState.update { it.copy(saveSuccess = false) }
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

        viewModelScope.launch {
            val location = locationRepository.getCurrentLocation().firstOrNull()

            if (location == null) {
                _uiState.update { it.copy(error = "Location unavailable, saving without coordinates") }
            }

            var resolvedLocationName = "Unknown Location"
            if (location != null) {
                resolvedLocationName = withContext(Dispatchers.IO) {
                    try {
                        if (Geocoder.isPresent()) {
                            val geocoder = Geocoder(context)

                            @Suppress("DEPRECATION")
                            val addresses = geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1,
                            )
                            val address = addresses?.firstOrNull()
                            if (address != null) {
                                val street = address.thoroughfare
                                val city = address.locality
                                val subLocality = address.subLocality

                                if (street != null && city != null) {
                                    "$street, $city"
                                } else if (city != null) {
                                    city
                                } else if (subLocality != null) {
                                    subLocality
                                } else {
                                    "Unknown Location"
                                }
                            } else {
                                "Unknown Location"
                            }
                        } else {
                            "Unknown Location"
                        }
                    } catch (e: Exception) {
                        "Unknown Location"
                    }
                }
            }

            val measurement = Measurement(
                id = UUID.randomUUID().toString(),
                userId = currentUserId.ifEmpty { "debug_user" },
                dbLevel = state.averageDb,
                waveform = normalizedWaveform,
                timestamp = System.currentTimeMillis(),
                latitude = location?.latitude,
                longitude = location?.longitude,
                contextTag = "Capture",
                locationName = resolvedLocationName,
            )

            try {
                saveMeasurementUseCase(measurement)
                _uiState.update { it.copy(saveSuccess = true) }
            } catch (e: Exception) {
                if (e is SyncException) {
                    // Local save succeeded; only remote sync failed
                    _uiState.update { it.copy(saveSuccess = true) } // Show success snackbar
                    // Optionally show a secondary warning via a separate event channel
                } else {
                    // Total failure — local save also failed
                    _uiState.update { it.copy(error = e.message ?: "Failed to save measurement") }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioCaptureRepository.stopCapture()
    }
}
