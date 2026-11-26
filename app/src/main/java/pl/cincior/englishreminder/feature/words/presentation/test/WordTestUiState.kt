package pl.cincior.englishreminder.feature.words.presentation.test

import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.feature.words.domain.model.TestWordResult
import pl.cincior.englishreminder.feature.words.domain.model.UserWord
import pl.cincior.englishreminder.feature.words.domain.model.WordCategory

data class WordTestUiState(
    val words: List<UserWord> = emptyList(),
    val currentIndex: Int = 0,
    val isCategoryTest: Boolean = false,
    val category: WordCategory? = null,
    val isLoading: Boolean = true,
    val answer: String = "",
    val secondsRemaining: Int = 30,
    val isSubmitting: Boolean = false,
    val submittedTimeSeconds: Float? = null,
    val result: TestWordResult? = null,
    val isTimedOut: Boolean = false,
    val error: UiText? = null,
    val speechError: UiText? = null
) {
    val currentWord: UserWord?
        get() = words.getOrNull(currentIndex)

    val isLastWord: Boolean
        get() = currentIndex == words.lastIndex
}
