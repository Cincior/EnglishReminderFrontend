package pl.cincior.englishreminder.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IdentityApolloClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WordApolloClient