package dam.a50274.diminuendo.domain.repository

import dam.a50274.diminuendo.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun signInWithEmail(email: String, password: String): User
    suspend fun registerWithEmail(email: String, password: String): User
    suspend fun signOut()
}
