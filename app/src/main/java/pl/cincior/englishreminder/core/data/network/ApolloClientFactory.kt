package pl.cincior.englishreminder.core.data.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

class ApolloClientFactory @Inject constructor(
    private val tokenRefreshInterceptor: Provider<TokenRefreshInterceptor>,
    private val authInterceptor: Provider<AuthInterceptor>
) {

    private fun createBaseOkHttpClientBuilder(): OkHttpClient.Builder {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
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
            .addInterceptor(authInterceptor.get())
            .addInterceptor(tokenRefreshInterceptor.get())
            .build()

        return ApolloClient.Builder()
            .serverUrl(serverUrl)
            .okHttpClient(okHttpClient)
            .build()
    }
}

fun ApolloResponse<*>.errorCode(): String? =
    errors?.firstOrNull()?.extensions?.get("errorCode") as? String
