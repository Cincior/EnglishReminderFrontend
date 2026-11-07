package pl.cincior.englishreminder.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.cincior.englishreminder.feature.auth.data.repository.AuthRepositoryImpl
import pl.cincior.englishreminder.feature.home.data.repository.WordRepositoryImpl
import pl.cincior.englishreminder.feature.home.domain.repository.WordRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WordModule {
    @Binds
    @Singleton
    abstract fun bindWordRepository(
        wordRepositoryImpl: WordRepositoryImpl
    ): WordRepository
}