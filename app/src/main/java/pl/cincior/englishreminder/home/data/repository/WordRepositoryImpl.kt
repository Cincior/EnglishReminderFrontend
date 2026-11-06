package pl.cincior.englishreminder.home.data.repository

import com.apollographql.apollo.ApolloClient
import pl.cincior.englishreminder.core.domain.Result
import pl.cincior.englishreminder.core.domain.WordError
import pl.cincior.englishreminder.core.network.errorCode
import pl.cincior.englishreminder.di.WordApolloClient
import pl.cincior.englishreminder.home.data.repository.mapper.toWordError
import pl.cincior.englishreminder.home.data.repository.mapper.toWordException
import pl.cincior.englishreminder.home.domain.model.AddWordResult
import pl.cincior.englishreminder.home.domain.model.DeleteWordResult
import pl.cincior.englishreminder.home.domain.model.ScheduleNotificationResult
import pl.cincior.englishreminder.home.domain.model.TestWordResult
import pl.cincior.englishreminder.home.domain.model.UpdateWordResult
import pl.cincior.englishreminder.home.domain.model.UserWord
import pl.cincior.englishreminder.home.domain.repository.WordRepository
import pl.cincior.englishreminder.words.graphql.GetUserWordsQuery
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    @param:WordApolloClient private val apolloWordClient: ApolloClient
) : WordRepository {
    override suspend fun getUserWords(): Result<List<UserWord>, WordError> {
        val result = apolloWordClient.query(
            GetUserWordsQuery()
        ).execute()

        result.exception?.let { return Result.Error(it.toWordException()) }

        if (result.hasErrors()) {
            return Result.Error(result.errorCode().toWordError())
        }

        val payload = result.data?.getUserWords ?: return Result.Error(WordError.Data.UNKNOWN_ERROR)

        return Result.Success(payload.map { userWord ->
            UserWord(
                id = userWord.id,
                word = userWord.word,
                translation = userWord.translation,
                insertDateTime = userWord.insertDateTime,
                memorizeChance = userWord.memorizeChance.toFloat()
            )
        })

    }

    override suspend fun addWord(
        word: String,
        translation: String,
        forceDuplicate: Boolean
    ): Result<AddWordResult, WordError> {
        TODO("Not yet implemented")
    }

    override suspend fun updateWord(
        wordId: String,
        newWord: String,
        newTranslation: String
    ): Result<UpdateWordResult, WordError> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWord(wordId: String): Result<DeleteWordResult, WordError> {
        TODO("Not yet implemented")
    }

    override suspend fun testWord(
        wordId: String,
        translation: String
    ): Result<TestWordResult, WordError> {
        TODO("Not yet implemented")
    }

    override suspend fun scheduleNotification(
        wordId: String,
        scheduleTime: String
    ): Result<ScheduleNotificationResult, WordError> {
        TODO("Not yet implemented")
    }
}