package dam.a50274.diminuendo.ui.feature.heatmap

import com.google.android.gms.maps.model.LatLng
import dam.a50274.diminuendo.domain.model.NoiseZone

data class HeatmapUiState(
    val isLoading: Boolean = false,
    val noiseZones: List<NoiseZone> = emptyList(),
    val selectedZoneDetails: NoiseZone? = null,
    val isOffline: Boolean = false,
    val isPremium: Boolean = false,
    val error: String? = null,
    val userInitialLocation: LatLng? = null,
    val searchLocationResult: LatLng? = null,
)
