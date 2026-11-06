package pl.cincior.englishreminder.feature.auth.presentation.reset_request

import pl.cincior.englishreminder.core.presentation.UiText

data class ResetRequestUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isRequested: Boolean = false,
    val error: UiText? = null
)