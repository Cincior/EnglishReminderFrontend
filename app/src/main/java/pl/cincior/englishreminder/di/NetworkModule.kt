package pl.cincior.englishreminder.di

import com.apollographql.apollo.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.cincior.englishreminder.feature.auth.domain.repository.TokenStorage
import pl.cincior.englishreminder.core.network.ApolloClientFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @IdentityApolloClient
    @Provides
    @Singleton
    fun provideApolloIdentityClient(
        apolloClientFactory: ApolloClientFactory
    ) : ApolloClient {
        return apolloClientFactory.createPublicApolloClient("http://10.0.2.2:8080/graphql/IdentityService")
    }

    @WordApolloClient
    @Provides
    @Singleton
    fun provideApolloWordClient(
        apolloClientFactory: ApolloClientFactory
    ): ApolloClient {
        return apolloClientFactory.createAuthorizedApolloClient("http://10.0.2.2:8080/graphql/wordService")
    }
}