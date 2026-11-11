package pl.cincior.englishreminder.core.room.data

import android.content.Context
import androidx.room.Database
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import pl.cincior.englishreminder.core.room.domain.WordDAO
import pl.cincior.englishreminder.core.room.domain.entity.WordEntity
import kotlin.jvm.java

@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDAO
}