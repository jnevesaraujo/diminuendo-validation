package dam.a50274.diminuendo.ui.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dam.a50274.diminuendo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenRoot(viewModel: ProfileViewModel = hiltViewModel(), onNavigateBack: () -> Unit, onSignOut: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    ProfileScreen(
        uiState = uiState,
        onDisplayNameChanged = viewModel::updateDisplayName,
        onSaveDisplayName = viewModel::saveDisplayName,
        onResetPassword = viewModel::resetPassword,
        onUnlockPremium = viewModel::unlockPremium,
        onClearMessages = viewModel::clearMessages,
        onNavigateBack = onNavigateBack,
        onSignOut = onSignOut,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onDisplayNameChanged: (String) -> Unit,
    onSaveDisplayName: () -> Unit,
    onResetPassword: () -> Unit,
    onUnlockPremium: () -> Unit,
    onClearMessages: () -> Unit,
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp).size(28.dp),
                        )
                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = WindowInsets(0),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = uiState.email,
                onValueChange = {},
                label = { Text("Email") },
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = uiState.displayName,
                onValueChange = onDisplayNameChanged,
                label = { Text("Display Name") },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = onSaveDisplayName,
                modifier = Modifier.align(Alignment.End),
                enabled = !uiState.isLoading,
            ) {
                Text("Save Profile")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Button(
                onClick = onResetPassword,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
            ) {
                Text("Send Password Reset Email")
            }

            if (!uiState.isPremium) {
                Button(
                    onClick = onUnlockPremium,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Unlock Premium (Simulated)")
                }
            } else {
                Text(
                    text = "You are a Premium member!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
            ) {
                Text("Sign Out")
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}
