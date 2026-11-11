package pl.cincior.englishreminder.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface TokenStorage {
    fun getAuthToken(): Flow<String?>
    suspend fun saveAuthToken(token: String)
    suspend fun clearAuthToken()
    fun getRefreshToken(): Flow<String?>
    suspend fun saveRefreshToken(token: String)
    suspend fun clearRefreshToken()
}