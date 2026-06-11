package dam.a50274.diminuendo.ui.feature.capture

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dam.a50274.diminuendo.R

@Composable
fun CaptureScreenRoot(
    viewModel: CaptureViewModel = hiltViewModel(),
    onNavigateToProfile: () -> Unit,
    onNavigateToDiary: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CaptureScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToDiary = onNavigateToDiary,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureScreen(
    state: CaptureUiState,
    onAction: (CaptureAction) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDiary: () -> Unit,
) {
    val context = LocalContext.current
    var showPermissionRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO,
            ) == PackageManager.PERMISSION_GRANTED
            )
        if (audioGranted) {
            onAction(CaptureAction.ToggleRecording)
        } else {
            showPermissionRationale = true
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            val result = snackbarHostState.showSnackbar(
                message = "Measurement saved",
                actionLabel = "View in Diary",
            )
            if (result == SnackbarResult.ActionPerformed) {
                onNavigateToDiary()
            }
            onAction(CaptureAction.ConsumeSaveSuccess)
        }
    }

    Scaffold(
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
                            style = MaterialTheme.typography.titleMedium,
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Top Section: Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatItem(label = stringResource(R.string.capture_stat_avg), value = "${state.averageDb.toInt()} dB")
                StatItem(label = stringResource(R.string.capture_stat_peak), value = "${state.peakDb.toInt()} dB")
                StatItem(label = stringResource(R.string.capture_stat_time), value = "${state.elapsedSeconds}s")
            }

            // Center: Circular Gauge
            Box(
                modifier = Modifier.size(250.dp),
                contentAlignment = Alignment.Center,
            ) {
                val gaugeDesc = stringResource(R.string.capture_gauge_desc)
                CircularDbGauge(
                    currentDb = state.currentDb,
                    modifier = Modifier.semantics { contentDescription = gaugeDesc },
                )
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
                    val waveformDesc = stringResource(R.string.capture_waveform_desc)
                    WaveformVisualizer(
                        waveform = state.latestWaveform,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .semantics { contentDescription = waveformDesc },
                    )
                } else {
                    Spacer(modifier = Modifier.height(60.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (showPermissionRationale) {
                    Text(
                        text = stringResource(R.string.capture_permission_rationale),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                if (state.error != null) {
                    Text(
                        text = state.error,
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
                                val audioGranted = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO,
                                ) == PackageManager.PERMISSION_GRANTED

                                val locationGranted = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                ) == PackageManager.PERMISSION_GRANTED

                                if (audioGranted && locationGranted) {
                                    showPermissionRationale = false
                                    onAction(CaptureAction.ToggleRecording)
                                } else {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.RECORD_AUDIO,
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                        ),
                                    )
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
                        Text(
                            if (state.isRecording) {
                                stringResource(
                                    R.string.capture_btn_stop,
                                )
                            } else {
                                stringResource(R.string.capture_btn_start)
                            },
                        )
                    }

                    if (state.averageDb > 0 && !state.isRecording) {
                        Button(
                            onClick = { onAction(CaptureAction.SaveMeasurement) },
                            modifier = Modifier.height(80.dp),
                        ) {
                            Text(stringResource(R.string.capture_btn_save))
                        }
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
