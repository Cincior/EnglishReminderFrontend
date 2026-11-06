package pl.cincior.englishreminder.auth.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import pl.cincior.englishreminder.auth.domain.repository.TokenStorage
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class TokenStorageImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : TokenStorage {
    private companion object {
        val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    override fun getAuthToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_AUTH_TOKEN]
        }
    }

    override suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = token
        }
    }

    override suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_AUTH_TOKEN)
        }
    }

    override fun getRefreshToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_REFRESH_TOKEN]
        }
    }

    override suspend fun saveRefreshToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_REFRESH_TOKEN] = token
        }
    }

    override suspend fun clearRefreshToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_REFRESH_TOKEN)
        }
    }
}