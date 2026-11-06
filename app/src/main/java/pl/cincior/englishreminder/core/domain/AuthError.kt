package pl.cincior.englishreminder.core.domain

sealed interface AuthError : RootError {
    enum class Data : AuthError {
        INVALID_CREDENTIALS,
        USER_NOT_FOUND,
        EMAIL_OR_NICKNAME_TAKEN,
        VALIDATION_ERROR,
        ACCOUNT_NOT_VERIFIED,
        USER_ALREADY_VERIFIED,
        INVALID_VERIFICATION_CODE,
        INVALID_RESET_PASSWORD_CODE,
        INVALID_ROLE,
        SESSION_EXPIRED,
        SERVER_ERROR,
        UNKNOWN_ERROR
    }
    enum class Network : AuthError {
        NETWORK_ERROR,
        NO_CONNECTION,
        SERVER_ERROR,
        UNKNOWN
    }
}