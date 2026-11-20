package pl.cincior.englishreminder.feature.words.presentation.details

import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.feature.words.domain.model.UserWord

data class WordDetailsUiState(
    val word: UserWord? = null,
    val isLoading: Boolean = false,
    val error: UiText? = null
)
