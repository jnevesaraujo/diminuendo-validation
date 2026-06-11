package dam.a50274.diminuendo.ui.feature.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.a50274.diminuendo.domain.repository.SubscriptionRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PaywallEvent {
    object NavigateBack : PaywallEvent()
}

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    private val _events = Channel<PaywallEvent>()
    val events = _events.receiveAsFlow()

    fun unlockPremium() {
        viewModelScope.launch {
            subscriptionRepository.setPremium(true)
            _events.send(PaywallEvent.NavigateBack)
        }
    }
}
