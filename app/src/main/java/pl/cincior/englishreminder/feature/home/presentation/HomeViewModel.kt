package pl.cincior.englishreminder.feature.home.presentation

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cincior.englishreminder.core.data.repository.TokenClaimsService
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.presentation.asUiText
import pl.cincior.englishreminder.core.domain.repository.WordRepository
import pl.cincior.englishreminder.core.room.domain.WordDAO
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val tokenClaimsService: TokenClaimsService
): ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        subscribeUserWords()
        refreshWordsFromBackend()
    }

    private fun subscribeUserWords() {
        viewModelScope.launch {
            _uiState.update { it.copy( isLoading = true, error = null) }
            wordRepository.userWords.collect { userWords ->
                when (userWords) {
                    is Result.Success -> {
                        val sortedWords = userWords.data.sortedByDescending { Instant.parse(it.insertDateTime) }
                        _uiState.update { it.copy(userWords = sortedWords, error = null) }
                    }
                    is Result.Error -> _uiState.update { it.copy(userWords = emptyList(), error = userWords.error.asUiText()) }
                }
            }
        }
    }

    private fun refreshWordsFromBackend() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = wordRepository.getUserWords()) {
                is Result.Success -> _uiState.update { it.copy(userWords = result.data, isLoading = false) }
                is Result.Error -> _uiState.update { it.copy(error = result.error.asUiText(), isLoading = false) }
            }
        }
    }

    fun getLastUserWords() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = wordRepository.getUserWords()) {
                is Result.Error -> _uiState.update { it.copy(error = result.error.asUiText()) }
                is Result.Success -> _uiState.update { it.copy(userWords = result.data) }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun getNickname() {
        _uiState.update { it.copy(nickName = tokenClaimsService.getUserClaims().nickname) }
    }

}