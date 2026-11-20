package pl.cincior.englishreminder.feature.words.presentation.details

import pl.cincior.englishreminder.core.domain.model.DateTime
import pl.cincior.englishreminder.core.presentation.UiText
import pl.cincior.englishreminder.feature.words.domain.model.TestWordResult
import pl.cincior.englishreminder.feature.words.domain.model.UserWord

data class WordDetailsUiState(
    val word: UserWord? = null,
    val isLoading: Boolean = false,
    val isTranslationVisible: Boolean = false,
    val error: UiText? = null,
    val successMessage: UiText? = null,

    // Test Word ("Sprawdź się")
    val testAnswer: String = "",
    val isTesting: Boolean = false,
    val testResult: TestWordResult? = null,
    val testError: UiText? = null,

    // Notification Scheduling
    val showNotificationPicker: Boolean = false,
    val scheduleDateTime: DateTime = DateTime(),
    val isSchedulingNotification: Boolean = false,
    val notificationSuccessMessage: UiText? = null,
    val notificationError: UiText? = null,

    // Editing Word
    val showEditDialog: Boolean = false,
    val editWord: String = "",
    val editTranslation: String = "",
    val isUpdating: Boolean = false,
    val editError: UiText? = null,

    // Deleting Word
    val showDeleteConfirmation: Boolean = false,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false
)
