package pl.cincior.englishreminder.feature.words.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import pl.cincior.englishreminder.core.data.util.TokenClaimsService
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.domain.model.error.WordError
import pl.cincior.englishreminder.core.domain.repository.TokenStorage
import pl.cincior.englishreminder.core.presentation.asUiText
import pl.cincior.englishreminder.feature.words.domain.repository.WordRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val tokenClaimsService: TokenClaimsService,
    private val tokenStorage: TokenStorage
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        subscribeUserWords()
        refreshWordsFromBackend()
    }

    private fun subscribeUserWords() {
        viewModelScope.launch {
            wordRepository.getUserWords().collect { userWords ->
                when (userWords) {
                    is Result.Success -> {
                        val sortedWords = userWords.data
                        _uiState.update { it.copy(userWords = sortedWords, error = null) }
                    }
                    is Result.Error -> _uiState.update { it.copy(userWords = emptyList(), error = userWords.error.asUiText()) }
                }
            }
        }
    }

    fun refreshWordsFromBackend() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = withTimeoutOrNull(10_000L.milliseconds) {
                wordRepository.fetchUserWords()
            }

            if (result == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = WordError.Network.NETWORK_ERROR.asUiText()
                    )
                }
            } else {
                when (result) {
                    is Result.Success -> _uiState.update { it.copy(isLoading = false) }
                    is Result.Error -> _uiState.update { it.copy(error = result.error.asUiText(), isLoading = false) }
                }
            }
        }
    }

    fun getNickname() {
        _uiState.update { it.copy(nickName = tokenClaimsService.getUserClaims().nickname) }
    }

    fun logout() {
        viewModelScope.launch {
            tokenStorage.clearAuthToken()
            tokenStorage.clearRefreshToken()
            _uiState.update { it.copy(isLoggedIn = false) }
        }
    }
}
