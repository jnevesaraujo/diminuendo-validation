package dam.a50274.diminuendo.ui.feature.diary

import dam.a50274.diminuendo.domain.model.Measurement

data class DiaryUiState(
    val isLoading: Boolean = false,
    val measurements: List<Measurement> = emptyList(),
    val isOffline: Boolean = false,
    val error: String? = null,
)
