package pl.cincior.englishreminder.feature.home.domain.repository

import pl.cincior.englishreminder.core.domain.Result
import pl.cincior.englishreminder.core.domain.WordError
import pl.cincior.englishreminder.feature.home.domain.model.AddWordResult
import pl.cincior.englishreminder.feature.home.domain.model.DeleteWordResult
import pl.cincior.englishreminder.feature.home.domain.model.ScheduleNotificationResult
import pl.cincior.englishreminder.feature.home.domain.model.TestWordResult
import pl.cincior.englishreminder.feature.home.domain.model.UpdateWordResult
import pl.cincior.englishreminder.feature.home.domain.model.UserWord

interface WordRepository {
    suspend fun getUserWords(): Result<List<UserWord>, WordError>
    suspend fun addWord(word: String, translation: String, forceDuplicate: Boolean): Result<AddWordResult, WordError>
    suspend fun updateWord(wordId: String, newWord: String, newTranslation: String): Result<UpdateWordResult, WordError>
    suspend fun deleteWord(wordId: String): Result<DeleteWordResult, WordError>
    suspend fun testWord(wordId: String, translation: String): Result<TestWordResult, WordError>
    suspend fun scheduleNotification(wordId: String, scheduleTime: String): Result<ScheduleNotificationResult, WordError>
}