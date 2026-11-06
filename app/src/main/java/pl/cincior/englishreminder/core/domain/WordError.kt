package pl.cincior.englishreminder.core.domain

sealed interface WordError: RootError {
    enum class Data : WordError {
        NO_WORDS_FOUND,
        WORD_NOT_FOUND,
        DUPLICATED_WORD,
        FAILED_TO_ADD_WORD,
        INVALID_IDENTIFIER,
        INVALID_WORD_DATA,
        CANNOT_CALCULATE_PREDICTION,
        PREDICTION_SERVICE_ERROR,
        CATEGORY_NOT_FOUND,
        WRONG_TIME_FORMAT,
        WRONG_TIME,
        FAILED_TO_SCHEDULE_NOTIFICATION,
        UNKNOWN_ERROR
    }

    enum class Network : WordError {
        NETWORK_ERROR,
        NO_CONNECTION,
        SERVER_ERROR,
        UNKNOWN
    }
}