package dam.a50274.diminuendo.ui.feature.heatmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import dam.a50274.diminuendo.R
import dam.a50274.diminuendo.domain.model.NoiseZone
import kotlinx.coroutines.launch

private fun findNearestZone(target: LatLng, zones: List<NoiseZone>): NoiseZone? {
    var nearestZone: NoiseZone? = null
    var minDistance = Float.MAX_VALUE
    val results = FloatArray(1)
    for (zone in zones) {
        android.location.Location.distanceBetween(
            target.latitude,
            target.longitude,
            zone.centerLatitude,
            zone.centerLongitude,
            results,
        )
        val distance = results[0]
        if (distance <= 500f && distance < minDistance) {
            minDistance = distance
            nearestZone = zone
        }
    }
    return nearestZone
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatmapScreen(
    viewModel: HeatmapViewModel = hiltViewModel(),
    onNavigateToPaywall: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val cameraPositionState = rememberCameraPositionState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HeatmapEvent.NavigateToPaywall -> onNavigateToPaywall()
            }
        }
    }

    LaunchedEffect(state.userInitialLocation) {
        state.userInitialLocation?.let { location ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(location, 14f),
            )
        }
    }

    LaunchedEffect(state.searchLocationResult) {
        state.searchLocationResult?.let { loc ->
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(loc, 14f))

            val nearestZone = findNearestZone(loc, state.noiseZones)
            viewModel.onAction(HeatmapAction.ZoneSelected(nearestZone, loc))
            if (nearestZone != null) {
                scaffoldState.bottomSheetState.expand()
            } else {
                scaffoldState.bottomSheetState.partialExpand()
                snackbarHostState.showSnackbar("No noise data for this area yet")
            }

            viewModel.onAction(HeatmapAction.ConsumeSearch)
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp).size(28.dp),
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                },
                windowInsets = WindowInsets(0),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        sheetPeekHeight = 128.dp,
        sheetContent = {
            val locName = state.selectedZoneDetails?.locationName
            val displayName = if (locName.isNullOrEmpty()) "Tap the map to explore noise zones" else locName

            val zoneToPass = state.selectedZoneDetails?.copy(locationName = displayName)
                ?: NoiseZone(
                    locationId = "",
                    locationName = displayName,
                    centerLatitude = 0.0,
                    centerLongitude = 0.0,
                    hourlyAverages = emptyList(),
                    totalContributions = 0,
                )

            ZoneInsightsBottomSheet(
                isPremium = state.isPremium,
                selectedZoneDetails = zoneToPass,
                onBusyHoursClicked = { viewModel.onAction(HeatmapAction.BusyHoursClicked) },
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isOfflineChecked && state.isOffline) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.errorContainer)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "You are offline. Data will sync when reconnected",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer,
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            text = "Map data may be outdated",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer,
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    val mapDesc = stringResource(R.string.heatmap_map_desc)
                    GoogleMap(
                        modifier = Modifier.fillMaxSize().semantics { contentDescription = mapDesc },
                        cameraPositionState = cameraPositionState,
                        onMapClick = { latLng ->
                            val nearestZone = findNearestZone(latLng, state.noiseZones)
                            viewModel.onAction(HeatmapAction.ZoneSelected(nearestZone, latLng))
                            if (nearestZone != null) {
                                coroutineScope.launch { scaffoldState.bottomSheetState.expand() }
                            } else {
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.partialExpand()
                                    snackbarHostState.showSnackbar("No noise data for this area yet")
                                }
                            }
                        },
                    ) {
                        state.userInitialLocation?.let { loc ->
                            Marker(
                                state = MarkerState(position = loc),
                                title = "Your location",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                                contentDescription = "Your location",
                            )
                        }
                        state.tappedLocation?.let { loc ->
                            Marker(
                                state = MarkerState(position = loc),
                                title = "Selected Location",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                            )
                        }
                        if (state.noiseZones.isNotEmpty()) {
                            // Use the total contribution count as a stable key so the TileOverlay
                            // is fully recreated whenever the underlying data changes.
                            val zoneKey = state.noiseZones.sumOf { it.totalContributions }

                            key(zoneKey) {
                                val provider = remember(zoneKey) {
                                    // Use log(contributions + 1) as the weight so that zones with
                                    // 1 contribution are still clearly visible next to zones with 50.
                                    // Without this, weight normalization makes low-count zones invisible:
                                    //   raw weight 1 vs 50  ->  2% intensity -> transparent
                                    //   log(2)   vs log(51) -> 35% intensity -> clearly visible
                                    val weightedPoints = state.noiseZones.map {
                                        WeightedLatLng(
                                            LatLng(it.centerLatitude, it.centerLongitude),
                                            Math.log((it.totalContributions + 1).toDouble()),
                                        )
                                    }

                                    HeatmapTileProvider.Builder()
                                        .weightedData(weightedPoints)
                                        .radius(50) // larger radius makes zones visible at city zoom
                                        .opacity(0.8) // slightly transparent so map labels show through
                                        .build()
                                }
                                TileOverlay(tileProvider = provider)
                            }
                        }
                    }
                    DockedSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = {
                            viewModel.onAction(HeatmapAction.SearchLocation(searchQuery))
                        },
                        active = false,
                        onActiveChange = {},
                        enabled = !state.isOffline,
                        placeholder = { Text("Search location…") },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    val loc = state.userInitialLocation
                                    if (loc != null) {
                                        coroutineScope.launch {
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngZoom(loc, 15f),
                                            )
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Location unavailable")
                                        }
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "Go to my location",
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 8.dp)
                            .align(Alignment.TopCenter),
                    ) {
                        // No suggestions content
                    }
                }
            }
        }
    }
}
