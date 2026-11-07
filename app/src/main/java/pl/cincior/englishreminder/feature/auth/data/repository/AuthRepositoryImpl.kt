package pl.cincior.englishreminder.feature.auth.data.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import pl.cincior.englishreminder.feature.auth.data.repository.mapper.toAuthError
import pl.cincior.englishreminder.feature.auth.data.repository.mapper.toAuthNetworkError
import pl.cincior.englishreminder.feature.auth.domain.model.AuthToken
import pl.cincior.englishreminder.feature.auth.domain.model.RegisterPayload
import pl.cincior.englishreminder.feature.auth.domain.model.ResetPasswordPayload
import pl.cincior.englishreminder.feature.auth.domain.model.ResetPasswordRequestPayload
import pl.cincior.englishreminder.feature.auth.domain.model.VerifyPayload
import pl.cincior.englishreminder.feature.auth.domain.repository.AuthRepository
import pl.cincior.englishreminder.core.domain.AuthError
import pl.cincior.englishreminder.core.domain.Result
import pl.cincior.englishreminder.core.network.errorCode
import pl.cincior.englishreminder.di.IdentityApolloClient
import pl.cincior.englishreminder.identity.graphql.LoginMutation
import pl.cincior.englishreminder.identity.graphql.RegisterMutation
import pl.cincior.englishreminder.identity.graphql.ResetPasswordMutation
import pl.cincior.englishreminder.identity.graphql.ResetPasswordRequestMutation
import pl.cincior.englishreminder.identity.graphql.VerifyUserMutation
import pl.cincior.englishreminder.identity.graphql.type.NewUserData
import pl.cincior.englishreminder.identity.graphql.type.ResetPasswordData
import pl.cincior.englishreminder.identity.graphql.type.UserRole
import pl.cincior.englishreminder.identity.graphql.type.UserVerifyData
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @param:IdentityApolloClient private val apolloIdentityClient: ApolloClient
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<AuthToken, AuthError> {
        try {
            val response = apolloIdentityClient.mutation(
                LoginMutation(email, password)
            ).execute()
            Log.i("Response", response.toString())
            Log.i("obiekty", "$email $password")

            response.exception?.let { return Result.Error(it.toAuthNetworkError()) }

            if (response.hasErrors()) {
                return Result.Error(response.errorCode().toAuthError())
            }

            val payload = response.data?.login ?: return Result.Error(AuthError.Data.UNKNOWN_ERROR)

            return Result.Success(AuthToken(payload.token, payload.refreshToken))
        } catch (_: ApolloException) {
            return Result.Error(AuthError.Network.NETWORK_ERROR)
        }
    }

    override suspend fun register(
        name: String,
        surname: String,
        nickname: String,
        role: UserRole,
        email: String,
        password: String
    ): Result<RegisterPayload, AuthError> {
        try {
            val response = apolloIdentityClient.mutation(
                RegisterMutation(
                    NewUserData(
                        name = name,
                        surname = surname,
                        nickName = nickname,
                        role = UserRole.USER,
                        email = email,
                        password = password
                    )
                )
            ).execute()

            response.exception?.let { return Result.Error(it.toAuthNetworkError()) }

            if (response.hasErrors()) {
                return Result.Error(response.errorCode().toAuthError())
            }

            val payload = response.data?.register ?: return Result.Error(AuthError.Data.UNKNOWN_ERROR)

            return Result.Success(RegisterPayload(payload.registered, payload.verificationRequired))
        } catch (_: ApolloException) {
            return Result.Error(AuthError.Network.NETWORK_ERROR)
        }

    }

    override suspend fun verifyUser(email: String, code: String): Result<VerifyPayload, AuthError> {
        try {
            val response = apolloIdentityClient.mutation(
                VerifyUserMutation(
                    UserVerifyData(
                        email = email,
                        code = code
                    )
                )
            ).execute()

            response.exception?.let { return Result.Error(it.toAuthNetworkError()) }

            if (response.hasErrors()) {
                return Result.Error(response.errorCode().toAuthError())
            }

            val payload = response.data?.verifyUser ?: return Result.Error(AuthError.Data.UNKNOWN_ERROR)

            return Result.Success(VerifyPayload(payload.verified))
        } catch (_: ApolloException) {
            return Result.Error(AuthError.Network.NETWORK_ERROR)
        }
    }

    override suspend fun resetPasswordRequest(email: String): Result<ResetPasswordRequestPayload, AuthError> {
        try {
            val response = apolloIdentityClient.mutation(
                ResetPasswordRequestMutation(email)
            ).execute()

            response.exception?.let { return Result.Error(it.toAuthNetworkError()) }

            if (response.hasErrors()) {
                return Result.Error(response.errorCode().toAuthError())
            }

            val payload = response.data?.resetPasswordRequest ?: return Result.Error(AuthError.Data.UNKNOWN_ERROR)

            return Result.Success(ResetPasswordRequestPayload(payload.requested))
        } catch (_: ApolloException) {
            return Result.Error(AuthError.Network.NETWORK_ERROR)
        }
    }

    override suspend fun resetPassword(email: String, code: String, newPassword: String): Result<ResetPasswordPayload, AuthError> {
        try {
            val response = apolloIdentityClient.mutation(
                ResetPasswordMutation(
                    ResetPasswordData(
                        email = email,
                        code = code,
                        newPassword = newPassword
                    )
                )
            ).execute()

            response.exception?.let { return Result.Error(it.toAuthNetworkError()) }

            if (response.hasErrors()) {
                return Result.Error(response.errorCode().toAuthError())
            }

            val payload = response.data?.resetPassword ?: return Result.Error(AuthError.Data.UNKNOWN_ERROR)

            return Result.Success(ResetPasswordPayload(payload.reset))
        } catch (_: ApolloException) {
            return Result.Error(AuthError.Network.NETWORK_ERROR)
        }
    }
}