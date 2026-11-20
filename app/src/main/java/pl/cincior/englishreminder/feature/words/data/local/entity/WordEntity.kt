package pl.cincior.englishreminder.feature.words.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val id: String,
    val word: String,
    val translation: String,
    val insertDateTime: String,
    val category: String = "NOT_LEARNED",
    val memorizeChance: Float
)
