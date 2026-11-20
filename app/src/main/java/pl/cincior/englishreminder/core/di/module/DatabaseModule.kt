package pl.cincior.englishreminder.core.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.cincior.englishreminder.feature.words.data.local.WordDatabase
import pl.cincior.englishreminder.feature.words.data.local.dao.WordDAO
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWordDatabase(@ApplicationContext context: Context): WordDatabase {
        return Room.databaseBuilder(
            context,
            WordDatabase::class.java,
            "word_database"
        ).fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideWordDao(wordDatabase: WordDatabase): WordDAO {
        return wordDatabase.wordDao()
    }
}
