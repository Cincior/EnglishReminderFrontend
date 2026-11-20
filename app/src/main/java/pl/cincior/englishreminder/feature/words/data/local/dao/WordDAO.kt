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
}
