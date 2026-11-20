package pl.cincior.englishreminder.feature.words.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.cincior.englishreminder.feature.words.data.local.dao.WordDAO
import pl.cincior.englishreminder.feature.words.data.local.entity.WordEntity

@Database(entities = [WordEntity::class], version = 2, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDAO
}
