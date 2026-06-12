package dam.a50274.diminuendo.domain.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class NoiseClassification(val label: String) {
    SAFE("Safe"),
    CONCERNING("Concerning"),
    DANGEROUS("Dangerous");

    val color: Color
        @Composable
        get() = when (this) {
            SAFE -> Color.Green
            CONCERNING -> Color.Yellow
            DANGEROUS -> MaterialTheme.colorScheme.error
        }
}

fun Double.toNoiseClassification(): NoiseClassification {
    return when {
        this < 70.0 -> NoiseClassification.SAFE
        this <= 85.0 -> NoiseClassification.CONCERNING
        else -> NoiseClassification.DANGEROUS
    }
}
