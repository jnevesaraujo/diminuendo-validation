package com.example.damfp.data.mapper

import com.example.damfp.data.local.SampleEntity
import com.example.damfp.data.remote.SampleDto
import com.example.damfp.domain.model.SampleItem

// Mappings: DTO -> Entity -> Domain. The UI only sees SampleItem.

fun SampleDto.toEntity(): SampleEntity = SampleEntity(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
    isPremium = isPremium,
)

fun SampleEntity.toDomain(): SampleItem = SampleItem(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
    isPremium = isPremium,
)
