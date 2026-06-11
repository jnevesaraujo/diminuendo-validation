package dam.a50274.diminuendo.ui.feature.heatmap

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dam.a50274.diminuendo.domain.repository.LocationRepository
import dam.a50274.diminuendo.domain.repository.NoiseZoneRepository
import dam.a50274.diminuendo.domain.usecase.CheckEntitlementUseCase
import dam.a50274.diminuendo.domain.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class HeatmapEvent {
    object NavigateToPaywall : HeatmapEvent()
}

sealed class HeatmapAction {
    object BusyHoursClicked : HeatmapAction()
    data class SearchLocation(val query: String) : HeatmapAction()
    object ConsumeSearch : HeatmapAction()
}

@HiltViewModel
class HeatmapViewModel @Inject constructor(
    private val noiseZoneRepository: NoiseZoneRepository,
    private val networkMonitor: NetworkMonitor,
    private val checkEntitlementUseCase: CheckEntitlementUseCase,
    private val locationRepository: LocationRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _events = Channel<HeatmapEvent>()
    val events = _events.receiveAsFlow()

    private val userInitialLocation = MutableStateFlow<LatLng?>(null)
    private val searchLocation = MutableStateFlow<LatLng?>(null)

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
        searchLocation,
    ) { zones, isOnline, isPremium, initialLocation, searchLoc ->
        HeatmapUiState(
            isLoading = false,
            noiseZones = zones,
            selectedZoneDetails = zones.firstOrNull(),
            isOffline = !isOnline,
            isPremium = isPremium,
            error = null,
            userInitialLocation = initialLocation,
            searchLocationResult = searchLoc,
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
            is HeatmapAction.SearchLocation -> {
                viewModelScope.launch {
                    val geocoder = Geocoder(context)
                    try {
                        val addresses = withContext(Dispatchers.IO) {
                            @Suppress("DEPRECATION")
                            geocoder.getFromLocationName(action.query, 1)
                        }
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            searchLocation.value = LatLng(address.latitude, address.longitude)
                        }
                    } catch (e: Exception) {
                        // Ignore or handle
                    }
                }
            }
            is HeatmapAction.ConsumeSearch -> {
                searchLocation.value = null
            }
        }
    }
}
