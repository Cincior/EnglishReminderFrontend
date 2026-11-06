package pl.cincior.englishreminder.feature.auth.presentation.reset_password

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cincior.englishreminder.feature.auth.domain.repository.AuthRepository
import pl.cincior.englishreminder.core.domain.Result
import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.core.presentation.asUiText
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialEmail: String = savedStateHandle.get<String>("email") ?: ""

    private val _uiState = MutableStateFlow(ResetPasswordUiState(email = initialEmail))
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, error = null) }
    }

    fun onCodeChanged(newCode: String) {
        _uiState.update { it.copy(code = newCode, error = null) }
    }

    fun onNewPasswordChanged(newPassword: String) {
        _uiState.update { it.copy(newPassword = newPassword, error = null) }
    }

    fun resetPassword() {
        val state = _uiState.value

        if (state.email.isBlank() || state.code.isBlank() || state.newPassword.isBlank()) {
            _uiState.update { it.copy(error = UiText.DynamicString("Wszystkie pola są wymagane")) }
            return
        }

        if (state.newPassword.length < 6) {
            _uiState.update { it.copy(error = UiText.DynamicString("Nowe hasło musi mieć co najmniej 6 znaków")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = authRepository.resetPassword(state.email, state.code, state.newPassword)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isReset = result.data.reset
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.asUiText()
                        )
                    }
                }
            }
        }
    }
}