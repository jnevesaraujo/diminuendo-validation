package dam.a50274.diminuendo.domain.repository

import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun isPremium(): Flow<Boolean>
    suspend fun setPremium(isPremium: Boolean)
    fun getAiDailyCount(): Flow<Int>
    suspend fun incrementAiDailyCount(newDate: Long?)
    fun getAiCountResetDate(): Flow<Long>
}
