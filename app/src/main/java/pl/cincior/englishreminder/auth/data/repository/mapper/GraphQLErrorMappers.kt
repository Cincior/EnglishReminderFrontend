package pl.cincior.englishreminder.auth.data.repository.mapper

import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloHttpException
import com.apollographql.apollo.exception.ApolloNetworkException
import pl.cincior.englishreminder.core.domain.LoginError

fun String?.toLoginError(): LoginError = when (this) {
    "USER_NOT_FOUND" -> LoginError.Data.USER_NOT_FOUND
    "EMAIL_OR_NICKNAME_ALREADY_TAKEN" -> LoginError.Data.EMAIL_OR_NICKNAME_TAKEN
    "VALIDATION_ERROR" -> LoginError.Data.VALIDATION_ERROR
    "LOGIN_FAILED", "INVALID_CREDENTIALS" -> LoginError.Data.INVALID_CREDENTIALS
    "UNVERIFIED_USER" -> LoginError.Data.ACCOUNT_NOT_VERIFIED
    "USER_ALREADY_VERIFIED" -> LoginError.Data.USER_ALREADY_VERIFIED
    "INVALID_VERIFICATION_CODE" -> LoginError.Data.INVALID_VERIFICATION_CODE
    "INVALID_RESET_PASSWORD_CODE" -> LoginError.Data.INVALID_RESET_PASSWORD_CODE
    "INVALID_ROLE" -> LoginError.Data.INVALID_ROLE
    "INVALID_REFRESH_TOKEN", "REFRESH_TOKEN_EXPIRED" -> LoginError.Data.SESSION_EXPIRED
    "INTERNAL_SERVER_ERROR" -> LoginError.Data.SERVER_ERROR
    else -> LoginError.Data.UNKNOWN_ERROR
}

fun ApolloException.toLoginNetworkError(): LoginError.Network = when (this) {
    is ApolloNetworkException -> LoginError.Network.NO_CONNECTION
    is ApolloHttpException -> if (statusCode >= 500) LoginError.Network.SERVER_ERROR
        else LoginError.Network.UNKNOWN
    else                      -> LoginError.Network.UNKNOWN
}