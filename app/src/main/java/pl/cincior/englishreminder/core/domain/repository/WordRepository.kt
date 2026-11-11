package pl.cincior.englishreminder.core.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.domain.model.UserWord
import pl.cincior.englishreminder.core.domain.model.error.WordError
import pl.cincior.englishreminder.feature.home.domain.model.AddWordResult
import pl.cincior.englishreminder.feature.home.domain.model.DeleteWordResult
import pl.cincior.englishreminder.feature.home.domain.model.ScheduleNotificationResult
import pl.cincior.englishreminder.feature.home.domain.model.TestWordResult
import pl.cincior.englishreminder.feature.home.domain.model.UpdateWordResult

interface WordRepository {
    val userWords: Flow<Result<List<UserWord>, WordError>>

    suspend fun getUserWords(): Result<List<UserWord>, WordError>
    suspend fun addWord(word: String, translation: String, forceDuplicate: Boolean): Result<AddWordResult, WordError>
    suspend fun updateWord(wordId: String, newWord: String, newTranslation: String): Result<UpdateWordResult, WordError>
    suspend fun deleteWord(wordId: String): Result<DeleteWordResult, WordError>
    suspend fun testWord(wordId: String, translation: String): Result<TestWordResult, WordError>
    suspend fun scheduleNotification(wordId: String, scheduleTime: String): Result<ScheduleNotificationResult, WordError>
}