package pl.cincior.englishreminder.feature.wordDetails.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.domain.repository.WordRepository
import pl.cincior.englishreminder.core.presentation.asUiText
import javax.inject.Inject

@HiltViewModel
class WordDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    wordRepository: WordRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(WordDetailsUiState())
    var uiState: StateFlow<WordDetailsUiState> = _uiState.asStateFlow()

    init {
        val wordId: String = savedStateHandle.get<String>("id") ?: ""
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            //
        }

    }

}