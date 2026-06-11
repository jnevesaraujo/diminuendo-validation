package dam.a50274.diminuendo.domain.usecase

import app.cash.turbine.test
import dam.a50274.diminuendo.fakes.FakeSubscriptionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class CheckEntitlementUseCaseTest {

    private lateinit var useCase: CheckEntitlementUseCase
    private lateinit var repository: FakeSubscriptionRepository

    @Before
    fun setup() {
        repository = FakeSubscriptionRepository()
        useCase = CheckEntitlementUseCase(repository)
    }

    @Test
    fun isPremium_whenFalse_returnsFreeTier() = runTest {
        repository.setPremium(false)
        useCase.isPremium.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun isPremium_whenTrue_returnsPremiumTier() = runTest {
        repository.setPremium(true)
        useCase.isPremium.test {
            assertTrue(awaitItem())
        }
    }

    @Test
    fun aiDailyLimit_whenCountBelow3_allowsPrompt() = runTest {
        repository.setPremium(false)
        repository.aiDailyCountState.value = 2
        repository.aiCountResetDateState.value = System.currentTimeMillis()

        val allowed = useCase.checkAndConsumeAiPrompt()
        assertTrue(allowed)
        assertEquals(3, repository.aiDailyCountState.value)
    }

    @Test
    fun aiDailyLimit_whenCountEquals3_blocksPrompt() = runTest {
        repository.setPremium(false)
        repository.aiDailyCountState.value = 3
        repository.aiCountResetDateState.value = System.currentTimeMillis()

        val allowed = useCase.checkAndConsumeAiPrompt()
        assertFalse(allowed)
        assertEquals(3, repository.aiDailyCountState.value)
    }

    @Test
    fun aiDailyLimit_whenNewDay_resetsCounter() = runTest {
        repository.setPremium(false)
        repository.aiDailyCountState.value = 3

        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        repository.aiCountResetDateState.value = cal.timeInMillis

        val allowed = useCase.checkAndConsumeAiPrompt()
        assertTrue(allowed)
    }
}
