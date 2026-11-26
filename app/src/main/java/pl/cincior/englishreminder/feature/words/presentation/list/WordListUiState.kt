package pl.cincior.englishreminder.feature.words.presentation.list

import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.feature.words.domain.model.UserWord
import pl.cincior.englishreminder.feature.words.domain.model.WordCategory

data class WordListUiState(
    val wordList: List<UserWord> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,

    val isCategoryExpanded: Boolean = false,
    val category: WordCategory? = null
)
