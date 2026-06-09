package dam.a50274.diminuendo.ui.feature.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AuthScreen(onNavigateToHome: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("AuthScreen")
            // TODO: remove before final build
            androidx.compose.material3.TextButton(onClick = onNavigateToHome) {
                Text("Skip Auth (Debug)")
            }
        }
    }
}
