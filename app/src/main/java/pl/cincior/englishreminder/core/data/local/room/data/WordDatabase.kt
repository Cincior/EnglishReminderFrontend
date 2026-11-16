package pl.cincior.englishreminder.core.data.local.room.data

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.cincior.englishreminder.core.data.local.room.data.dao.WordDAO
import pl.cincior.englishreminder.core.data.local.room.data.entity.WordEntity

@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDAO
}