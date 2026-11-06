package pl.cincior.englishreminder.core.presentation

import pl.cincior.englishreminder.R
import pl.cincior.englishreminder.core.domain.LoginError
import pl.cincior.englishreminder.core.domain.WordError

fun LoginError.asUiText(): UiText {
    return when (this) {
        LoginError.Data.INVALID_CREDENTIALS -> UiText.StringResource(R.string.invalid_credentials)
        LoginError.Data.USER_NOT_FOUND -> UiText.StringResource(R.string.user_not_found)
        LoginError.Data.EMAIL_OR_NICKNAME_TAKEN -> UiText.StringResource(R.string.email_or_nickname_taken)
        LoginError.Data.VALIDATION_ERROR -> UiText.StringResource(R.string.validation_error)
        LoginError.Data.ACCOUNT_NOT_VERIFIED -> UiText.StringResource(R.string.account_not_verified)
        LoginError.Data.USER_ALREADY_VERIFIED -> UiText.StringResource(R.string.user_already_verified)
        LoginError.Data.INVALID_VERIFICATION_CODE -> UiText.StringResource(R.string.invalid_verification_code)
        LoginError.Data.INVALID_RESET_PASSWORD_CODE -> UiText.StringResource(R.string.invalid_reset_password_code)
        LoginError.Data.INVALID_ROLE -> UiText.StringResource(R.string.invalid_role)
        LoginError.Data.SESSION_EXPIRED -> UiText.StringResource(R.string.session_expired)
        LoginError.Data.SERVER_ERROR -> UiText.StringResource(R.string.server_error)
        LoginError.Data.UNKNOWN_ERROR -> UiText.StringResource(R.string.unknown_error)
        LoginError.Network.NETWORK_ERROR -> UiText.StringResource(R.string.network_error)
        LoginError.Network.SERVER_ERROR -> UiText.StringResource(R.string.server_error)
        LoginError.Network.NO_CONNECTION -> UiText.StringResource(R.string.no_connection)
        LoginError.Network.UNKNOWN -> UiText.StringResource(R.string.unknown_error)
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