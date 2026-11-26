package pl.cincior.englishreminder.feature.words.presentation.test

import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.domain.model.error.WordError
import pl.cincior.englishreminder.core.domain.service.TextToSpeechService
import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.core.presentation.asUiText
import pl.cincior.englishreminder.feature.words.domain.model.WordCategory
import pl.cincior.englishreminder.feature.words.domain.repository.WordRepository
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class WordTestViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
    private val textToSpeechService: TextToSpeechService
) : ViewModel() {
    private val wordId = savedStateHandle.get<String>("wordId")?.takeIf(String::isNotBlank)
    private val categoryName = savedStateHandle.get<String>("category")?.takeIf(String::isNotBlank)
    private val category = categoryName?.let { name ->
        WordCategory.entries.firstOrNull { it.name == name }
    }

    private val _uiState = MutableStateFlow(
        WordTestUiState(
            isCategoryTest = categoryName != null,
            category = category
        )
    )
    val uiState: StateFlow<WordTestUiState> = _uiState.asStateFlow()

    private var hasLoadedWords = false
    private var itemStartedAt: Long? = null
    private var timerJob: Job? = null

    init {
        val hasWordId = !wordId.isNullOrBlank()
        val hasCategory = !categoryName.isNullOrBlank()
        if (hasWordId == hasCategory || (hasCategory && category == null)) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = UiText.DynamicString("Nieprawidłowy zestaw słówek do testu.")
                )
            }
        } else {
            observeWords()
        }
    }

    private fun observeWords() {
        viewModelScope.launch {
            wordRepository.getUserWords().collect { result ->
                if (hasLoadedWords) return@collect

                when (result) {
                    is Result.Success -> {
                        hasLoadedWords = true
                        val selectedWords = if (wordId != null) {
                            result.data.filter { it.id == wordId }
                        } else {
                            result.data.filter { it.category == category }
                        }

                        if (selectedWords.isEmpty()) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = UiText.DynamicString(
                                        if (wordId != null) {
                                            "Nie znaleziono słówka do testu."
                                        } else {
                                            "Brak słówek w wybranej kategorii."
                                        }
                                    )
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(words = selectedWords, isLoading = false, error = null)
                            }
                            startTimer()
                        }
                    }
                    is Result.Error -> {
                        hasLoadedWords = true
                        _uiState.update {
                            it.copy(isLoading = false, error = result.error.asUiText())
                        }
                    }
                }
            }
        }
    }

    fun onAnswerChanged(answer: String) {
        val state = _uiState.value
        if (state.result == null && !state.isSubmitting) {
            _uiState.update { it.copy(answer = answer, error = null) }
        }
    }

    fun checkAnswer() {
        submitAnswer(isTimeout = false)
    }

    private fun submitAnswer(isTimeout: Boolean) {
        val state = _uiState.value
        val currentWord = state.currentWord ?: return
        if (state.isSubmitting || state.result != null) return

        val elapsedSeconds = state.submittedTimeSeconds ?: elapsedTimeSeconds()
        _uiState.update {
            it.copy(
                isSubmitting = true,
                submittedTimeSeconds = elapsedSeconds,
                secondsRemaining = ceil((TEST_DURATION_SECONDS - elapsedSeconds).coerceAtLeast(0f)).toInt(),
                error = null
            )
        }

        viewModelScope.launch {
            when (
                val result = wordRepository.testWord(
                    wordId = currentWord.id,
                    translation = state.answer,
                    timeToAnswer = elapsedSeconds
                )
            ) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSubmitting = false, result = result.data) }
                }
                is Result.Error -> {
                    if (isTimeout &&
                        result.error == WordError.Data.INVALID_WORD_DATA
                    ) {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                isTimedOut = true,
                                error = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                submittedTimeSeconds = null,
                                error = result.error.asUiText()
                            )
                        }
                    }
                }
            }
        }
    }

    fun moveToNextWord() {
        val state = _uiState.value
        if (state.result == null || state.currentIndex >= state.words.lastIndex) return

        _uiState.update {
            it.copy(
                currentIndex = it.currentIndex + 1,
                answer = "",
                secondsRemaining = TEST_DURATION_SECONDS.toInt(),
                isSubmitting = false,
                submittedTimeSeconds = null,
                result = null,
                isTimedOut = false,
                error = null,
                speechError = null
            )
        }
        startTimer()
    }

    fun pronounceWord() {
        val word = _uiState.value.currentWord?.word ?: return
        _uiState.update { it.copy(speechError = null) }
        textToSpeechService.speak(word) { message ->
            _uiState.update { it.copy(speechError = UiText.DynamicString(message)) }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        itemStartedAt = SystemClock.elapsedRealtime()
        _uiState.update {
            it.copy(
                secondsRemaining = TEST_DURATION_SECONDS.toInt(),
                submittedTimeSeconds = null
            )
        }

        timerJob = viewModelScope.launch {
            while (true) {
                if (_uiState.value.result != null) break

                val elapsedMillis = elapsedTimeMillis()
                val remainingSeconds = ceil(
                    ((TEST_DURATION_MILLIS - elapsedMillis).coerceAtLeast(0L)) / 1_000f
                ).toInt()
                _uiState.update { it.copy(secondsRemaining = remainingSeconds) }

                if (elapsedMillis >= TEST_DURATION_MILLIS) {
                    submitAnswer(isTimeout = true)
                    break
                }
                delay(TIMER_TICK_MILLIS)
            }
        }
    }

    private fun elapsedTimeSeconds(): Float =
        elapsedTimeMillis() / 1_000f

    private fun elapsedTimeMillis(): Long {
        val startedAt = itemStartedAt ?: return 0L
        return (SystemClock.elapsedRealtime() - startedAt).coerceIn(0L, TEST_DURATION_MILLIS)
    }

    override fun onCleared() {
        timerJob?.cancel()
        textToSpeechService.stop()
        super.onCleared()
    }

    private companion object {
        const val TEST_DURATION_SECONDS = 30f
        const val TEST_DURATION_MILLIS = 30_000L
        const val TIMER_TICK_MILLIS = 100L
    }
}
