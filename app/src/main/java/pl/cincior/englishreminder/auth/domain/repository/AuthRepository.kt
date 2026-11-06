package pl.cincior.englishreminder.auth.domain.repository

import pl.cincior.englishreminder.auth.domain.model.AuthToken
import pl.cincior.englishreminder.identity.graphql.type.UserRole
import pl.cincior.englishreminder.core.domain.AuthError
import pl.cincior.englishreminder.core.domain.Result
import pl.cincior.englishreminder.auth.domain.model.RegisterPayload

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthToken, AuthError>
    suspend fun register(
        name: String,
        surname: String,
        nickname: String,
        role: UserRole,
        email: String,
        password: String
    ): Result<RegisterPayload, AuthError>
}