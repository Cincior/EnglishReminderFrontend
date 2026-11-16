package pl.cincior.englishreminder.core.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.domain.model.word.UserWord
import pl.cincior.englishreminder.core.domain.model.error.WordError
import pl.cincior.englishreminder.core.domain.model.word.AddWordResult
import pl.cincior.englishreminder.core.domain.model.word.DeleteWordResult
import pl.cincior.englishreminder.core.domain.model.word.ScheduleNotificationResult
import pl.cincior.englishreminder.core.domain.model.word.TestWordResult
import pl.cincior.englishreminder.core.domain.model.word.UpdateWordResult

interface WordRepository {
    fun getUserWords(): Flow<Result<List<UserWord>, WordError>>
    suspend fun fetchUserWords(): Result<Unit, WordError>
    fun getUserWord(wordId: String): Flow<Result<UserWord, WordError>>
    suspend fun addWord(word: String, translation: String, forceDuplicate: Boolean): Result<AddWordResult, WordError>
    suspend fun updateWord(wordId: String, newWord: String, newTranslation: String): Result<UpdateWordResult, WordError>
    suspend fun deleteWord(wordId: String): Result<DeleteWordResult, WordError>
    suspend fun testWord(wordId: String, translation: String): Result<TestWordResult, WordError>
    suspend fun scheduleNotification(wordId: String, scheduleTime: String): Result<ScheduleNotificationResult, WordError>
}