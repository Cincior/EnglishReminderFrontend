package pl.cincior.englishreminder.auth.domain.repository

import pl.cincior.englishreminder.auth.domain.model.AuthToken
import pl.cincior.englishreminder.core.domain.LoginError
import pl.cincior.englishreminder.core.domain.Result

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthToken, LoginError>
}