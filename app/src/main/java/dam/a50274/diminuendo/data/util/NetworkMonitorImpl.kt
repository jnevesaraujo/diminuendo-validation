package dam.a50274.diminuendo.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dam.a50274.diminuendo.domain.util.NetworkMonitor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkMonitorImpl(private val context: Context) : NetworkMonitor {

    override val isOnline: Flow<Boolean> = callbackFlow {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Send the real initial state before any callbacks fire.
        trySend(checkIsOnline(connectivityManager))

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // A network became available but may not be validated yet.
                // Re-check the full capabilities rather than blindly emitting true.
                trySend(checkIsOnline(connectivityManager))
            }

            override fun onLost(network: Network) {
                trySend(checkIsOnline(connectivityManager))
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                // Must check NET_CAPABILITY_VALIDATED, not just NET_CAPABILITY_INTERNET.
                // NET_CAPABILITY_INTERNET means the network *declares* internet access.
                // NET_CAPABILITY_VALIDATED means Android has *confirmed* internet access
                // by performing a captive-portal / connectivity probe.
                // Without VALIDATED, a connected-but-not-working WiFi (e.g. captive portal,
                // hotel WiFi before login) or a brief capability update during setup
                // would incorrectly report the device as online.
                val validated = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED,
                )
                val hasInternet = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET,
                )
                trySend(validated && hasInternet)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged() // Suppress duplicate emissions (e.g. true/true on reconnect)

    private fun checkIsOnline(connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
