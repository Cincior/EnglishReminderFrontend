package pl.cincior.englishreminder.feature.words.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cincior.englishreminder.R
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.domain.service.TextToSpeechService
import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.core.presentation.asUiText
import pl.cincior.englishreminder.feature.words.domain.model.WordCategory
import pl.cincior.englishreminder.feature.words.domain.repository.WordRepository
import javax.inject.Inject

@HiltViewModel
class WordDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val textToSpeechService: TextToSpeechService,
    private val wordRepository: WordRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WordDetailsUiState())
    val uiState: StateFlow<WordDetailsUiState> = _uiState.asStateFlow()

    private val wordId: String = savedStateHandle.get<String>("id") ?: ""

    init {
        if (wordId.isNotBlank()) {
            observeWord()
        } else {
            _uiState.update { it.copy(error = UiText.StringResource(R.string.error_word_id_not_found)) }
        }
    }

    private fun observeWord() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            wordRepository.getUserWord(wordId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                word = result.data,
                                isLoading = false,
                                error = null,
                                editState = state.editState.copy(
                                    word = state.editState.word.ifEmpty { result.data.word },
                                    translation = state.editState.translation.ifEmpty { result.data.translation }
                                )
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = result.error.asUiText()
                            )
                        }
                    }
                }
            }
        }
    }

    fun onToggleTranslationVisibility() {
        _uiState.update { it.copy(isTranslationVisible = !it.isTranslationVisible) }
    }

    fun onCategorySelected(category: WordCategory) {
        val currentWord = _uiState.value.word ?: return
        viewModelScope.launch {
            val result = wordRepository.setCategory(currentWord.id, category)
            if (result is Result.Error) {
                _uiState.update { it.copy(error = result.error.asUiText()) }
            }
        }
    }

    // --- NOTIFICATION ACTIONS ---
    fun onToggleNotificationPicker(show: Boolean) {
        _uiState.update {
            it.copy(
                notificationState = it.notificationState.copy(
                    showPicker = show,
                    error = null,
                    successMessage = null
                )
            )
        }
    }

    fun onScheduleDateChanged(dateMillis: Long?) {
        if (dateMillis == null) return
        _uiState.update {
            it.copy(
                notificationState = it.notificationState.copy(
                    dateTime = it.notificationState.dateTime.copy(date = dateMillis)
                )
            )
        }
    }

    fun onScheduleTimeChanged(hour: Int, minute: Int) {
        _uiState.update {
            it.copy(
                notificationState = it.notificationState.copy(
                    dateTime = it.notificationState.dateTime.copy(hour = hour, minute = minute)
                )
            )
        }
    }

    fun scheduleNotification() {
        val currentWord = _uiState.value.word ?: return
        val scheduleDateTime = _uiState.value.notificationState.dateTime

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    notificationState = it.notificationState.copy(
                        isScheduling = true,
                        error = null,
                        successMessage = null
                    )
                )
            }
            val result = wordRepository.scheduleNotification(
                wordId = currentWord.id,
                scheduleTime = scheduleDateTime.toISOString()
            )
            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            notificationState = it.notificationState.copy(
                                isScheduling = false,
                                showPicker = false,
                                successMessage = UiText.StringResource(
                                    R.string.notification_scheduled_success,
                                    arrayOf(scheduleDateTime.toFormattedString())
                                ),
                                error = null
                            )
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            notificationState = it.notificationState.copy(
                                isScheduling = false,
                                error = result.error.asUiText()
                            )
                        )
                    }
                }
            }
        }
    }

    // --- EDIT WORD ACTIONS ---
    fun onShowEditDialog(show: Boolean) {
        val currentWord = _uiState.value.word
        _uiState.update {
            it.copy(
                editState = it.editState.copy(
                    showDialog = show,
                    word = currentWord?.word ?: "",
                    translation = currentWord?.translation ?: "",
                    error = null
                )
            )
        }
    }

    fun onEditWordChanged(word: String) {
        _uiState.update { it.copy(editState = it.editState.copy(word = word, error = null)) }
    }

    fun onEditTranslationChanged(translation: String) {
        _uiState.update { it.copy(editState = it.editState.copy(translation = translation, error = null)) }
    }

    fun updateWord() {
        val currentWord = _uiState.value.word ?: return
        val newWord = _uiState.value.editState.word.trim()
        val newTranslation = _uiState.value.editState.translation.trim()

        if (newWord.isBlank() || newTranslation.isBlank()) {
            _uiState.update { it.copy(editState = it.editState.copy(error = UiText.StringResource(R.string.error_fill_word_and_translation_edit))) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(editState = it.editState.copy(isUpdating = true, error = null)) }
            val result = wordRepository.updateWord(currentWord.id, newWord, newTranslation)
            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            successMessage = UiText.StringResource(R.string.word_updated_success),
                            editState = it.editState.copy(
                                isUpdating = false,
                                showDialog = false,
                                error = null
                            )
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            editState = it.editState.copy(
                                isUpdating = false,
                                error = result.error.asUiText()
                            )
                        )
                    }
                }
            }
        }
    }

    // --- DELETE WORD ACTIONS ---
    fun onShowDeleteConfirmation(show: Boolean) {
        _uiState.update { it.copy(deleteState = it.deleteState.copy(showConfirmation = show)) }
    }

    fun deleteWord() {
        val currentWord = _uiState.value.word ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(deleteState = it.deleteState.copy(isDeleting = true), error = null) }
            val result = wordRepository.deleteWord(currentWord.id)
            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            deleteState = it.deleteState.copy(
                                isDeleting = false,
                                showConfirmation = false,
                                isDeleted = true
                            )
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            deleteState = it.deleteState.copy(
                                isDeleting = false,
                                showConfirmation = false
                            ),
                            error = result.error.asUiText()
                        )
                    }
                }
            }
        }
    }

    fun pronounceWord() {
        val word = _uiState.value.word?.word?.takeIf(String::isNotBlank) ?: return
        textToSpeechService.speak(word, ::showSpeechError)
    }

    private fun showSpeechError(message: String) {
        _uiState.update { it.copy(error = UiText.DynamicString(message)) }
    }

    override fun onCleared() {
        textToSpeechService.stop()
        super.onCleared()
    }

    fun clearSuccessMessage() {
        _uiState.update {
            it.copy(
                successMessage = null,
                notificationState = it.notificationState.copy(successMessage = null)
            )
        }
    }
}
