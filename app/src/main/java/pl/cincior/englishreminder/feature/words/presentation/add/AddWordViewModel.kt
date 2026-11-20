package pl.cincior.englishreminder.feature.words.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.core.presentation.asUiText
import pl.cincior.englishreminder.feature.words.domain.model.WordCategory
import pl.cincior.englishreminder.feature.words.domain.repository.WordRepository
import javax.inject.Inject

@HiltViewModel
class AddWordViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddWordUiState())
    val uiState: StateFlow<AddWordUiState> = _uiState.asStateFlow()

    fun onWordChanged(newWord: String) {
        _uiState.update { it.copy(word = newWord, error = null) }
    }

    fun onTranslationChanged(newTranslation: String) {
        _uiState.update { it.copy(translation = newTranslation, error = null) }
    }

    fun onForceDuplicateChanged(force: Boolean) {
        _uiState.update { it.copy(forceDuplicate = force) }
    }

    fun onCategorySelected(category: WordCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onScheduleNotificationChanged(schedule: Boolean) {
        _uiState.update { it.copy(scheduleNotification = schedule) }
    }

    fun onScheduleTimeChanged(hour: Int, minute: Int) {
        _uiState.update { it.copy(scheduleDateTime = it.scheduleDateTime.copy(hour = hour, minute = minute)) }
    }

    fun onScheduleDateChanged(dateMilis: Long?) {
        if (dateMilis == null) {
            return
        }
        _uiState.update { it.copy(scheduleDateTime = it.scheduleDateTime.copy(date = dateMilis)) }
    }

    fun addWord() {
        val currentState = _uiState.value
        if (currentState.word.isBlank() || currentState.translation.isBlank()) {
            _uiState.update {
                it.copy(error = UiText.DynamicString("Wypełnij słowo oraz tłumaczenie"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val addResult = wordRepository.addWord(
                word = currentState.word.trim(),
                translation = currentState.translation.trim(),
                forceDuplicate = currentState.forceDuplicate,
                category = currentState.selectedCategory
            )

            when (addResult) {
                is Result.Success -> {
                    val wordData = addResult.data
                    if (wordData.created && wordData.wordId != null) {

                        if (currentState.scheduleNotification) {
                            when (
                                val notificationResult = wordRepository.scheduleNotification(
                                    wordId = wordData.wordId,
                                    scheduleTime = currentState.scheduleDateTime.toISOString())
                            ) {
                                is Result.Success -> {
                                    wordRepository.fetchUserWords()
                                    _uiState.update { it.copy(
                                        isLoading = false,
                                        error = null,
                                        isNotificationCreated = true,
                                        isWordAdded = true)
                                    }
                                }
                                is Result.Error -> {
                                    _uiState.update { it.copy(
                                        isLoading = false,
                                        error = notificationResult.error.asUiText(),
                                        isWordAdded = true,
                                        isNotificationCreated = false) }
                                }
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = null,
                                    isWordAdded = true,
                                    isNotificationCreated = true
                                )
                            }
                        }
                        wordRepository.fetchUserWords()
                    } else if (!wordData.created && wordData.existingWordId != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = UiText.DynamicString("Słowo już istnieje w bazie. Zaznacz 'Wymuś duplikat', jeśli chcesz dodać je ponownie.")
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = UiText.DynamicString("Nie udało się dodać słówka")) }
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = addResult.error.asUiText()) }
                }
            }
        }
    }
}
