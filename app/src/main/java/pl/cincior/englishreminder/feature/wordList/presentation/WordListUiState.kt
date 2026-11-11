package pl.cincior.englishreminder.feature.wordList.presentation

import pl.cincior.englishreminder.core.domain.model.UserWord
import pl.cincior.englishreminder.core.presentation.UiText

data class WordListUiState(
    val wordList: List<UserWord> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null
)
