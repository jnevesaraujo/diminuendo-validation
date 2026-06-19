package dam.a50274.diminuendo.domain.model

enum class NoiseClassification(val label: String) {
    SAFE("Safe"),
    CONCERNING("Concerning"),
    DANGEROUS("Dangerous"),
}

fun Double.toNoiseClassification(): NoiseClassification {
    return when {
        this < 70.0 -> NoiseClassification.SAFE
        this <= 85.0 -> NoiseClassification.CONCERNING
        else -> NoiseClassification.DANGEROUS
    }
}
