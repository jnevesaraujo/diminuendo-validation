package dam.a50274.diminuendo.ui.feature.capture

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CaptureScreenRoot(viewModel: CaptureViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CaptureScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
fun CaptureScreen(state: CaptureUiState, onAction: (CaptureAction) -> Unit) {
    val context = LocalContext.current
    var showPermissionRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            onAction(CaptureAction.ToggleRecording)
        } else {
            showPermissionRationale = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // Top Section: Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatItem(label = "Avg", value = "${state.averageDb.toInt()} dB")
            StatItem(label = "Peak", value = "${state.peakDb.toInt()} dB")
            StatItem(label = "Time", value = "${state.elapsedSeconds}s")
        }

        // Center: Circular Gauge
        Box(
            modifier = Modifier.size(250.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularDbGauge(currentDb = state.currentDb)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${state.currentDb.toInt()}",
                    style = MaterialTheme.typography.displayLarge,
                )
                Text(text = "dB", style = MaterialTheme.typography.titleMedium)
            }
        }

        // Bottom Section: Waveform & Controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.latestWaveform.isNotEmpty()) {
                WaveformVisualizer(
                    waveform = state.latestWaveform,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                )
            } else {
                Spacer(modifier = Modifier.height(60.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (showPermissionRationale) {
                Text(
                    text = "Microphone access is required to capture decibel levels.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    onClick = {
                        if (state.isRecording) {
                            onAction(CaptureAction.ToggleRecording)
                        } else {
                            val isGranted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO,
                            ) == PackageManager.PERMISSION_GRANTED

                            if (isGranted) {
                                showPermissionRationale = false
                                onAction(CaptureAction.ToggleRecording)
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isRecording) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                    ),
                ) {
                    Text(if (state.isRecording) "Stop" else "Start")
                }

                if (state.averageDb > 0 && !state.isRecording) {
                    Button(
                        onClick = { onAction(CaptureAction.SaveMeasurement) },
                        modifier = Modifier.height(80.dp),
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun CircularDbGauge(currentDb: Double, modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier.fillMaxSize()) {
        val sweepAngle = ((currentDb / 120.0) * 240).toFloat().coerceIn(0f, 240f)

        drawArc(
            color = trackColor,
            startAngle = 150f,
            sweepAngle = 240f,
            useCenter = false,
            style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round),
        )

        drawArc(
            color = primaryColor,
            startAngle = 150f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

@Composable
fun WaveformVisualizer(waveform: List<Double>, modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        if (waveform.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val barWidth = width / 20f // Fixed to 20 samples
        val spacing = barWidth * 0.2f
        val effectiveBarWidth = barWidth - spacing

        val maxDb = 120.0 // Normalize against a reasonable max dB

        waveform.forEachIndexed { index, db ->
            val normalizedHeight = ((db / maxDb) * height).toFloat().coerceIn(0f, height)
            val x = index * barWidth + (spacing / 2f)
            val y = height - normalizedHeight

            drawLine(
                color = primaryColor,
                start = Offset(x, height),
                end = Offset(x, y),
                strokeWidth = effectiveBarWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}
