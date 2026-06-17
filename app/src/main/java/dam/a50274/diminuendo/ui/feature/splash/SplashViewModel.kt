package dam.a50274.diminuendo.ui.feature.splash

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.a50274.diminuendo.data.local.PreferencesKeys
import dam.a50274.diminuendo.ui.navigation.Auth
import dam.a50274.diminuendo.ui.navigation.Heatmap
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    val startDestination: StateFlow<Any?> = dataStore.data
        .map { prefs ->
            if (!prefs[PreferencesKeys.USER_ID].isNullOrEmpty()) Heatmap else Auth
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
