package pl.cincior.englishreminder.auth.presentation.register

import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.identity.graphql.type.UserRole

data class RegisterUiState(
    val name: String = "",
    val surname: String = "",
    val nickname: String = "",
    val role: UserRole = UserRole.USER,
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val verificationRequired: Boolean = false,
    val error: UiText? = null
)
