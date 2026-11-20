package pl.cincior.englishreminder.core.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.cincior.englishreminder.core.data.repository.InstallationIdRepositoryImpl
import pl.cincior.englishreminder.core.domain.repository.InstallationIdRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FcmModule {
    @Binds
    @Singleton
    abstract fun bindInstallationIdRepository(
        repository: InstallationIdRepositoryImpl
    ): InstallationIdRepository
}