package pl.cincior.englishreminder.home.presentation

import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.home.domain.model.UserWord

data class HomeUiState(
    val userWords: List<UserWord> = emptyList(),
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: UiText? = null
)