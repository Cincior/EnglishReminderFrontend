package pl.cincior.englishreminder.core.di.qualifier

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IdentityApolloClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WordApolloClient