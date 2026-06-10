package dam.a50274.diminuendo.ui.feature.auth

sealed interface AuthAction {
    data class UpdateEmail(val email: String) : AuthAction
    data class UpdatePassword(val password: String) : AuthAction
    object ToggleMode : AuthAction
    object Submit : AuthAction
    object AcknowledgeError : AuthAction
}
