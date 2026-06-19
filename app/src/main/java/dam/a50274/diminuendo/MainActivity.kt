package dam.a50274.diminuendo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import dam.a50274.diminuendo.ui.feature.splash.SplashViewModel
import dam.a50274.diminuendo.ui.navigation.AppShell
import dam.a50274.diminuendo.ui.theme.DiminuendoTheme

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
