package dam.a50274.diminuendo.ui.feature.heatmap

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dam.a50274.diminuendo.domain.model.NoiseZone
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
import kotlinx.coroutines.flow.onEach
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
    data class ZoneSelected(val zone: NoiseZone?, val location: LatLng?) : HeatmapAction()
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
    private val selectedZone = MutableStateFlow<NoiseZone?>(null)
    private val tappedLocation = MutableStateFlow<LatLng?>(null)

    init {
        viewModelScope.launch {
            val location = locationRepository.getCurrentLocation().firstOrNull()
            if (location != null) {
                userInitialLocation.value = LatLng(location.latitude, location.longitude)
            }
        }
    }

    private val mutableOfflineChecked = MutableStateFlow(false)

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    private val debouncedIsOnline = networkMonitor.isOnline
        //  .debounce(3000)
        .onEach { mutableOfflineChecked.value = true }

    val uiState: StateFlow<HeatmapUiState> = combine(
        noiseZoneRepository.getNoiseZones(),
        debouncedIsOnline,
        checkEntitlementUseCase.isPremium,
        userInitialLocation,
        searchLocation,
        selectedZone,
        tappedLocation,
        mutableOfflineChecked,
    ) { params ->
        val zones = params[0] as List<NoiseZone>
        val isOnline = params[1] as Boolean
        val isPremium = params[2] as Boolean
        val initialLocation = params[3] as LatLng?
        val searchLoc = params[4] as LatLng?
        val selZone = params[5] as NoiseZone?
        val tapLoc = params[6] as LatLng?
        val isOfflineChecked = params[7] as Boolean
        HeatmapUiState(
            isLoading = false,
            noiseZones = zones,
            selectedZoneDetails = selZone,
            isOffline = !isOnline,
            isOfflineChecked = isOfflineChecked,
            isPremium = isPremium,
            error = null,
            userInitialLocation = initialLocation,
            searchLocationResult = searchLoc,
            tappedLocation = tapLoc,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HeatmapUiState(isLoading = true, isOffline = false, isOfflineChecked = false),
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
            is HeatmapAction.ZoneSelected -> {
                tappedLocation.value = action.location
                if (action.zone != null) {
                    viewModelScope.launch {
                        val name = withContext(Dispatchers.IO) {
                            try {
                                val geocoder = Geocoder(context)

                                @Suppress("DEPRECATION")
                                val addresses = geocoder.getFromLocation(
                                    action.zone.centerLatitude,
                                    action.zone.centerLongitude,
                                    1,
                                )
                                if (!addresses.isNullOrEmpty()) {
                                    val address = addresses[0]
                                    val street = address.thoroughfare
                                    val city = address.locality ?: address.subAdminArea ?: address.adminArea
                                    val neighborhood = address.subLocality

                                    when {
                                        street != null && city != null -> "$street, $city"
                                        city != null -> city
                                        neighborhood != null -> neighborhood
                                        else -> "Unknown Area"
                                    }
                                } else {
                                    "Unknown Area"
                                }
                            } catch (e: Exception) {
                                "Unknown Area"
                            }
                        }
                        selectedZone.value = action.zone.copy(locationName = name)
                    }
                } else {
                    selectedZone.value = null
                }
            }
        }
    }
}
