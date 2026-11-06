package pl.cincior.englishreminder.auth.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.cincior.englishreminder.auth.domain.model.AuthToken

interface TokenStorage {
    fun getAuthToken(): Flow<String?>
    suspend fun saveAuthToken(token: String)
    suspend fun clearAuthToken()
    fun getRefreshToken(): Flow<String?>
    suspend fun saveRefreshToken(token: String)
    suspend fun clearRefreshToken()
}