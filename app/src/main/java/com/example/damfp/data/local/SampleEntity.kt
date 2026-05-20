package com.example.damfp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Local persistence (Room). Single Source of Truth for offline mode. */
@Entity(tableName = "sample_items")
data class SampleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val isPremium: Boolean,
    val updatedAt: Long = System.currentTimeMillis(),
)
