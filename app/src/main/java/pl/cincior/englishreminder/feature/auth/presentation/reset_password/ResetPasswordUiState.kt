package pl.cincior.englishreminder.feature.auth.presentation.reset_password

import pl.cincior.englishreminder.core.presentation.UiText

data class ResetPasswordUiState(
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val isLoading: Boolean = false,
    val isReset: Boolean = false,
    val error: UiText? = null
)