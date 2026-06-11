package dam.a50274.diminuendo.ui.feature.heatmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.a50274.diminuendo.domain.repository.LocationRepository
import dam.a50274.diminuendo.domain.repository.NoiseZoneRepository
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
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HeatmapEvent {
    object NavigateToPaywall : HeatmapEvent()
}

sealed class HeatmapAction {
    object BusyHoursClicked : HeatmapAction()
}

@HiltViewModel
class HeatmapViewModel @Inject constructor(
    private val noiseZoneRepository: NoiseZoneRepository,
    private val networkMonitor: NetworkMonitor,
    private val checkEntitlementUseCase: CheckEntitlementUseCase,
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _events = Channel<HeatmapEvent>()
    val events = _events.receiveAsFlow()

    private val userInitialLocation = MutableStateFlow<LatLng?>(null)

    init {
        viewModelScope.launch {
            val location = locationRepository.getCurrentLocation().firstOrNull()
            if (location != null) {
                userInitialLocation.value = LatLng(location.latitude, location.longitude)
            }
        }
    }

    val uiState: StateFlow<HeatmapUiState> = combine(
        noiseZoneRepository.getNoiseZones(),
        networkMonitor.isOnline,
        checkEntitlementUseCase.isPremium,
        userInitialLocation,
    ) { zones, isOnline, isPremium, initialLocation ->
        HeatmapUiState(
            isLoading = false,
            noiseZones = zones,
            isOffline = !isOnline,
            isPremium = isPremium,
            error = null,
            userInitialLocation = initialLocation,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HeatmapUiState(isLoading = true),
    )

    fun onAction(action: HeatmapAction) {
        when (action) {
            is HeatmapAction.BusyHoursClicked -> {
                if (!uiState.value.isPremium) {
                    viewModelScope.launch {
                        _events.send(HeatmapEvent.NavigateToPaywall)
                    }
                }
            }
        }
    }
}
