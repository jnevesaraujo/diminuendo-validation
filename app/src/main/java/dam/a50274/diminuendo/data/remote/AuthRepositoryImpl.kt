package dam.a50274.diminuendo.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dam.a50274.diminuendo.data.local.PreferencesKeys
import dam.a50274.diminuendo.domain.model.User
import dam.a50274.diminuendo.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val dataStore: DataStore<Preferences>,
) : AuthRepository {

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toDomainModel())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithEmail(email: String, password: String): User {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return result.user?.toDomainModel() ?: throw Exception("User not found after sign in")
    }

    override suspend fun registerWithEmail(email: String, password: String): User {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.toDomainModel() ?: throw Exception("User not found after registration")
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_ID)
        }
    }

    private fun FirebaseUser.toDomainModel(): User {
        return User(
            id = uid,
            name = displayName ?: "Anonymous",
            email = email ?: "",
        )
    }
}
