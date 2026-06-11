package dam.a50274.diminuendo.ui.feature.heatmap

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import dam.a50274.diminuendo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatmapScreen(viewModel: HeatmapViewModel = hiltViewModel(), onNavigateToPaywall: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState()

    val cameraPositionState = rememberCameraPositionState()

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

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 72.dp,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { viewModel.onAction(HeatmapAction.BusyHoursClicked) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (state.isPremium) {
                        stringResource(R.string.heatmap_busy_hours_premium)
                    } else {
                        stringResource(R.string.heatmap_busy_hours_free)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.blur(if (state.isPremium) 0.dp else 8.dp),
                )
                if (!state.isPremium) {
                    val premiumRequiredText = stringResource(R.string.heatmap_premium_required)
                    Text(
                        text = premiumRequiredText,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.semantics { contentDescription = premiumRequiredText },
                    )
                }
            }
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
            }
        }
    }
}
