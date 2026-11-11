package pl.cincior.englishreminder.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cincior.englishreminder.feature.auth.domain.repository.AuthRepository
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.core.presentation.asUiText
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChanged(newName: String) {
        _uiState.update { it.copy(name = newName, error = null) }
    }

    fun onSurnameChanged(newSurname: String) {
        _uiState.update { it.copy(surname = newSurname, error = null) }
    }

    fun onNicknameChanged(newNickname: String) {
        _uiState.update { it.copy(nickname = newNickname, error = null) }
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, error = null) }
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, error = null) }
    }

    fun registerUser() {
        val state = _uiState.value

        // Walidacja po stronie klienta
        if (state.name.isBlank() || state.surname.isBlank() || state.nickname.isBlank() ||
            state.email.isBlank() || state.password.isBlank()
        ) {
            _uiState.update { it.copy(error = UiText.DynamicString("Wszystkie pola muszą być wypełnione")) }
            return
        }

        if (!state.email.contains("@")) {
            _uiState.update { it.copy(error = UiText.DynamicString("Niepoprawny format adresu e-mail")) }
            return
        }

        if (state.password.length < 6) {
            _uiState.update { it.copy(error = UiText.DynamicString("Hasło musi mieć co najmniej 6 znaków")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = authRepository.register(
                name = state.name,
                surname = state.surname,
                nickname = state.nickname,
                role = state.role,
                email = state.email,
                password = state.password
            )) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegistered = true,
                            verificationRequired = result.data.verificationRequired
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