package pl.cincior.englishreminder.core.presentation

import pl.cincior.englishreminder.R
import pl.cincior.englishreminder.core.domain.AuthError
import pl.cincior.englishreminder.core.domain.WordError

fun AuthError.asUiText(): UiText {
    return when (this) {
        AuthError.Data.INVALID_CREDENTIALS -> UiText.StringResource(R.string.invalid_credentials)
        AuthError.Data.USER_NOT_FOUND -> UiText.StringResource(R.string.user_not_found)
        AuthError.Data.EMAIL_OR_NICKNAME_TAKEN -> UiText.StringResource(R.string.email_or_nickname_taken)
        AuthError.Data.VALIDATION_ERROR -> UiText.StringResource(R.string.validation_error)
        AuthError.Data.ACCOUNT_NOT_VERIFIED -> UiText.StringResource(R.string.account_not_verified)
        AuthError.Data.USER_ALREADY_VERIFIED -> UiText.StringResource(R.string.user_already_verified)
        AuthError.Data.INVALID_VERIFICATION_CODE -> UiText.StringResource(R.string.invalid_verification_code)
        AuthError.Data.INVALID_RESET_PASSWORD_CODE -> UiText.StringResource(R.string.invalid_reset_password_code)
        AuthError.Data.INVALID_ROLE -> UiText.StringResource(R.string.invalid_role)
        AuthError.Data.SESSION_EXPIRED -> UiText.StringResource(R.string.session_expired)
        AuthError.Data.SERVER_ERROR -> UiText.StringResource(R.string.server_error)
        AuthError.Data.UNKNOWN_ERROR -> UiText.StringResource(R.string.unknown_error)
        AuthError.Network.NETWORK_ERROR -> UiText.StringResource(R.string.network_error)
        AuthError.Network.SERVER_ERROR -> UiText.StringResource(R.string.server_error)
        AuthError.Network.NO_CONNECTION -> UiText.StringResource(R.string.no_connection)
        AuthError.Network.UNKNOWN -> UiText.StringResource(R.string.unknown_error)
    }
}

fun WordError.asUiText(): UiText {
    return when (this) {
        WordError.Data.NO_WORDS_FOUND -> UiText.StringResource(R.string.no_words_found)
        WordError.Data.WORD_NOT_FOUND -> UiText.StringResource(R.string.word_not_found)
        WordError.Data.DUPLICATED_WORD -> UiText.StringResource(R.string.duplicated_word)
        WordError.Data.FAILED_TO_ADD_WORD -> UiText.StringResource(R.string.failed_to_add_word)
        WordError.Data.INVALID_IDENTIFIER -> UiText.StringResource(R.string.invalid_identifier)
        WordError.Data.INVALID_WORD_DATA -> UiText.StringResource(R.string.invalid_word_data)
        WordError.Data.CANNOT_CALCULATE_PREDICTION -> UiText.StringResource(R.string.cannot_calculate_prediction)
        WordError.Data.PREDICTION_SERVICE_ERROR -> UiText.StringResource(R.string.prediction_service_error)
        WordError.Data.CATEGORY_NOT_FOUND -> UiText.StringResource(R.string.category_not_found)
        WordError.Data.WRONG_TIME_FORMAT -> UiText.StringResource(R.string.wrong_time_format)
        WordError.Data.WRONG_TIME -> UiText.StringResource(R.string.wrong_time)
        WordError.Data.FAILED_TO_SCHEDULE_NOTIFICATION -> UiText.StringResource(R.string.failed_to_schedule_notification)
        WordError.Data.UNKNOWN_ERROR -> UiText.StringResource(R.string.unknown_error)
        WordError.Network.NETWORK_ERROR -> UiText.StringResource(R.string.network_error)
        WordError.Network.NO_CONNECTION -> UiText.StringResource(R.string.no_connection)
        WordError.Network.SERVER_ERROR -> UiText.StringResource(R.string.server_error)
        WordError.Network.UNKNOWN -> UiText.StringResource(R.string.unknown_error)
    }
}