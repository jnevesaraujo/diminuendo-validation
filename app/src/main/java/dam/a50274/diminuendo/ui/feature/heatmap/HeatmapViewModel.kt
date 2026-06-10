package dam.a50274.diminuendo.ui.feature.heatmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.a50274.diminuendo.domain.repository.NoiseZoneRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HeatmapViewModel @Inject constructor(
    private val noiseZoneRepository: NoiseZoneRepository,
) : ViewModel() {

    val uiState: StateFlow<HeatmapUiState> = noiseZoneRepository.getNoiseZones()
        .map { zones ->
            HeatmapUiState(
                isLoading = false,
                noiseZones = zones,
                error = null,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HeatmapUiState(isLoading = true),
        )
}
