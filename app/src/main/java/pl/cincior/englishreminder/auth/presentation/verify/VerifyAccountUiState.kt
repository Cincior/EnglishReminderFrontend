package pl.cincior.englishreminder.auth.presentation.verify

import pl.cincior.englishreminder.core.presentation.UiText

data class VerifyAccountUiState(
    val email: String = "",
    val code: String = "",
    val isLoading: Boolean = false,
    val isVerified: Boolean = false,
    val error: UiText? = null
)