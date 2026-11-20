package pl.cincior.englishreminder.core.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.cincior.englishreminder.core.data.repository.FcmRepositoryImpl
import pl.cincior.englishreminder.feature.auth.data.repository.AuthRepositoryImpl
import pl.cincior.englishreminder.core.data.repository.TokenStorageImpl
import pl.cincior.englishreminder.core.domain.repository.FcmRepository
import pl.cincior.englishreminder.feature.auth.domain.repository.AuthRepository
import pl.cincior.englishreminder.core.domain.repository.TokenStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FcmModule {
    @Binds
    @Singleton
    abstract fun bindFcmRepository(
        fcmRepository: FcmRepositoryImpl
    ): FcmRepository
}