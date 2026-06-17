package dam.a50274.diminuendo.fakes

import dam.a50274.diminuendo.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSubscriptionRepository : SubscriptionRepository {
    var isPremiumState = MutableStateFlow(false)
    var aiDailyCountState = MutableStateFlow(0)
    var aiCountResetDateState = MutableStateFlow(0L)

    override fun isPremium(): Flow<Boolean> = isPremiumState

    override suspend fun setPremium(isPremium: Boolean) {
        isPremiumState.value = isPremium
    }

    override fun getAiDailyCount(): Flow<Int> = aiDailyCountState

    override suspend fun incrementAiDailyCount(newDate: Long?) {
        if (newDate != null) {
            aiDailyCountState.value = 1  // Reset to 1 (first use of new day)
            aiCountResetDateState.value = newDate
        } else {
            aiDailyCountState.value += 1
        }
    }

    override fun getAiCountResetDate(): Flow<Long> = aiCountResetDateState
}
