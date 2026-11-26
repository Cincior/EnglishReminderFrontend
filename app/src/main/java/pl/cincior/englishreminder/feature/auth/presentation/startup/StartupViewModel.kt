package pl.cincior.englishreminder.feature.auth.presentation.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.domain.repository.TokenStorage
import pl.cincior.englishreminder.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(StartupUiState())
    val uiState: StateFlow<StartupUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            validateSession()
        }
    }

    private suspend fun validateSession() {
        val rememberMe = tokenStorage.getRememberMe().first()
        val authToken = tokenStorage.getAuthToken().first()
        val refreshToken = tokenStorage.getRefreshToken().first()

        if (!rememberMe || authToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
            tokenStorage.clearAuthToken()
            tokenStorage.clearRefreshToken()
            tokenStorage.clearRememberMe()
            _uiState.update { it.copy(destination = StartupDestination.Login()) }
            return
        }

        val authTokenIsValid = try {
            !JWT(authToken).isExpired(10)
        } catch (_: DecodeException) {
            false
        } catch (_: Exception) {
            false
        }

        if (authTokenIsValid) {
            //TODO: check if token is valid by sending a request to backend
            _uiState.update { it.copy(destination = StartupDestination.Home) }
            return
        }

        when (val result = authRepository.refreshToken(refreshToken)) {
            is Result.Success -> {
                tokenStorage.saveAuthToken(result.data.jwtToken)
                tokenStorage.saveRefreshToken(result.data.refreshToken)
                _uiState.update { it.copy(destination = StartupDestination.Home) }
            }
            is Result.Error -> {
                tokenStorage.clearAuthToken()
                tokenStorage.clearRefreshToken()
                tokenStorage.clearRememberMe()
                _uiState.update {
                    it.copy(
                        destination = StartupDestination.Login(
                            startupError = "Sesja wygasła. Zaloguj się ponownie"
                        )
                    )
                }
            }
        }
    }
}
