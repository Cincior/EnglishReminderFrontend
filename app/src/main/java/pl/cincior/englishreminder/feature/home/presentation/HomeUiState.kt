package pl.cincior.englishreminder.feature.home.presentation

import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.feature.home.domain.model.UserWord

data class HomeUiState(
    val userWords: List<UserWord> = emptyList(),
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: UiText? = null
)