package dam.a50274.diminuendo.ui.feature.heatmap

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dam.a50274.diminuendo.R
import dam.a50274.diminuendo.domain.model.NoiseZone

val NoiseZone.locationName: String?
    get() = null

@Composable
fun ZoneInsightsBottomSheet(isPremium: Boolean, selectedZoneDetails: NoiseZone?, onBusyHoursClicked: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // Header Area (Always Visible & Clickable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val locName = selectedZoneDetails?.locationName
                Text(
                    text = if (locName.isNullOrEmpty()) "Unknown Location" else locName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "Busy Hours",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                )
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
            )
        }

        // Expandable Content
        AnimatedVisibility(visible = isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clickable { onBusyHoursClicked() },
                contentAlignment = Alignment.Center,
            ) {
                val hourlyAverages = selectedZoneDetails?.hourlyAverages

                if (hourlyAverages.isNullOrEmpty()) {
                    Text(
                        text = "No data yet for this zone",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .blur(if (isPremium) 0.dp else 8.dp)
                            .padding(vertical = 16.dp),
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .blur(if (isPremium) 0.dp else 8.dp),
                    ) {
                        val maxDb = hourlyAverages.maxOrNull()?.coerceAtLeast(1.0) ?: 100.0
                        val primaryColor = MaterialTheme.colorScheme.primary

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                        ) {
                            val barCount = 24
                            val spacing = 8.dp.toPx()
                            val totalSpacing = spacing * (barCount - 1)
                            val barWidth = (size.width - totalSpacing) / barCount

                            hourlyAverages.take(24).forEachIndexed { index, db ->
                                val barHeight = ((db / maxDb) * size.height).toFloat()
                                val x = index * (barWidth + spacing)
                                val y = size.height - barHeight

                                drawRect(
                                    color = primaryColor,
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight),
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("0", style = MaterialTheme.typography.labelSmall)
                            Text("6", style = MaterialTheme.typography.labelSmall)
                            Text("12", style = MaterialTheme.typography.labelSmall)
                            Text("18", style = MaterialTheme.typography.labelSmall)
                            Text("23", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                if (!isPremium) {
                    val premiumRequiredText = stringResource(R.string.heatmap_premium_required)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = premiumRequiredText,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .semantics { contentDescription = premiumRequiredText },
                        )
                    }
                }
            }
        }
    } 
}
