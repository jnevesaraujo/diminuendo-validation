package dam.a50274.diminuendo.ui.feature.capture

data class CaptureUiState(
    val currentDb: Double = 0.0,
    val averageDb: Double = 0.0,
    val peakDb: Double = 0.0,
    val elapsedSeconds: Long = 0L,
    val isRecording: Boolean = false,
    val isOffline: Boolean = false,
    val error: String? = null,
    val latestWaveform: List<Double> = emptyList(),
    val saveSuccess: Boolean = false,
)
