package pl.cincior.englishreminder.core.di.module

import com.apollographql.apollo.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.cincior.englishreminder.core.data.network.ApolloClientFactory
import pl.cincior.englishreminder.core.di.qualifier.IdentityApolloClient
import pl.cincior.englishreminder.core.di.qualifier.WordApolloClient
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