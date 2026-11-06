package pl.cincior.englishreminder.auth.data.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import pl.cincior.englishreminder.auth.data.repository.mapper.toLoginError
import pl.cincior.englishreminder.auth.data.repository.mapper.toLoginNetworkError
import pl.cincior.englishreminder.auth.domain.model.AuthToken
import pl.cincior.englishreminder.auth.domain.repository.AuthRepository
import pl.cincior.englishreminder.core.domain.LoginError
import pl.cincior.englishreminder.core.domain.Result
import pl.cincior.englishreminder.core.network.errorCode
import pl.cincior.englishreminder.di.IdentityApolloClient
import pl.cincior.englishreminder.identity.graphql.LoginMutation
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @param:IdentityApolloClient private val apolloIdentityClient: ApolloClient
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<AuthToken, LoginError> {
        try {
            val response = apolloIdentityClient.mutation(
                LoginMutation(email, password)
            ).execute()
            Log.i("Response", response.toString())
            Log.i("obiekty", "$email $password")

            response.exception?.let { return Result.Error(it.toLoginNetworkError()) }

            if (response.hasErrors()) {
                return Result.Error(response.errorCode().toLoginError())
            }

            val payload = response.data?.login ?: return Result.Error(LoginError.Data.UNKNOWN_ERROR)

            return Result.Success(AuthToken(payload.token, payload.refreshToken))
        } catch (_: ApolloException) {
            return Result.Error(LoginError.Network.NETWORK_ERROR)
        }
    }
}