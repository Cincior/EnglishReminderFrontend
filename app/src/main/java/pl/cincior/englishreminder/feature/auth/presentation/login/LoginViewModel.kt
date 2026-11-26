package pl.cincior.englishreminder.feature.auth.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cincior.englishreminder.R
import pl.cincior.englishreminder.feature.auth.domain.repository.AuthRepository
import pl.cincior.englishreminder.core.domain.repository.TokenStorage
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.core.presentation.asUiText
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    rememberMe = tokenStorage.getRememberMe().first(),
                    rememberMeLoaded = true
                )
            }
        }
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, error = null) }
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, error = null) }
    }

    fun onRememberMeChanged(rememberMe: Boolean) {
        _uiState.update { it.copy(rememberMe = rememberMe) }
    }

    fun showStartupError(message: String) {
        if (message.isNotBlank()) {
            _uiState.update { it.copy(error = UiText.DynamicString(message)) }
        }
    }

    fun onLoginClick() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = UiText.StringResource(R.string.error_email_or_password_empty)) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = authRepository.login(state.email, state.password)) {
                is Result.Success -> {
                    tokenStorage.saveAuthToken(result.data.jwtToken)
                    tokenStorage.saveRefreshToken(result.data.refreshToken)
                    tokenStorage.saveRememberMe(state.rememberMe)
                    Log.i("token: ", result.data.jwtToken)
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
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