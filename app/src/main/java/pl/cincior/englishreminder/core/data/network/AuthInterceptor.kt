package pl.cincior.englishreminder.core.data.network

import okhttp3.Interceptor
import okhttp3.Response
import pl.cincior.englishreminder.core.domain.repository.TokenStorage
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = tokenStorage.getAuthTokenSync()

        val request = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}