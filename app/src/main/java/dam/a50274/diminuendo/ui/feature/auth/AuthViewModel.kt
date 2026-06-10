package dam.a50274.diminuendo.ui.feature.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.a50274.diminuendo.data.local.PreferencesKeys
import dam.a50274.diminuendo.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import javax.inject.Inject

sealed interface AuthEvent {
    object AuthSuccess : AuthEvent
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<AuthEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.UpdateEmail -> {
                _uiState.update { it.copy(email = action.email) }
            }
            is AuthAction.UpdatePassword -> {
                _uiState.update { it.copy(password = action.password) }
            }
            is AuthAction.ToggleMode -> {
                _uiState.update { it.copy(isLoginMode = !it.isLoginMode, error = null) }
            }
            is AuthAction.Submit -> {
                submit()
            }
            is AuthAction.AcknowledgeError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun submit() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.update { it.copy(error = "Fields cannot be empty") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val user = if (_uiState.value.isLoginMode) {
                    authRepository.signInWithEmail(email, password)
                } else {
                    authRepository.registerWithEmail(email, password)
                }

                // Save to DataStore
                dataStore.edit { preferences ->
                    preferences[PreferencesKeys.USER_ID] = user.id
                }

                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AuthEvent.AuthSuccess)
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email or password."
                    is FirebaseAuthUserCollisionException -> "An account already exists with this email."
                    is FirebaseAuthInvalidUserException -> "No account found with this email."
                    is FirebaseAuthWeakPasswordException -> "Password is too weak."
                    else -> "Authentication failed: ${e.localizedMessage}"
                }
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }
}
