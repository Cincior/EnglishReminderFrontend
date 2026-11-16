package pl.cincior.englishreminder.feature.wordDetails.presentation

import pl.cincior.englishreminder.core.domain.model.word.UserWord
import pl.cincior.englishreminder.core.presentation.UiText

data class WordDetailsUiState(
    val word: UserWord? = null,
    val isLoading: Boolean = false,
    val error: UiText? = null
)