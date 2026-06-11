package dam.a50274.diminuendo.ui.feature.heatmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import dam.a50274.diminuendo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatmapScreen(
    viewModel: HeatmapViewModel = hiltViewModel(),
    onNavigateToPaywall: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState()

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
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
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
        sheetPeekHeight = 80.dp,
        sheetContent = {
            ZoneInsightsBottomSheet(
                isPremium = state.isPremium,
                selectedZoneDetails = state.selectedZoneDetails,
                onBusyHoursClicked = { viewModel.onAction(HeatmapAction.BusyHoursClicked) },
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                val mapDesc = stringResource(R.string.heatmap_map_desc)
                GoogleMap(
                    modifier = Modifier.fillMaxSize().semantics { contentDescription = mapDesc },
                    cameraPositionState = cameraPositionState,
                ) {
                    state.userInitialLocation?.let { loc ->
                        Marker(state = MarkerState(position = loc), title = "Current Location")
                    }
                    state.searchLocationResult?.let { loc ->
                        Marker(state = MarkerState(position = loc), title = "Search Result")
                    }
                    if (state.noiseZones.isNotEmpty()) {
                        val provider = HeatmapTileProvider.Builder()
                            .weightedData(
                                state.noiseZones.map {
                                    WeightedLatLng(
                                        LatLng(it.centerLatitude, it.centerLongitude),
                                        it.totalContributions.toDouble(),
                                    )
                                },
                            )
                            .build()
                        TileOverlay(tileProvider = provider)
                    }
                }
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Location...") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        viewModel.onAction(HeatmapAction.SearchLocation(searchQuery))
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter),
                )
            }
        }
    }
}
