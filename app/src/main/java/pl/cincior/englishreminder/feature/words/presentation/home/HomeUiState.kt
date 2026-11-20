package pl.cincior.englishreminder.feature.words.presentation.home

import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.feature.words.domain.model.UserWord

data class HomeUiState(
    val userWords: List<UserWord> = emptyList(),
    val nickName: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: UiText? = null
)
