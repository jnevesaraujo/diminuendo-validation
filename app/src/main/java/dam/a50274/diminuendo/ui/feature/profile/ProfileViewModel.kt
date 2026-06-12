package dam.a50274.diminuendo.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.a50274.diminuendo.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val isPremium: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val user = firebaseAuth.currentUser
        _uiState.update { state ->
            state.copy(
                displayName = user?.displayName ?: "",
                email = user?.email ?: "",
            )
        }
        viewModelScope.launch {
            subscriptionRepository.isPremium().collect { premium ->
                _uiState.update { it.copy(isPremium = premium) }
            }
        }
    }

    fun updateDisplayName(newName: String) {
        _uiState.update { it.copy(displayName = newName) }
    }

    fun saveDisplayName() {
        val user = firebaseAuth.currentUser ?: return
        val newName = _uiState.value.displayName
        _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
        viewModelScope.launch {
            try {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build()
                user.updateProfile(profileUpdates).await()
                _uiState.update { it.copy(isLoading = false, successMessage = "Display name updated") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun resetPassword() {
        val email = firebaseAuth.currentUser?.email ?: return
        _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
        viewModelScope.launch {
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                _uiState.update { it.copy(isLoading = false, successMessage = "Password reset email sent") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun unlockPremium() {
        viewModelScope.launch {
            subscriptionRepository.setPremium(true)
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
