package pl.cincior.englishreminder.auth.domain.repository

import pl.cincior.englishreminder.auth.domain.model.AuthToken
import pl.cincior.englishreminder.auth.domain.model.RegisterPayload
import pl.cincior.englishreminder.auth.domain.model.ResetPasswordPayload
import pl.cincior.englishreminder.auth.domain.model.ResetPasswordRequestPayload
import pl.cincior.englishreminder.auth.domain.model.VerifyPayload
import pl.cincior.englishreminder.core.domain.AuthError
import pl.cincior.englishreminder.core.domain.Result
import pl.cincior.englishreminder.identity.graphql.type.UserRole

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
    suspend fun verifyUser(email: String, code: String): Result<VerifyPayload, AuthError>
    suspend fun resetPasswordRequest(email: String): Result<ResetPasswordRequestPayload, AuthError>
    suspend fun resetPassword(email: String, code: String, newPassword: String): Result<ResetPasswordPayload, AuthError>
}