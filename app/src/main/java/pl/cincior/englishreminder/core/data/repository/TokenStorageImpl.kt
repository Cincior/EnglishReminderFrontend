package pl.cincior.englishreminder.core.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import pl.cincior.englishreminder.core.domain.repository.TokenStorage
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

@Singleton
class TokenStorageImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : TokenStorage {
    private companion object {
        val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val KEY_REMEMBER_ME = booleanPreferencesKey("remember_me")
    }

    @Volatile
    private var cachedAuthToken: String? = null

    override fun getAuthTokenSync(): String? = cachedAuthToken

    override fun getAuthToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            val token = preferences[KEY_AUTH_TOKEN]
            cachedAuthToken = token
            token
        }
    }

    override suspend fun saveAuthToken(token: String) {
        cachedAuthToken = token
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = token
        }
    }

    override suspend fun clearAuthToken() {
        cachedAuthToken = null
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

    override fun getRememberMe(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_REMEMBER_ME] ?: false
        }
    }

    override suspend fun saveRememberMe(rememberMe: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_REMEMBER_ME] = rememberMe
        }
    }

    override suspend fun clearRememberMe() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_REMEMBER_ME)
        }
    }
}