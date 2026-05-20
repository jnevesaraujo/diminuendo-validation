package com.example.damfp.domain.model

/**
 * DOMAIN model. It is the only type the UI layer knows about.
 * DTO (network) and Entity (Room) map to this — see data/mapper.
 */
data class SampleItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val isPremium: Boolean,
)
