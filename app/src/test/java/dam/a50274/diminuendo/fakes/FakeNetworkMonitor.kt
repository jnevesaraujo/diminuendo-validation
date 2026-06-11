package dam.a50274.diminuendo.fakes

import dam.a50274.diminuendo.domain.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNetworkMonitor : NetworkMonitor {
    var isOnlineState = MutableStateFlow(true)

    override val isOnline: Flow<Boolean>
        get() = isOnlineState
}
