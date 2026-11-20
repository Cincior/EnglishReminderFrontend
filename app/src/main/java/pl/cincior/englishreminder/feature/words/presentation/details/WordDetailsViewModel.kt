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
import pl.cincior.englishreminder.core.domain.model.Result
import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.core.presentation.asUiText
import pl.cincior.englishreminder.feature.words.domain.model.WordCategory
import pl.cincior.englishreminder.feature.words.domain.repository.WordRepository
import javax.inject.Inject

@HiltViewModel
class WordDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WordDetailsUiState())
    val uiState: StateFlow<WordDetailsUiState> = _uiState.asStateFlow()

    private val wordId: String = savedStateHandle.get<String>("id") ?: ""

    init {
        if (wordId.isNotBlank()) {
            observeWord()
        } else {
            _uiState.update { it.copy(error = UiText.DynamicString("Nie znaleziono identyfikatora słówka")) }
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
                                editWord = state.editWord.ifEmpty { result.data.word },
                                editTranslation = state.editTranslation.ifEmpty { result.data.translation }
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

    fun onTestAnswerChanged(answer: String) {
        _uiState.update { it.copy(testAnswer = answer, testError = null) }
    }

    fun testWord() {
        val currentWord = _uiState.value.word ?: return
        val answer = _uiState.value.testAnswer
        if (answer.isBlank()) {
            _uiState.update { it.copy(testError = UiText.DynamicString("Wpisz odpowiedź przed sprawdzeniem")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isTesting = true, testError = null, testResult = null) }
            when (val result = wordRepository.testWord(currentWord.id, answer.trim())) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isTesting = false,
                            testResult = result.data,
                            testError = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isTesting = false,
                            testError = result.error.asUiText()
                        )
                    }
                }
            }
        }
    }

    fun onToggleNotificationPicker(show: Boolean) {
        _uiState.update {
            it.copy(
                showNotificationPicker = show,
                notificationError = null,
                notificationSuccessMessage = null
            )
        }
    }

    fun onScheduleDateChanged(dateMillis: Long?) {
        if (dateMillis == null) return
        _uiState.update {
            it.copy(scheduleDateTime = it.scheduleDateTime.copy(date = dateMillis))
        }
    }

    fun onScheduleTimeChanged(hour: Int, minute: Int) {
        _uiState.update {
            it.copy(scheduleDateTime = it.scheduleDateTime.copy(hour = hour, minute = minute))
        }
    }

    fun scheduleNotification() {
        val currentWord = _uiState.value.word ?: return
        val scheduleDateTime = _uiState.value.scheduleDateTime

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSchedulingNotification = true,
                    notificationError = null,
                    notificationSuccessMessage = null
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
                            isSchedulingNotification = false,
                            showNotificationPicker = false,
                            notificationSuccessMessage = UiText.DynamicString(
                                "Zaplanowano powiadomienie na ${scheduleDateTime.toFormattedString()}!"
                            ),
                            notificationError = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSchedulingNotification = false,
                            notificationError = result.error.asUiText()
                        )
                    }
                }
            }
        }
    }

    fun onShowEditDialog(show: Boolean) {
        val currentWord = _uiState.value.word
        _uiState.update {
            it.copy(
                showEditDialog = show,
                editWord = currentWord?.word ?: "",
                editTranslation = currentWord?.translation ?: "",
                editError = null
            )
        }
    }

    fun onEditWordChanged(word: String) {
        _uiState.update { it.copy(editWord = word, editError = null) }
    }

    fun onEditTranslationChanged(translation: String) {
        _uiState.update { it.copy(editTranslation = translation, editError = null) }
    }

    fun updateWord() {
        val currentWord = _uiState.value.word ?: return
        val newWord = _uiState.value.editWord.trim()
        val newTranslation = _uiState.value.editTranslation.trim()

        if (newWord.isBlank() || newTranslation.isBlank()) {
            _uiState.update { it.copy(editError = UiText.DynamicString("Wypełnij słówko i tłumaczenie")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, editError = null) }
            val result = wordRepository.updateWord(currentWord.id, newWord, newTranslation)
            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            showEditDialog = false,
                            editError = null,
                            successMessage = UiText.DynamicString("Słówko zostało pomyślnie zaktualizowane")
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            editError = result.error.asUiText()
                        )
                    }
                }
            }
        }
    }

    fun onShowDeleteConfirmation(show: Boolean) {
        _uiState.update { it.copy(showDeleteConfirmation = show) }
    }

    fun deleteWord() {
        val currentWord = _uiState.value.word ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, error = null) }
            val result = wordRepository.deleteWord(currentWord.id)
            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            showDeleteConfirmation = false,
                            isDeleted = true
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            showDeleteConfirmation = false,
                            error = result.error.asUiText()
                        )
                    }
                }
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null, notificationSuccessMessage = null) }
    }
}
