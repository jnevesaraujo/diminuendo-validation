package dam.a50274.diminuendo.data.remote

import com.google.firebase.firestore.PropertyName

// All fields need explicit @PropertyName annotations AND @JvmField / @get:JvmField
// to guarantee Firestore serialization uses the exact field name and Double type.
// Without this, toObjects() may misread List<Double> as List<Long> when values are whole numbers.
data class NoiseZoneDto(
    @get:PropertyName("locationId")
    @set:PropertyName("locationId")
    var locationId: String = "",

    @get:PropertyName("centerLatitude")
    @set:PropertyName("centerLatitude")
    var centerLatitude: Double = 0.0,

    @get:PropertyName("centerLongitude")
    @set:PropertyName("centerLongitude")
    var centerLongitude: Double = 0.0,

    @get:PropertyName("locationName")
    @set:PropertyName("locationName")
    var locationName: String = "",

    @get:PropertyName("hourlyAverages")
    @set:PropertyName("hourlyAverages")
    var hourlyAverages: List<Double> = List(24) { 0.0 },

    @get:PropertyName("totalContributions")
    @set:PropertyName("totalContributions")
    var totalContributions: Int = 0,
) {
    // Firestore requires a public no-arg constructor for deserialization.
    // The default values above satisfy this, but we make it explicit.
    constructor() : this("", 0.0, 0.0, "", List(24) { 0.0 }, 0)
}
