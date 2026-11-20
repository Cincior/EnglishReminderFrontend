package pl.cincior.englishreminder.feature.words.presentation.add

import pl.cincior.englishreminder.core.domain.model.DateTime
import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.feature.words.domain.model.WordCategory

data class AddWordUiState(
    val word: String = "",
    val translation: String = "",
    val forceDuplicate: Boolean = false,
    val selectedCategory: WordCategory = WordCategory.LEARN_NOW,
    val scheduleNotification: Boolean = false,
    val isLoading: Boolean = false,
    val isWordAdded: Boolean = false,
    val isNotificationCreated: Boolean = false,
    val isDateSelected: Boolean = false,
    val isTimeSelected: Boolean = false,
    val scheduleDateTime: DateTime = DateTime(),
    val error: UiText? = null
)
