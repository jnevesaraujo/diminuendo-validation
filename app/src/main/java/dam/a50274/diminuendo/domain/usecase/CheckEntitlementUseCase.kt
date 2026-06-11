package dam.a50274.diminuendo.domain.usecase

import dam.a50274.diminuendo.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

class CheckEntitlementUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
) {
    val isPremium: Flow<Boolean> = subscriptionRepository.isPremium()

    /**
     * Checks if the user is allowed to perform an AI prompt.
     * Logic:
     * - If premium, always true.
     * - If free, limit is 3 prompts per day.
     * - If allowed, increments the counter.
     *
     * @return true if allowed, false if limit reached and user needs to upgrade.
     */
    suspend fun checkAndConsumeAiPrompt(): Boolean {
        val premium = isPremium.first()
        if (premium) return true

        val resetDateMs = subscriptionRepository.getAiCountResetDate().first()
        val dailyCount = subscriptionRepository.getAiDailyCount().first()

        val cal = Calendar.getInstance()
        val currentDayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val currentYear = cal.get(Calendar.YEAR)

        val resetCal = Calendar.getInstance().apply { timeInMillis = resetDateMs }
        val resetDayOfYear = resetCal.get(Calendar.DAY_OF_YEAR)
        val resetYear = resetCal.get(Calendar.YEAR)

        val isNewDay = (currentDayOfYear != resetDayOfYear) || (currentYear != resetYear)

        return if (isNewDay) {
            subscriptionRepository.incrementAiDailyCount(newDate = System.currentTimeMillis())
            true
        } else {
            if (dailyCount < 3) {
                subscriptionRepository.incrementAiDailyCount(newDate = null)
                true
            } else {
                false
            }
        }
    }
    fun getRemainingFreePrompts(): Flow<Int> = kotlinx.coroutines.flow.combine(
        subscriptionRepository.getAiCountResetDate(),
        subscriptionRepository.getAiDailyCount(),
    ) { resetDateMs, dailyCount ->
        val cal = Calendar.getInstance()
        val currentDayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val currentYear = cal.get(Calendar.YEAR)

        val resetCal = Calendar.getInstance().apply { timeInMillis = resetDateMs }
        val resetDayOfYear = resetCal.get(Calendar.DAY_OF_YEAR)
        val resetYear = resetCal.get(Calendar.YEAR)

        val isNewDay = (currentDayOfYear != resetDayOfYear) || (currentYear != resetYear)
        if (isNewDay) 3 else (3 - dailyCount).coerceAtLeast(0)
    }
}
