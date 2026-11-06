package pl.cincior.englishreminder.core.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.cincior.englishreminder.feature.auth.domain.repository.TokenStorage
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ApolloClientFactory @Inject constructor(
    private val tokenStorage: TokenStorage
) {

    private fun createBaseOkHttpClientBuilder(): OkHttpClient.Builder {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
    }

    fun createPublicApolloClient(serverUrl: String): ApolloClient {
        val okHttpClient = createBaseOkHttpClientBuilder().build()

        return ApolloClient.Builder()
            .serverUrl(serverUrl)
            .okHttpClient(okHttpClient)
            .build()
    }

    fun createAuthorizedApolloClient(serverUrl: String): ApolloClient {
        val okHttpClient = createBaseOkHttpClientBuilder()
            .addInterceptor(AuthInterceptor(tokenStorage))
            .build()

        return ApolloClient.Builder()
            .serverUrl(serverUrl)
            .okHttpClient(okHttpClient)
            .build()
    }
}

fun ApolloResponse<*>.errorCode(): String? =
    errors?.firstOrNull()?.extensions?.get("errorCode") as? String