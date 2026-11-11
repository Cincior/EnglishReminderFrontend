package pl.cincior.englishreminder.feature.wordList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.domain.repository.WordRepository
import pl.cincior.englishreminder.core.presentation.asUiText
import javax.inject.Inject

@HiltViewModel
class WordListViewModel @Inject constructor(
    private val wordRepository: WordRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(WordListUiState())
    val uiState: StateFlow<WordListUiState> = _uiState

    init {
        subscribeUserWords()
        refreshWordsFromBackend()
    }

    private fun subscribeUserWords() {
        viewModelScope.launch {
            wordRepository.userWords.collect { userWords ->
                when (userWords) {
                    is Result.Success -> _uiState.update { it.copy(wordList = userWords.data, error = null) }
                    is Result.Error -> _uiState.update { it.copy(wordList = emptyList(), error = userWords.error.asUiText()) }
                }
            }
        }
    }

    private fun refreshWordsFromBackend() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = wordRepository.getUserWords()) {
                is Result.Success -> _uiState.update { it.copy(wordList = result.data, isLoading = false) }
                is Result.Error -> _uiState.update { it.copy(error = result.error.asUiText(), isLoading = false) }
            }
        }
    }
}