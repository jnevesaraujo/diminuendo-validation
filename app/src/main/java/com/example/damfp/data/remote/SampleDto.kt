package com.example.damfp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Network DTO. Never exposed to the UI — mapped to the domain in data/mapper. */
@Serializable
data class SampleDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String = "",
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("premium") val isPremium: Boolean = false,
)
