package pl.cincior.englishreminder.core.data.repository

import com.apollographql.apollo.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.domain.model.UserWord
import pl.cincior.englishreminder.core.domain.model.error.WordError
import pl.cincior.englishreminder.core.domain.repository.WordRepository
import pl.cincior.englishreminder.core.network.errorCode
import pl.cincior.englishreminder.di.WordApolloClient
import pl.cincior.englishreminder.core.data.repository.mapper.toWordError
import pl.cincior.englishreminder.core.data.repository.mapper.toWordException
import pl.cincior.englishreminder.core.room.domain.WordDAO
import pl.cincior.englishreminder.core.room.domain.entity.WordEntity
import pl.cincior.englishreminder.feature.home.domain.model.AddWordResult
import pl.cincior.englishreminder.feature.home.domain.model.DeleteWordResult
import pl.cincior.englishreminder.feature.home.domain.model.ScheduleNotificationResult
import pl.cincior.englishreminder.feature.home.domain.model.TestWordResult
import pl.cincior.englishreminder.feature.home.domain.model.UpdateWordResult
import pl.cincior.englishreminder.words.graphql.GetUserWordsQuery
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    @param:WordApolloClient private val apolloWordClient: ApolloClient,
    private val wordDAO: WordDAO
) : WordRepository {
    override val userWords: Flow<Result<List<UserWord>, WordError>> = wordDAO.getAllWords()
        .map { entities ->
            val userWords = entities.map { entity ->
                UserWord(
                    id = entity.id,
                    word = entity.word,
                    translation = entity.translation,
                    insertDateTime = entity.insertDateTime,
                    memorizeChance = entity.memorizeChance
                )
            }
            Result.Success(userWords)
       }
        .catch {
            Result.Error(WordError.Data.LOCAL_STORAGE_ERROR)
        }

    override suspend fun getUserWords(): Result<List<UserWord>, WordError> {
        val result = apolloWordClient.query(
            GetUserWordsQuery()
        ).execute()

        result.exception?.let { return Result.Error(it.toWordException()) }

        if (result.hasErrors()) {
            return Result.Error(result.errorCode().toWordError())
        }

        val payload = result.data?.getUserWords ?: return Result.Error(WordError.Data.UNKNOWN_ERROR)

        val userWords = payload.map { userWord ->
            UserWord(
                id = userWord.id,
                word = userWord.word,
                translation = userWord.translation,
                insertDateTime = userWord.insertDateTime,
                memorizeChance = userWord.memorizeChance.toFloat()
            )
        }
        wordDAO.insertWords(userWords.map { userWord ->
            WordEntity(
                id = userWord.id,
                word = userWord.word,
                translation = userWord.translation,
                insertDateTime = userWord.insertDateTime,
                memorizeChance = userWord.memorizeChance
            )
        })
        return Result.Success(userWords)

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