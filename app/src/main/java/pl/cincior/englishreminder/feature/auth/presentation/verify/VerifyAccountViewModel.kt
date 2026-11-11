package pl.cincior.englishreminder.feature.auth.presentation.verify

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
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.core.presentation.asUiText
import javax.inject.Inject

@HiltViewModel
class VerifyAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val initialEmail: String = savedStateHandle.get<String>("email") ?: ""

    private val _uiState = MutableStateFlow(VerifyAccountUiState(email = initialEmail))
    val uiState: StateFlow<VerifyAccountUiState> = _uiState.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, error = null) }
    }

    fun onCodeChanged(newCode: String) {
        _uiState.update { it.copy(code = newCode, error = null) }
    }

    fun verifyAccount() {
        val state = _uiState.value

        if (state.email.isBlank() || state.code.isBlank()) {
            _uiState.update { it.copy(error = UiText.DynamicString("Email oraz kod weryfikacyjny są wymagane")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = authRepository.verifyUser(state.email, state.code)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isVerified = result.data.verified
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