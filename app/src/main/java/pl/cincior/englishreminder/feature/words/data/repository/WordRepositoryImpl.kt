package pl.cincior.englishreminder.feature.words.data.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import pl.cincior.englishreminder.core.data.network.errorCode
import pl.cincior.englishreminder.core.di.qualifier.WordApolloClient
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.domain.model.error.WordError
import pl.cincior.englishreminder.feature.words.data.local.dao.WordDAO
import pl.cincior.englishreminder.feature.words.data.local.entity.WordEntity
import pl.cincior.englishreminder.feature.words.data.mapper.toWordError
import pl.cincior.englishreminder.feature.words.data.mapper.toWordException
import pl.cincior.englishreminder.feature.words.domain.model.AddWordResult
import pl.cincior.englishreminder.feature.words.domain.model.DeleteWordResult
import pl.cincior.englishreminder.feature.words.domain.model.ScheduleNotificationResult
import pl.cincior.englishreminder.feature.words.domain.model.TestWordResult
import pl.cincior.englishreminder.feature.words.domain.model.UpdateWordResult
import pl.cincior.englishreminder.feature.words.domain.model.UserWord
import pl.cincior.englishreminder.feature.words.domain.model.WordCategory
import pl.cincior.englishreminder.feature.words.domain.repository.WordRepository
import pl.cincior.englishreminder.words.graphql.AddWordMutation
import pl.cincior.englishreminder.words.graphql.DeleteWordMutation
import pl.cincior.englishreminder.words.graphql.GetUserWordsQuery
import pl.cincior.englishreminder.words.graphql.ScheduleNotificationMutation
import pl.cincior.englishreminder.words.graphql.SetCategoryMutation
import pl.cincior.englishreminder.words.graphql.TestWordMutation
import pl.cincior.englishreminder.words.graphql.UpdateWordMutation
import pl.cincior.englishreminder.words.graphql.type.Category
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    @param:WordApolloClient private val apolloWordClient: ApolloClient,
    private val wordDAO: WordDAO
) : WordRepository {

    override fun getUserWords(): Flow<Result<List<UserWord>, WordError>> {
        return wordDAO.getAllWords()
            .map<List<WordEntity>, Result<List<UserWord>, WordError>> { entities ->
                val userWords = entities.map { entity ->
                    UserWord(
                        id = entity.id,
                        word = entity.word,
                        translation = entity.translation,
                        insertDateTime = entity.insertDateTime,
                        category = entity.category.toWordCategory(),
                        memorizeChance = entity.memorizeChance
                    )
                }
                Result.Success(userWords)
            }
            .catch {
                emit(Result.Error(WordError.Data.LOCAL_STORAGE_ERROR))
            }
    }

    override suspend fun fetchUserWords(): Result<Unit, WordError> {
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
                category = userWord.category.toWordCategory(),
                memorizeChance = userWord.memorizeChance.toFloat()
            )
        }
        wordDAO.syncWords(userWords.map { userWord ->
            WordEntity(
                id = userWord.id,
                word = userWord.word,
                translation = userWord.translation,
                insertDateTime = userWord.insertDateTime,
                category = userWord.category.name,
                memorizeChance = userWord.memorizeChance
            )
        })
        return Result.Success(Unit)
    }

    override fun getUserWord(wordId: String): Flow<Result<UserWord, WordError>> {
        return wordDAO.getWordById(wordId)
            .map { entity ->
                if (entity != null) {
                    Result.Success(
                        UserWord(
                            id = entity.id,
                            word = entity.word,
                            translation = entity.translation,
                            insertDateTime = entity.insertDateTime,
                            category = entity.category.toWordCategory(),
                            memorizeChance = entity.memorizeChance
                        )
                    )
                } else {
                    Result.Error(WordError.Data.WORD_NOT_FOUND)
                }
            }
            .catch {
                emit(Result.Error(WordError.Data.LOCAL_STORAGE_ERROR))
            }
    }

    override suspend fun addWord(
        word: String,
        translation: String,
        forceDuplicate: Boolean,
        category: WordCategory
    ): Result<AddWordResult, WordError> {
        val response = apolloWordClient.mutation(
            AddWordMutation(
                word = word,
                translation = translation,
                forceDuplicate = forceDuplicate,
                category = category.toCategory()
            )
        ).execute()

        response.exception?.let { return Result.Error(it.toWordException()) }
        if (response.hasErrors()) {
            return Result.Error(response.errorCode().toWordError())
        }

        val data = response.data?.addWord ?: return Result.Error(WordError.Data.UNKNOWN_ERROR)
        return Result.Success(
            AddWordResult(
                created = data.created,
                wordId = data.wordId,
                existingWordId = data.existingWordId
            )
        )
    }

    override suspend fun setCategory(
        wordId: String,
        category: WordCategory
    ): Result<Unit, WordError> {
        val graphqlCategory = when (category) {
            WordCategory.LEARN_NOW -> Category.LEARN_NOW
            WordCategory.LEARN_LATER -> Category.LEARN_LATER
            WordCategory.LEARNED -> Category.LEARNED
            WordCategory.NOT_LEARNED -> Category.NOT_LEARNED
        }

        val response = apolloWordClient.mutation(
            SetCategoryMutation(
                wordId = wordId,
                category = graphqlCategory
            )
        ).execute()

        response.exception?.let { return Result.Error(it.toWordException()) }
        if (response.hasErrors()) {
            return Result.Error(response.errorCode().toWordError())
        }

        wordDAO.updateCategory(wordId, category.name)
        return Result.Success(Unit)
    }

    override suspend fun scheduleNotification(
        wordId: String,
        scheduleTime: String
    ): Result<ScheduleNotificationResult, WordError> {
        Log.i("Time in repo", scheduleTime)
        val response = apolloWordClient.mutation(
            ScheduleNotificationMutation(
                wordId = wordId,
                scheduleTime = scheduleTime
            )
        ).execute()

        response.exception?.let { return Result.Error(it.toWordException()) }
        if (response.hasErrors()) {
            return Result.Error(response.errorCode().toWordError())
        }

        val payload = response.data?.scheduleNotification
        return if (payload != null) {
            Result.Success(ScheduleNotificationResult(id = payload.wordId))
        } else {
            Result.Error(WordError.Data.FAILED_TO_SCHEDULE_NOTIFICATION)
        }
    }

    override suspend fun updateWord(
        wordId: String,
        newWord: String,
        newTranslation: String
    ): Result<UpdateWordResult, WordError> {
        val response = apolloWordClient.mutation(
            UpdateWordMutation(
                wordId = wordId,
                word = newWord,
                translation = newTranslation
            )
        ).execute()

        response.exception?.let { return Result.Error(it.toWordException()) }
        if (response.hasErrors()) {
            return Result.Error(response.errorCode().toWordError())
        }

        val payload = response.data?.updateWord ?: return Result.Error(WordError.Data.UNKNOWN_ERROR)

        wordDAO.updateWord(wordId, payload.word, payload.translation)

        return Result.Success(
            UpdateWordResult(
                id = payload.wordId,
                word = payload.word,
                translation = payload.translation
            )
        )
    }

    override suspend fun deleteWord(wordId: String): Result<DeleteWordResult, WordError> {
        val response = apolloWordClient.mutation(
            DeleteWordMutation(wordId = wordId)
        ).execute()

        response.exception?.let { return Result.Error(it.toWordException()) }
        if (response.hasErrors()) {
            return Result.Error(response.errorCode().toWordError())
        }

        val payload = response.data?.deleteWord ?: return Result.Error(WordError.Data.UNKNOWN_ERROR)

        if (payload.deleted) {
            wordDAO.deleteWordById(wordId)
        }

        return Result.Success(
            DeleteWordResult(
                id = payload.wordId,
                deleted = payload.deleted
            )
        )
    }

    override suspend fun testWord(
        wordId: String,
        translation: String
    ): Result<TestWordResult, WordError> {
        val response = apolloWordClient.mutation(
            TestWordMutation(
                wordId = wordId,
                translation = translation
            )
        ).execute()

        response.exception?.let { return Result.Error(it.toWordException()) }
        if (response.hasErrors()) {
            return Result.Error(response.errorCode().toWordError())
        }

        val payload = response.data?.testWord ?: return Result.Error(WordError.Data.UNKNOWN_ERROR)

        return Result.Success(
            TestWordResult(
                id = payload.wordId,
                isCorrect = payload.isCorrect,
                testTime = payload.testTime
            )
        )
    }

    private fun Category.toWordCategory(): WordCategory = when (this) {
        Category.LEARN_NOW -> WordCategory.LEARN_NOW
        Category.LEARN_LATER -> WordCategory.LEARN_LATER
        Category.LEARNED -> WordCategory.LEARNED
        Category.NOT_LEARNED -> WordCategory.NOT_LEARNED
        else -> WordCategory.NOT_LEARNED
    }

    private fun String.toWordCategory(): WordCategory = try {
        WordCategory.valueOf(this)
    } catch (e: Exception) {
        WordCategory.NOT_LEARNED
    }

    private fun WordCategory.toCategory(): Category = when (this) {
        WordCategory.LEARN_NOW -> Category.LEARN_NOW
        WordCategory.LEARN_LATER -> Category.LEARN_LATER
        WordCategory.LEARNED -> Category.LEARNED
        WordCategory.NOT_LEARNED -> Category.NOT_LEARNED
    }
}
