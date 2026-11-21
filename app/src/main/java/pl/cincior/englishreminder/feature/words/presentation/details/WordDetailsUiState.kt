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

    val testState: TestWordState = TestWordState(),
    val notificationState: NotificationState = NotificationState(),
    val editState: EditWordState = EditWordState(),
    val deleteState: DeleteWordState = DeleteWordState()
)

data class TestWordState(
    val answer: String = "",
    val isTesting: Boolean = false,
    val result: TestWordResult? = null,
    val error: UiText? = null
)

data class NotificationState(
    val showPicker: Boolean = false,
    val dateTime: DateTime = DateTime(),
    val isScheduling: Boolean = false,
    val successMessage: UiText? = null,
    val error: UiText? = null
)

data class EditWordState(
    val showDialog: Boolean = false,
    val word: String = "",
    val translation: String = "",
    val isUpdating: Boolean = false,
    val error: UiText? = null
)

data class DeleteWordState(
    val showConfirmation: Boolean = false,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false
)
