package dam.a50274.diminuendo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.hilt.android.AndroidEntryPoint
import dam.a50274.diminuendo.data.local.PreferencesKeys
import dam.a50274.diminuendo.ui.feature.splash.SplashViewModel
import dam.a50274.diminuendo.ui.navigation.AppShell
import dam.a50274.diminuendo.ui.navigation.Auth
import dam.a50274.diminuendo.ui.navigation.Heatmap
import dam.a50274.diminuendo.ui.theme.DiminuendoTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            DiminuendoTheme {
                val startDestination by splashViewModel.startDestination.collectAsState()

                startDestination?.let { destination ->
                    AppShell(startDestination = destination)
                }
            }
        }
    }
}
