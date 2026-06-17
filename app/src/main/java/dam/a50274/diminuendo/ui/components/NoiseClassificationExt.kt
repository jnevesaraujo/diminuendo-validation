package dam.a50274.diminuendo.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dam.a50274.diminuendo.domain.model.NoiseClassification

@Composable
fun NoiseClassification.toColor(): Color = when (this) {
    NoiseClassification.SAFE -> Color.Green
    NoiseClassification.CONCERNING -> Color.Yellow
    NoiseClassification.DANGEROUS -> MaterialTheme.colorScheme.error
}
