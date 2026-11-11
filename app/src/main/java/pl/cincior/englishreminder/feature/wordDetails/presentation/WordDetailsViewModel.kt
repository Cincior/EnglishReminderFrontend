package pl.cincior.englishreminder.feature.wordDetails.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WordDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val wordId: String = savedStateHandle.get<String>("id") ?: ""
}