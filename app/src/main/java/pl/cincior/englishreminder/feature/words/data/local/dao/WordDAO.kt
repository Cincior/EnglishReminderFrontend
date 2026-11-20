package pl.cincior.englishreminder.feature.words.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.cincior.englishreminder.feature.words.data.local.entity.WordEntity

@Dao
interface WordDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Query("SELECT * FROM words")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id = :wordId")
    fun getWordById(wordId: String): Flow<WordEntity?>

    @Query("UPDATE words SET word = :word, translation = :translation WHERE id = :wordId")
    suspend fun updateWord(wordId: String, word: String, translation: String)

    @Query("UPDATE words SET category = :category WHERE id = :wordId")
    suspend fun updateCategory(wordId: String, category: String)

    @Query("DELETE FROM words WHERE id = :wordId")
    suspend fun deleteWordById(wordId: String)
}
