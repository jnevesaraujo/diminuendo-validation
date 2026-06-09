package dam.a50274.diminuendo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import dam.a50274.diminuendo.ui.navigation.AppShell
import dam.a50274.diminuendo.ui.theme.DiminuendoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            DiminuendoTheme {
                AppShell()
            }
        }
    }
}
