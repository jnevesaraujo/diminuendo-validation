package dam.a50274.diminuendo.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val IS_PREMIUM = booleanPreferencesKey("is_premium")
    val USER_ID = stringPreferencesKey("user_id")
    val AI_DAILY_COUNT = intPreferencesKey("ai_daily_count")
    val AI_COUNT_RESET_DATE = longPreferencesKey("ai_count_reset_date")
}
