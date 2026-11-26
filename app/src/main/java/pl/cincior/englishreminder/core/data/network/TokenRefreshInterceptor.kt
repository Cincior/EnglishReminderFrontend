package pl.cincior.englishreminder.core.data.network

import com.apollographql.apollo.ApolloClient
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import pl.cincior.englishreminder.core.di.qualifier.IdentityApolloClient
import pl.cincior.englishreminder.core.domain.repository.TokenStorage
import pl.cincior.englishreminder.identity.graphql.RefreshMutation
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class TokenRefreshInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage,
    @param:IdentityApolloClient private val identityApolloClient: ApolloClient
) : Interceptor {

    private val lock = Any()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        if (response.code == 401) {
            synchronized(lock) {
                val currentToken = runBlocking { tokenStorage.getAuthToken().firstOrNull() }
                val requestToken = originalRequest.header("Authorization")?.removePrefix("Bearer ")

                if (!currentToken.isNullOrBlank() && currentToken != requestToken) {
                    response.close()
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                    return chain.proceed(newRequest)
                }

                val refreshToken = runBlocking { tokenStorage.getRefreshToken().firstOrNull() }
                if (refreshToken.isNullOrBlank()) {
                    runBlocking {
                        tokenStorage.clearAuthToken()
                        tokenStorage.clearRefreshToken()
                        tokenStorage.clearRememberMe()
                    }
                    return response
                }

                val newTokens = performTokenRefresh(refreshToken)
                if (newTokens != null) {
                    runBlocking {
                        tokenStorage.saveAuthToken(newTokens.first)
                        tokenStorage.saveRefreshToken(newTokens.second)
                    }

                    response.close()
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.first}")
                        .build()
                    return chain.proceed(newRequest)
                } else {
                    runBlocking {
                        tokenStorage.clearAuthToken()
                        tokenStorage.clearRefreshToken()
                        tokenStorage.clearRememberMe()
                    }
                }
            }
        }

        return response
    }

    private fun performTokenRefresh(refreshToken: String): Pair<String, String>? {
        return try {
            val response = runBlocking {
                identityApolloClient.mutation(RefreshMutation(refreshToken = refreshToken)).execute()
            }
            val refreshData = response.data?.refresh
            if (refreshData != null && refreshData.token.isNotBlank() && refreshData.refreshToken.isNotBlank()) {
                Pair(refreshData.token, refreshData.refreshToken)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
