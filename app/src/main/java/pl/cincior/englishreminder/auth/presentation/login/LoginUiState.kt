package pl.cincior.englishreminder.auth.presentation.login

import pl.cincior.englishreminder.core.presentation.UiText

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: UiText? = null
)
