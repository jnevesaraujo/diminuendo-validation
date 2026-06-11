package dam.a50274.diminuendo.data.mapper

import dam.a50274.diminuendo.data.local.MeasurementEntity
import dam.a50274.diminuendo.domain.model.Measurement
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class MeasurementMapperTest {

    @Test
    fun measurementEntity_toDomain_mapsAllFieldsCorrectly() {
        val entity = MeasurementEntity(
            id = UUID.randomUUID().toString(),
            userId = "user_123",
            dbLevel = 85.5,
            waveform = intArrayOf(10, 20, 30),
            timestamp = 1622548800000L,
            latitude = 40.7128,
            longitude = -74.0060,
            contextTag = "Concert",
            locationName = "Madison Square Garden",
            updatedAt = System.currentTimeMillis(),
            pendingSync = false,
            isDeleted = false,
        )

        val domain = entity.toDomain()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.userId, domain.userId)
        assertEquals(entity.dbLevel, domain.dbLevel, 0.01)
        assertEquals(entity.waveform.toList(), domain.waveform.toList())
        assertEquals(entity.timestamp, domain.timestamp)
        assertEquals(entity.latitude, domain.latitude)
        assertEquals(entity.longitude, domain.longitude)
        assertEquals(entity.contextTag, domain.contextTag)
        assertEquals(entity.locationName, domain.locationName)
    }

    @Test
    fun measurement_toEntity_preservesPendingSyncFlag() {
        val domain = Measurement(
            id = UUID.randomUUID().toString(),
            userId = "user_123",
            dbLevel = 85.5,
            waveform = intArrayOf(10, 20, 30),
            timestamp = 1622548800000L,
            latitude = 40.7128,
            longitude = -74.0060,
            contextTag = "Concert",
            locationName = "Madison Square Garden",
        )

        val entityTrue = domain.toEntity(pendingSync = true)
        val entityFalse = domain.toEntity(pendingSync = false)

        assertEquals(true, entityTrue.pendingSync)
        assertEquals(false, entityFalse.pendingSync)
        assertEquals(domain.id, entityTrue.id)
        assertEquals(domain.waveform.toList(), entityTrue.waveform.toList())
    }
}
