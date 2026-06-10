package dam.a50274.diminuendo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.hilt.android.AndroidEntryPoint
import dam.a50274.diminuendo.data.local.PreferencesKeys
import dam.a50274.diminuendo.ui.navigation.AppShell
import dam.a50274.diminuendo.ui.navigation.Auth
import dam.a50274.diminuendo.ui.navigation.Heatmap
import dam.a50274.diminuendo.ui.theme.DiminuendoTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        var startDestination: Any = Auth
        runBlocking {
            val prefs = dataStore.data.first()
            if (!prefs[PreferencesKeys.USER_ID].isNullOrEmpty()) {
                startDestination = Heatmap
            }
        }

        setContent {
            DiminuendoTheme {
                AppShell(startDestination = startDestination)
            }
        }
    }
}
