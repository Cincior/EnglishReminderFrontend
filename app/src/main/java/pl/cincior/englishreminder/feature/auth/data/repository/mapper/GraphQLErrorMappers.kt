package pl.cincior.englishreminder.feature.auth.data.repository.mapper

import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloHttpException
import com.apollographql.apollo.exception.ApolloNetworkException
import pl.cincior.englishreminder.core.domain.AuthError

fun String?.toAuthError(): AuthError = when (this) {
    "USER_NOT_FOUND" -> AuthError.Data.USER_NOT_FOUND
    "EMAIL_OR_NICKNAME_ALREADY_TAKEN" -> AuthError.Data.EMAIL_OR_NICKNAME_TAKEN
    "VALIDATION_ERROR" -> AuthError.Data.VALIDATION_ERROR
    "LOGIN_FAILED", "INVALID_CREDENTIALS" -> AuthError.Data.INVALID_CREDENTIALS
    "UNVERIFIED_USER" -> AuthError.Data.ACCOUNT_NOT_VERIFIED
    "USER_ALREADY_VERIFIED" -> AuthError.Data.USER_ALREADY_VERIFIED
    "INVALID_VERIFICATION_CODE" -> AuthError.Data.INVALID_VERIFICATION_CODE
    "INVALID_RESET_PASSWORD_CODE" -> AuthError.Data.INVALID_RESET_PASSWORD_CODE
    "INVALID_ROLE" -> AuthError.Data.INVALID_ROLE
    "INVALID_REFRESH_TOKEN", "REFRESH_TOKEN_EXPIRED" -> AuthError.Data.SESSION_EXPIRED
    "INTERNAL_SERVER_ERROR" -> AuthError.Data.SERVER_ERROR
    else -> AuthError.Data.UNKNOWN_ERROR
}

fun ApolloException.toAuthNetworkError(): AuthError.Network = when (this) {
    is ApolloNetworkException -> AuthError.Network.NO_CONNECTION
    is ApolloHttpException -> if (statusCode >= 500) AuthError.Network.SERVER_ERROR
        else AuthError.Network.UNKNOWN
    else                      -> AuthError.Network.UNKNOWN
}