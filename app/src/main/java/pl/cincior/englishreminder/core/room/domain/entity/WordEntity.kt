package pl.cincior.englishreminder.core.room.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val word: String,
    val translation: String,
    val insertDateTime: String,
    val memorizeChance: Float
)
