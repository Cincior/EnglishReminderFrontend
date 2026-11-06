package pl.cincior.englishreminder.feature.home.presentation

import androidx.compose.runtime.mutableStateOf
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
import pl.cincior.englishreminder.core.presentation.asUiText
import pl.cincior.englishreminder.feature.home.domain.model.UserWord
import pl.cincior.englishreminder.feature.home.domain.repository.WordRepository
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wordRepository: WordRepository
): ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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

}