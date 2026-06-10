package dam.a50274.diminuendo.ui.feature.heatmap

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng

@Composable
fun HeatmapScreen(
    viewModel: HeatmapViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            GoogleMap(modifier = Modifier.fillMaxSize()) {
                if (state.noiseZones.isNotEmpty()) {
                    val provider = HeatmapTileProvider.Builder()
                        .weightedData(state.noiseZones.map { 
                            WeightedLatLng(LatLng(it.centerLatitude, it.centerLongitude), it.totalContributions.toDouble()) 
                        })
                        .build()
                    TileOverlay(tileProvider = provider)
                }
            }
        }
    }
}
