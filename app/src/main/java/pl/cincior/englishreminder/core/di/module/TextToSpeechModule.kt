package pl.cincior.englishreminder.core.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.cincior.englishreminder.core.data.tts.AndroidTextToSpeechService
import pl.cincior.englishreminder.core.domain.service.TextToSpeechService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TextToSpeechModule {
    @Binds
    @Singleton
    abstract fun bindTextToSpeechService(
        androidTextToSpeechService: AndroidTextToSpeechService
    ): TextToSpeechService
}
